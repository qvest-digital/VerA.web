/**
 * Created by mley on 21.07.14.
 */

var onlineRegApp = angular.module('onlineRegApp', [ 'ngRoute', 'ui.bootstrap', 'pascalprecht.translate',
                                                    'flow', 'flow.img', 'flow.init', 'flow.provider' ]);

onlineRegApp.run(function ($rootScope) {
    $rootScope.parseDate = function (dt) {
            return moment(dt).toDate();
    };

    $rootScope.isUserLoged = function () {
        return $rootScope.user_logged_in != null;
    }

    $rootScope.cleanMessages = function() {
        $rootScope.button = false;
        $rootScope.status = null;
        $rootScope.previousMessage = null;
        $rootScope.messageContent = null;
    }

    $rootScope.cleanImageControls = function () {
        $rootScope.correctImageFormat = true;
        $rootScope.correctImageSize = true;
    }
    //Only required for LoginController
    setStatus = null;
});

onlineRegApp.config(function ($routeProvider, $translateProvider) {
    $routeProvider.when('/login', {
        templateUrl: 'partials/login.html',
        controller: 'LoginController'
    }).when('/login/:delegation', {
        templateUrl: 'partials/login.html',
        controller: 'LoginController'
    }).when('/event', {
        templateUrl: 'partials/event.html',
        controller: 'EventController'
    }).when('/register/:eventId', {
        templateUrl: 'partials/register.html',
        controller: 'RegisterController'
    }).when('/register/:eventId/:noLoginRequiredUUID', {
            templateUrl: 'partials/register.html',
            controller: 'RegisterController'
    }).when('/register/', {
        templateUrl: 'partials/register_user.html',
        controller: 'RegisterUserController'
    }).when('/veranstaltungen', {
        templateUrl: 'partials/meine-veranstaltungen.html',
        controller: 'VeranstaltungsController'
    }).when('/kontaktdaten' , {
        templateUrl: 'partials/kontaktdaten.html',
        controller: 'KontaktdatenController',
    }).when('/delegation/:uuid', {
      templateUrl: 'partials/delegation.html',
      controller: 'DelegationController'
    }).when('/media/:uuid', {
      templateUrl: 'partials/media.html',
      controller: 'MediaController'
    }).when('/freevisitors/:uuid', {
        template: "",
        controller: 'FreeVisitorController'
    }).when('/freevisitors/:uuid/:noLoginRequiredUUID', {
        template: "",
        controller: 'FreeVisitorController'
    }).when('/update/:eventId', {
        templateUrl: 'partials/update.html',
        controller: 'UpdateController'
    }).when('/reset/password/:uuid', {
        templateUrl: 'partials/reset_password.html',
        controller: 'ResetPasswordController'
    }).when('/page_not_found', {
        templateUrl: 'partials/page_not_found.html',
        controller: 'PageNotFoundController'
    }).otherwise({
      redirectTo: '/event'
    })

    $translateProvider.useStaticFilesLoader({
        prefix: 'languages/lang-',
        suffix: '.json'
    });

    $translateProvider.preferredLanguage('de_DE');

});


onlineRegApp.config(['flowFactoryProvider', function (flowFactoryProvider) {
    flowFactoryProvider.defaults = {
        permanentErrors: [404, 500, 501],
        maxChunkRetries: 1,
        chunkRetryInterval: 5000,
        simultaneousUploads: 4,
        singleFile: true
    };
}]);

onlineRegApp.directive('equals', function() {
    return {
        restrict: 'A',
        require: '?ngModel',
        link: function(scope, elem, attrs, ngModel) {
            if (!ngModel) return;

            scope.$watch(attrs.ngModel, function() {
                validate();
            });

            attrs.$observe('equals', function(val) {
                validate();
            });

            var validate = function() {
                var val1 = ngModel.$viewValue;
                var val2 = attrs.equals;

                ngModel.$setValidity('equals', !val1 || !val2 || val1 === val2);
            }
        }
    }
});

onlineRegApp.controller('PageNotFoundController', function ($scope, $translate) {
    $translate('GENERIC_PAGE_NOT_FOUND').then(function (text) {
        $scope.error = text;
    });
});

onlineRegApp.controller('LanguageSelectController', function ($scope, $translate) {
  $scope.langKey = 'de_DE';
  $scope.changeLang = function (key) {
    $translate.use(key).then(function (key) {
    }, function (key) {
    });
  };
});

onlineRegApp.controller('ResetPasswordController', function($http, $scope, $routeParams, $location, $rootScope, $translate) {
    $scope.resetPassword = function() {
        if ($scope.resetPasswordForm.password.$viewValue != $scope.resetPasswordForm.passwordRepeat.$viewValue) {
            $translate('REGISTER_USER_MESSAGE_PASSWORD_REPEAT_ERROR').then(function (text) {
                $scope.error = text;
            });
        } else {
            $scope.error = null;

            $http({
                method: 'POST',
                url: 'api/reset/password/' + $routeParams.uuid,
                headers: {"Content-Type" : undefined},
                data: $.param({
                    password: $scope.resetPasswordForm.password.$viewValue
                })
            }).success(function () {
                $translate('USER_PASSWORD_CHANGE_SUCCESS_MESSAGE').then(function (text) {
                    $rootScope.success = text;
                });
                $location.path('/event');
            });
        }
    }
});

onlineRegApp.controller('FreeVisitorController', function($http, $scope, $location, $routeParams, $translate) {
    if ($routeParams.noLoginRequiredUUID != null) {
        var freeVisitorsUrl = 'api/freevisitors/'+ $routeParams.uuid + '/' + $routeParams.noLoginRequiredUUID
    } else {
        var freeVisitorsUrl = 'api/freevisitors/'+ $routeParams.uuid
    }
    $http({
        method: 'GET',
        url: freeVisitorsUrl
    }).success(function (result) {
        if(result.status != 'ERROR') {
            $location.path('/register/' + result.status);
        } else {
            $location.path('/page_not_found');
        }
    }).error(function (data, status, headers, config) {
        // FIXME Wrong message?
        $translate('GENERIC_MISSING_GENDER_MESSAGE').then(function (text) {
            $scope.error = text;
        });
    });
});

onlineRegApp.controller('UpdateController', function($scope, $rootScope, $location, $routeParams, $http, $translate) {
    if ($rootScope.user_logged_in == null) {

        $scope.setNextPage('register/' + $routeParams.eventId);
        $location.path('/login');
    } else {
        $scope.acceptanceOptions = [
            {id: 0, name:"USER_EVENTS_STATUS_TYPE_OPEN"},
            {id: 1, name:"USER_EVENTS_STATUS_TYPE_ACCEPTANCE"},
            {id: 2, name:"USER_EVENTS_STATUS_TYPE_REFUSE"}
        ];

        $http.get('api/update/' + $routeParams.eventId).success(function (result) {
            $scope.event = result;
            $scope.acceptance = $scope.acceptanceOptions[$scope.event.status];
            $scope.noteToHost = $scope.event.message;
        }).error(function (data, status, headers, config) {
            $translate('USER_EVENTS_STATUS_CHANGED_ERROR_MESSAGE').then(function (text) {
                $scope.error = text;
            });
        });

        $http.get('api/update/isreserve/' + $routeParams.eventId + '/' + $rootScope.user_logged_in).success(function (result) {
            $scope.event = result;
            if (result) {
                $translate('REGISTER_USER_MESSAGE_TO_RESERVE_LIST').then(function (text) {
                    $scope.error = text;
                });
            }
            else $scope.error = null;
        }).error(function (data, status, headers, config) {
            $translate('USER_EVENTS_STATUS_CHANGED_ERROR_MESSAGE').then(function (text) {
                $scope.error = text;
            });
        });

        $scope.update = function () {
            $http({
                method: 'POST',
                url: 'api/update/' + $routeParams.eventId + '/update',
                headers: {"Content-Type" : undefined},
                data: $.param({
                        notehost: $scope.noteToHost,
                        invitationstatus: $scope.acceptance.id
                })
            }).success(function (result) {
                if (result.status === 'OK') {
                    $translate(['USER_EVENTS_STATUS_CHANGED_SUCCESSFUL_MESSAGE_PART_ONE','USER_EVENTS_STATUS_CHANGED_SUCCESSFUL_MESSAGE_PART_TWO']).then(function (translations) {
                        $rootScope.previousMessage = translations['USER_EVENTS_STATUS_CHANGED_SUCCESSFUL_MESSAGE_PART_ONE'] + " \"" + $scope.event.shortname + "\" " + translations['USER_EVENTS_STATUS_CHANGED_SUCCESSFUL_MESSAGE_PART_TWO'];
                    });

                    $scope.setNextPage('veranstaltungen');
                    $location.path($scope.nextPage);
                } else if (result.status === 'NOT_REGISTERED') {
                    $translate('USER_EVENTS_STATUS_CHANGED_ERROR_MESSAGE').then(function (text) {
                        $scope.error = text;
                    });
                }
            });
        }
    }
});

onlineRegApp.controller('MediaController', function ($scope, $http, $rootScope, $location, $routeParams, $translate) {
    $scope.success = null;
    $scope.error = null;

    $rootScope.cleanMessages();

    $scope.genderOptions = [
        {id: 0, name:"GENERIC_PLEASE_SELECT"},
        {id: 1, name:"GENERIC_GENDER_MALE"},
        {id: 2, name:"GENERIC_GENDER_FEMALE"}
    ];

    $scope.gender = $scope.genderOptions[0];

    if ($rootScope.user_logged_in == null) {
        $http.get('api/media/' + $routeParams.uuid).then(function(presentPersons) {
            $scope.presentPersons = presentPersons.data;
        });

        $scope.register_pressevertreter = function () {

            if ($scope.gender.id == 0) {
                $translate('GENERIC_MISSING_GENDER_MESSAGE').then(function (text) {
                    $scope.error = text;
                });
            } else if ($scope.gender.id == 1 || $scope.gender.id == 2){
                if ($scope.lastname != null && $scope.firstname != null && $scope.email != null && $scope.address != null && $scope.plz != null && $scope.city != null && $scope.country != null) {
                    $translate('GENERIC_ERROR').then(function (text) {
                        var ERROR_TEXT = text;
                    });
                    $scope.button = true;
                    $http({
                        method: 'POST',
                        url: 'api/media/' + $routeParams.uuid + '/register',
                        headers: {"Content-Type" : undefined},
                        data: $.param({
                                nachname: $scope.lastname,
                                vorname: $scope.firstname,
                                gender: $scope.gender.label,
                                email: $scope.email,
                                address: $scope.address,
                                plz: $scope.plz,
                                city: $scope.city,
                                country: $scope.country
                        })
                    }).success(function (result) {
                        if (result.status === 'NO_EVENT_DATA') {
                            $translate('GENERIC_MESSAGE_EVENT_DOESNT_EXISTS').then(function (text) {
                                $scope.error = text;
                            });
                            $scope.success = null;
                        }  else if (result.status === 'WRONG_EVENT') {
                            $translate('GENERIC_MESSAGE_EVENT_DOESNT_EXISTS').then(function (text) {
                                $scope.error = text;
                            });
                            $scope.success = null;
                        } else if (result.status === 'OK') {
                            $translate('MEDIA_REPRESEINTATIVES_REGISTER_SUCCESSFUL_MESSAGE').then(function (text) {
                                $scope.success = text;
                            });
                            $http.get('api/delegation/' + $routeParams.uuid).then(function(presentPersons) {
                                $scope.presentPersons = presentPersons.data;
                            });

                            $scope.lastname = null;
                            $scope.firstname = null;
                            $scope.gender = $scope.genderOptions[0];
                            $scope.email = null;
                            $scope.address = null;
                            $scope.plz = null;
                            $scope.city = null;
                            $scope.country = null;
                        } else {
                            $scope.error = ERROR_TEXT;
                        }
                        $scope.button = false;
                    }).error(function (data, status, headers, config) {
                        $scope.error = ERROR_TEXT;
                        $scope.button = false;
                    });
                } else {
                    $translate('GENERIC_MESSAGE_FILL_IN_ALL_FIELDS').then(function (text) {
                        $scope.error = text;
                    });
                }
            }
        }
    }
    else {
        $location.path('/veranstaltungen');
    }
});

onlineRegApp.controller('DelegationController', function ($scope, $http, $rootScope, $location, $routeParams, $translate, $route, $timeout, $anchorScroll) {
    $rootScope.cleanMessages();
    $rootScope.cleanImageControls();

    if($rootScope.delegationSuccessMessage) {
        $translate('DELEGATION_MESSAGE_DELEGATION_DATA_SAVED_SUCCESSFUL').then(function (text) {
            $scope.success = text;
        });

        $rootScope.delegationSuccessMessage = false;
    }

    $scope.$on('flow::fileAdded', function (event, $flow, flowFile) {

        $scope.loadingphoto = true;
        $scope.error = null;

        if ($scope.success != null && $scope.success != false && $rootScope.delegationSuccessMessage != null && $rootScope.delegationSuccessMessage != false) {
            $rootScope.cleanMessages();
        }

        var extensionTypes = new Array('jpeg','jpg','png');
        var fileExtension = flowFile.name.split('.');
        fileExtension = fileExtension[fileExtension.length - 1];
        var extensionStatus = false;

        for (var i in extensionTypes) {
            if (extensionTypes[i] == fileExtension) {
                extensionStatus = true;
            }
        }

        if(!extensionStatus) {
            $scope.success = null;
            $rootScope.cleanMessages();

            $rootScope.correctImageFormat = false;
        }
        else {
            $rootScope.correctImageFormat = true;
        }

        $scope.imageError = undefined;
        var fileReader = new FileReader();

        fileReader.onload = function (event) {

            $scope.$apply(function () {
              $timeout(function(){

                    var img = new Image();
                    img.src = event.target.result;

                        if ($rootScope.correctImageFormat) {

                            // was cached 186x245
                            if (img.complete) {
                                if (img.width == 186 && img.height == 245) {
                                $timeout(function(){
                                    $scope.image = event.target.result;
                                    $scope.loadingphoto = false;
                                    $scope.success = null;
                                    $scope.error = null;
                                }, 500);
                                }
                                else {
                                    if (!$rootScope.correctImageFormat) {
                                        $scope.success = null;
                                        $scope.error = null;

                                        $rootScope.cleanMessages();

                                        $translate('GENERIC_IMAGE_FORMAT_FALSE').then(function (text) {
                                            $scope.error = text;
                                        });
                                    }
                                    else {
                                        $scope.success = null;
                                        $scope.error = null;

                                        $rootScope.cleanMessages();

                                        $translate('GENERIC_IMAGE_SIZE_FALSE').then(function (text) {
                                            $scope.error = text;
                                        });
                                    }
                                }
                            }
                            else {
                                img.onload = function() {
                                   if (img.width == 186 && img.height == 245) {
                                       $timeout(function(){
                                        $scope.image = event.target.result;
                                        $scope.loadingphoto = false;
                                        $scope.success = null;
                                        $scope.error = null;
                                       }, 500);
                                   }
                                   else {
                                       if (!$rootScope.correctImageFormat) {
                                           $scope.success = null;
                                           $scope.error = null;

                                           $rootScope.cleanMessages();
                                           $translate('GENERIC_IMAGE_FORMAT_FALSE').then(function (text) {
                                               $scope.error = text;
                                           });
                                       }
                                       else {
                                           $scope.success = null;
                                           $scope.error = null;

                                           $rootScope.cleanMessages();

                                           $translate('GENERIC_IMAGE_SIZE_FALSE').then(function (text) {
                                               $scope.error = text;
                                           });
                                       }
                                   }
                                }
                            }
                        }
                        else {
                               $scope.success = null;
                               $scope.error = null;

                               $rootScope.cleanMessages();
                               $translate('GENERIC_IMAGE_FORMAT_FALSE').then(function (text) {
                                   $scope.error = text;
                               });
                        }

                        $scope.loadingphoto = false;
                }, 600);
            });
        };
        fileReader.readAsDataURL(flowFile.file);
    });

    if ($rootScope.user_logged_in == null) {
        $scope.setNextPage('delegation/' + $routeParams.uuid);
        lastPageRegisterPath = $location.path();
        $location.path('/login/' + $routeParams.uuid);
    } else {
        $scope.genderOptions = [
            {id: 0, name:"GENERIC_PLEASE_SELECT"},
            {id: 1, name:"GENERIC_GENDER_MALE"},
            {id: 2, name:"GENERIC_GENDER_FEMALE"}
        ];

        $scope.categoryNames = [];

        $scope.functionSignNames = [];

        $http.get('api/delegation/fields/list/category/' + $routeParams.uuid).then(function(categoryNames) {
            $scope.categoryNames = categoryNames.data;
        });

        $http.get('api/delegation/fields/list/function/' + $routeParams.uuid).then(function(functionNames) {
            $scope.functionSignNames = functionNames.data;
        });

        $scope.gender = $scope.genderOptions[0];

        $http.get('api/delegation/' + $routeParams.uuid).then(function(presentPersons) {
                $scope.presentPersons = presentPersons.data;
        });

        $translate('DELEGATION_MESSAGE_NO_EXTRA_FIELDS').then(function (text) {
            $scope.error_dialog = text;
        });

        $scope.labellist = {};

        $scope.getOptionalFieldsWithTypeContent = function() {
            $http.get('api/delegation/' + $routeParams.uuid + '/data/').then(function(fields) {
                $scope.fields = fields.data;
                if ($scope.fields.length == 0) {
                    $scope.showDialog = false;
                } else {
                    $scope.error_dialog = null;
                    $scope.showDialog = true;

                    for(var prop in $scope.fields) {
                        var curField = $scope.fields[prop];
                        if(curField.value != null) {
                            $scope.labellist[curField.pk] = curField.value;
                        }
                    }
                }
            });
        }

        $scope.getOptionalFieldsWithTypeContent();

        $scope.uploadImage = function() {
            $http({
                method: 'POST',
                url: 'api/fileupload/save',
                dataType: 'text',
                headers: {
                    "Content-Type": undefined
                },
                data: $.param({
                    file: $scope.image,
                    imgUUID: $scope.imgUUID
                })
            }).success(function(result) {
                $rootScope.delegationSuccessMessage = true;
            }).error(function(data, status, headers, config) {
            });
        }

        $scope.removeImage = function() {
            $scope.image = null;
            $scope.imgUUID = null;
        }

        $scope.validateImageExtension = function(flowFile) {
            var extensionTypes = new Array('jpeg','jpg','png');
            var fileExtension = flowFile.name.split('.');
            fileExtension = fileExtension[fileExtension.length - 1];
            var extensionStatus = false;

            for (var i in extensionTypes) {
                if (extensionTypes[i] == fileExtension) {
                    extensionStatus = true;
                }
            }

            if(!extensionStatus) {
                $scope.success = null;
                $rootScope.cleanMessages();

                $rootScope.correctImageFormat = false;
            }
            else {
                $rootScope.correctImageFormat = true;
            }
        }

        $scope.validateImageSize = function(img) {
            if ($rootScope.correctImageFormat) {

                // was cached 186x245
                if (img.complete) {
                    if (img.width == 186 && img.height == 245) {
                        $rootScope.correctImageSize = true;
                    }
                    else {
                        $rootScope.correctImageSize = false;
                    }
                }
            }
        }

        $scope.register_user = function() {
            $scope.hasTempImage = false;
            if ($scope.image != null) {
                $scope.hasTempImage = true;
            }

            $scope.success = null;
            $scope.error = null;
            $rootScope.cleanMessages();

            if ($scope.gender.id == 0 ) {
                $translate('GENERIC_MISSING_GENDER_MESSAGE').then(function (text) {
                    $scope.error = text;
                });
            } else if ($scope.vorname == null) {
                $translate('DELEGATION_MESSAGE_MISSING_FIRSTNAME').then(function (text) {
                    $scope.error = text;
                });
            } else if ($scope.nachname == null) {
                $translate('DELEGATION_MESSAGE_MISSING_LASTNAME').then(function (text) {
                    $scope.error = text;
                });
            } else if($scope.vorname.length > 35) {
                $translate('DELEGATION_MESSAGE_FIRSTNAME_MAX').then(function (text) {
                    $scope.error = text;
                });
            } else if($scope.nachname.length > 35) {
                $translate('DELEGATION_MESSAGE_LASTNAME_MAX').then(function (text) {
                    $scope.error = text;
                });
            } else if ($scope.gender.id == 1 || $scope.gender.id == 2 && typeof $scope.vorname != "undefined" &&
                        $scope.vorname != null && typeof $scope.nachname != "undefined" && $scope.nachname != null) {
                $translate('GENERIC_ERROR').then(function (text) {
                    var ERROR_TEXT = text;
                });

                $scope.button = true;

                if (($scope.nachname != null && $scope.nachname != '') && ($scope.vorname != null && $scope.vorname != '')) {
                    //Empty the standard label for saving
                    //No required fields
                    if ($scope.category == null) {
                        $scope.category = "";
                    }
                    if ($scope.functionSign == null) {
                        $scope.functionSign = "";
                    }

                    $http({
                        method: 'POST',
                        url: 'api/delegation/' + $routeParams.uuid + '/register',
                        dataType: 'text',
                        headers: {
                            "Content-Type": undefined
                        },

                        data: $.param({
                            firstname: $scope.vorname,
                            lastname: $scope.nachname,
                            gender: $scope.gender.name,
                            category: $scope.category,
                            fields: JSON.stringify($scope.labellist),
                            functionDescription: $scope.functionDescription,
                            personId: $scope.targetPersonId,
                            hasTempImage: $scope.hasTempImage
                        })
                    }).success(function(result) {
                        if (result.status === 'NO_EVENT_DATA') {
                            $translate('GENERIC_MESSAGE_EVENT_DOESNT_EXISTS').then(function (text) {
                                $scope.error = text;
                            });
                        } else if (result.status === 'WRONG_DELEGATION') {
                            $translate('DELEGATION_MESSAGE_DELEGATION_DOESNT_EXISTS').then(function (text) {
                                $scope.error = text;
                            });
                        } else if (result.status === 'OK') {
                            $scope.refreshData();

                            $rootScope.delegationSuccessMessage = true;

                            $http.get('api/delegation/' + $routeParams.uuid).then(function(presentPersons) {
                                $scope.presentPersons = presentPersons.data;
                            });

                        } else {
                            $scope.imgUUID = result.status;

                            $scope.uploadImage();

                            $scope.removeImage();

                            $scope.refreshData();

                            $rootScope.delegationSuccessMessage = true;

                            $route.reload();

                            $http.get('api/delegation/' + $routeParams.uuid).then(function(presentPersons) {
                                $scope.presentPersons = presentPersons.data;
                            });
                        }

                        $scope.button = false;
                    }).error(function(data, status, headers, config) {
                    });
                }
            } else {
                $translate('GENERIC_MESSAGE_FILL_IN_ALL_FIELDS').then(function (text) {
                    $scope.error = text;
                });
            }
        }

        $scope.refreshData = function() {
            $scope.success = null;
            $scope.error = null;
            $rootScope.cleanMessages();

            $scope.gender = $scope.genderOptions[0];
            $scope.category = null;
            $scope.functionDescription = null;
            $scope.nachname = null;
            $scope.vorname = null;
            $scope.targetPersonId = null;
            $scope.labellist = {};
            $scope.getOptionalFieldsWithTypeContent();
            $scope.removeImage();

            $timeout(function(){
              // waiting for modal closing effect - little delay, might not need this long
              $route.reload();
            }, 600);
        }

        $scope.loadPersonData = function(personId) {
            $scope.targetPersonId=personId;
            $scope.success = null;
            $scope.error = null;
            $rootScope.cleanMessages();

            $http.get('api/delegation/load/' + $routeParams.uuid + '/' + $scope.targetPersonId).then(function(person) {
                // Setting current values
                $scope.nachname = person.data.lastname_a_e1;
                $scope.vorname = person.data.firstname_a_e1;
                if (person.data.sex_a_e1 == 'f') {
                    $scope.gender = $scope.genderOptions[2];
                } else {
                    $scope.gender = $scope.genderOptions[1];
                }

                $scope.functionDescription = person.data.function_a_e1;
                // Loading optional fields
                $scope.showOptionalFields($scope.targetPersonId);
            });
            $scope.loadDelegateCategory();

            $scope.loadGuestImage();
        }

        $scope.getImageUUIDByUser = function() {
            $http.get('api/fileupload/user/image/' + $routeParams.uuid + "/" + $scope.targetPersonId).then(function(imgUUID) {
                $scope.imgUUID = imgUUID.data.status;
                $scope.downloadImage();
            });
        }

        $scope.downloadImage = function() {
            $http.get('api/fileupload/download/' + $scope.imgUUID).then(function(encodedImage) {
                $scope.image = encodedImage.data.status;
            });
        }

        $scope.loadGuestImage = function() {
            $scope.getImageUUIDByUser();
        }

        $scope.loadDelegateCategory = function() {
            $http.get('api/delegation/load/category/' + $routeParams.uuid + '/' + $scope.targetPersonId).then(function(catname) {
                if (catname != null) {
                    $scope.category = catname.data.status;
                }
            });
        }

        $scope.showOptionalFields = function (personId) {
            $scope.targetPersonId=personId;
            $translate('DELEGATION_MESSAGE_NO_EXTRA_FIELDS').then(function (text) {
                var ERROR_TEXT = text;
            });

            $scope.labellist = {};

            $http.get('api/delegation/load/fields/' + $routeParams.uuid + '/' + $scope.targetPersonId).then(function(fields) {
                $scope.fields = fields.data;
                if ($scope.fields.length == 0) {
                    $scope.error_dialog = ERROR_TEXT;
                    $scope.showDialog = false;
                } else {
                    $scope.error_dialog = null;
                    $scope.showDialog = true;

                    for(var prop in $scope.fields) {
                        var curField = $scope.fields[prop];
                        if(curField.value != null){
                            $scope.labellist[curField.pk] = curField.value;
                        }
                    }
                }
            });
        }

        $scope.saveOptionalFields = function () {
            $scope.success = null;
            $scope.error = null;
            $rootScope.cleanMessages();

            var list = $scope.labellist;

            $http({
                method: 'POST',
                url: 'api/delegation/'+ $routeParams.uuid + '/fields/save',
                headers: {"Content-Type" : undefined},
                data: $.param({
                    fields: JSON.stringify($scope.labellist),
                    personId: $scope.targetPersonId
                })
            }).success(function (result) {
                $rootScope.delegationSuccessMessage = true;
            }).error(function (data, status, headers, config) {
                $translate('GENERIC_ERROR').then(function (text) {
                    $scope.error = text;
                });
            });
        }
    }
});

onlineRegApp.controller('DirectLoginController',
                        function ($scope, $location, $http, $rootScope, $translate, $routeParams) {
    $scope.button = false;
    $rootScope.cleanMessages();

    $scope.logout = function () {
        $http({
            method: 'POST',
            url: 'api/idm/logout/'
        }).success(function (result) {
            $rootScope.cleanMessages();
            $rootScope.error = null;
            $scope.directusername = null;
            $scope.directpassword = null;

            $translate('GENERIC_LOGOUT_SUCCESSFUL_MESSAGE').then(function (text) {
                $rootScope.messageContent = text;
            });

            $rootScope.status = "success";
            $rootScope.user_logged_in = null;
            $rootScope.userinfo = null;
            $location.path("/");
        }).error(function (data, status, headers, config) {
        });
    }

    $scope.direct_login = function () {
        $scope.button = true;

        $http({
            method: 'POST',
            url: 'api/idm/login/' + encodeURIComponent($scope.directusername),
            headers: {"Content-Type" : undefined},
            data: $.param({
                    password: $scope.directpassword,
                    delegation: $routeParams.delegation
            })
        }).success(function (result) {
            $scope.button = false;
            $rootScope.error = null;
            if (result != "") {
                $rootScope.userinfo = result.status;
                $rootScope.user_logged_in = $scope.directusername;
                $rootScope.status = null;
                $rootScope.messageContent = null;
                $location.path($scope.nextPage);
            } else {
                $rootScope.userinfo = null;
                $translate('GENERIC_MESSAGE_USER_OR_PASSWORD_WRONG').then(function (text) {
                    $rootScope.messageContent = text;
                });
                $rootScope.status = "danger";
            }
        }).error(function (data, status, headers, config) {
            $scope.button = false;
            $translate('GENERIC_ERROR').then(function (text) {
                $rootScope.messageContent = text;
            });
            $rootScope.status = "danger";
        });
    }

    $scope.setStatus = function(value) {
            $scope.x = value;
    }

    $scope.setMessageContent = function(value) {
            $scope.x = value;
    }

    $scope.setNextPage = function(value) {
        $scope.nextPage = "/" + value;
    }
});

onlineRegApp.controller('LoginController', function ($scope, $location, $http, $rootScope, $translate, $routeParams) {
    //setStatus will be setted from RegisterUserController, if
    //registering was successful
    if(setStatus == 1) {
        $scope.status = 1;
        setStatus = null;
    }

    $scope.login = function () {
        $scope.button = true;

        $http({
            method: 'POST',
            url: 'api/idm/login/' + encodeURIComponent($scope.username),
            headers: {"Content-Type" : undefined},
            data: $.param({
                password: $scope.password,
                delegation: $routeParams.delegation
            })
        }).success(function (result) {
            $scope.error = null;
            $scope.button = false;
            if (result != "") {
                $rootScope.user_logged_in = $scope.username;
                $rootScope.userinfo = result.status;
                $rootScope.status = null;
                $rootScope.messageContent = null;
                $location.path($scope.nextPage);
            } else {
                $rootScope.status = "danger";
                $translate('GENERIC_MESSAGE_USER_OR_PASSWORD_WRONG').then(function (text) {
                    $rootScope.messageContent = text;
                });
            }
        }).error(function (data, status, headers, config) {
            $scope.button = false;
            $rootScope.status = "danger";
            $translate('GENERIC_ERROR').then(function (text) {
                $rootScope.messageContent = text;
            });
        });
    }
});

onlineRegApp.controller('EventController', function ($scope, $http, $rootScope) {
    $scope.success = null;
    $scope.error = null;
    $rootScope.cleanMessages();
    $scope.setNextPage('veranstaltungen');
    $http.get('api/event/list/' + $rootScope.user_logged_in).success(function (result) {
        $scope.events = result;
    });
});

onlineRegApp.controller('RegisterController', function ($scope, $rootScope, $location, $routeParams, $http, $translate) {
    $scope.success = null;
    $scope.error = null;
    $scope.registerButton = true;

    $http.get('api/event/guestlist/status/' + $routeParams.eventId).success(function(result) {
        //save result.status in scope for next functions
        $scope.guestStatus = result.status;
        //second status to save status of registering in waiting list
        $scope.registeredOnWaitingList = result.status;

        if (result.status === 'WAITING_LIST_FULL' && $routeParams.noLoginRequiredUUID == null) {
            $scope.registerButton = false;

            $translate('REGISTER_USER_MESSAGE_EVENT_FULL').then(function (text) {
                $scope.error = text;
            });
        }
        else {
            $scope.registerButton = true;
        }
    });

    if ($rootScope.user_logged_in == null && $routeParams.noLoginRequiredUUID == null) {
        $scope.setNextPage('register/' + $routeParams.eventId);
        $location.path('/login');
    } else {

        $http.get('api/event/registered/' + $rootScope.user_logged_in + '/' + $routeParams.eventId)
        .success(function (isUserRegistered) {

            if (!isUserRegistered) {
                $scope.noLoginRequiredUUID = $routeParams.noLoginRequiredUUID;

                $http.get('api/event/' + $routeParams.eventId).success(function (result) {
                    $scope.event = result;
                });
            }
            else {
                // redirect to update site because the user is already registered
                $location.path('/update/' + $routeParams.eventId);
            }
        });

//        $http.get('api/event/' + $routeParams.eventId + '/register/' + $scope.userId).success(function (result) {
//            if (!isUserLoged()) {
//                $location.path('/login');
//            } else {
//                if (result.invitationstatus) {
//                    $scope.acceptance = $scope.acceptanceOptions[result.invitationstatus];
//                }
//                if (result.notehost) {
//                    $scope.noteToHost = result.notehost;
//                }
//            }
//        });

        $scope.save = function () {
            if ($scope.noLoginRequiredUUID == null) {
                $http({
                    method: 'POST',
                    url: 'api/event/' + $routeParams.eventId + '/register',
                    headers: {"Content-Type" : undefined},
                    data: $.param({
                        notehost: $scope.noteToHost,
                        guestStatus: $scope.guestStatus
                    })
                }).success(function (result) {
                    if ($scope.registeredOnWaitingList === 'WAITING_LIST_OK') {
                        $translate('REGISTER_USER_MESSAGE_TO_RESERVE_LIST').then(function (text) {
                            $rootScope.previousErrorMessage = text;
                        });
                        $scope.setNextPage('veranstaltungen');
                        $location.path($scope.nextPage);
                    }
                    else if (result.status === 'OK') {
                        $translate(['USER_EVENT_REGISTER_MESSAGE_SUCCESSFUL_PART_ONE','USER_EVENT_REGISTER_MESSAGE_SUCCESSFUL_PART_TWO']).then(function (translations) {
                            $rootScope.previousMessage = translations['USER_EVENT_REGISTER_MESSAGE_SUCCESSFUL_PART_ONE'] + " \"" + $scope.event.shortname + "\" " + translations['USER_EVENT_REGISTER_MESSAGE_SUCCESSFUL_PART_TWO'];
                        });
                        $scope.setNextPage('veranstaltungen');
                        $location.path($scope.nextPage);
                    } else if (result.status === 'REGISTERED') {
                        $translate('USER_EVENTS_STATUS_CHANGED_ERROR_MESSAGE').then(function (text) {
                            $scope.error = text;
                        });
                    }
                });
            } else {
                $http({
                    method: 'POST',
                    url: 'api/event/' + $routeParams.eventId + '/register/nologin',
                    headers: {"Content-Type" : undefined},
                    data: $.param({
                        notehost: $scope.noteToHost,
                        noLoginRequiredUUID: $scope.noLoginRequiredUUID
                    })
                }).success(function (result) {
                    if (result.status === 'OK') {
                        $scope.error = null;
                        $translate(['USER_EVENT_REGISTER_MESSAGE_SUCCESSFUL_PART_ONE','USER_EVENT_REGISTER_MESSAGE_SUCCESSFUL_PART_TWO']).then(function (translations) {
                            $scope.success = translations['USER_EVENT_REGISTER_MESSAGE_SUCCESSFUL_PART_ONE'] + " \"" + $scope.event.shortname + "\" " + translations['USER_EVENT_REGISTER_MESSAGE_SUCCESSFUL_PART_TWO'];
                        });
                        $scope.noteToHost = null;
                    } else if (result.status === 'REGISTERED') {
                        $scope.success = null;
                        $translate('USER_EVENTS_STATUS_WITHOUT_LOGIN_CHANGED_ERROR_MESSAGE').then(function (text) {
                            $scope.error = text;
                        });
                    }
                });
            }

        }
    }
});

onlineRegApp.controller('RegisterUserController', function($scope, $http, $location, $rootScope) {
    $scope.success = null;
    $scope.error = null;
    $rootScope.cleanMessages();

    $scope.status = 0;

    $scope.register = function(isValid) {
        if(!isValid) { return; }
        $http({
            method: 'POST',
            url: 'api/user/register/' + encodeURIComponent($scope.osiam.userName) + '',
                headers: {"Content-Type" : undefined},
                data: $.param({
                osiam_firstname: $scope.osiam.firstName,
                osiam_secondname: $scope.osiam.lastName,
                osiam_password1: $scope.osiam.password
            })
        }).success(function (result) {
            switch(result.status) {
            case 'OK':
                $scope.status = 1;

                setStatus = 1;

                $scope.update = function (parm1, parm2) {
                    $scope.status = parm1 + ": " + parm2;
                };

                break;
            case 'USER_EXISTS':
                $scope.status = 'e1';
                break;
            case 'INVALID_USERNAME':
                $scope.status = 'e2';
                break;
            default:
                $scope.status = 'e';
            }

            if($scope.status == 1) {
                $location.path('/login');
            }

        }).error(function (data, status, headers, config) {
            $scope.status = 'e';
        });
    };
});

onlineRegApp.controller('VeranstaltungsController', function ($scope, $http, $rootScope, $location) {
    $scope.success = null;
    if($rootScope.previousErrorMessage != null && $rootScope.previousErrorMessage != "") {
        $scope.error = $rootScope.previousErrorMessage;
    } else {
        $scope.error = null;
    }

    $rootScope.previousErrorMessage = null;

    if ($rootScope.user_logged_in == null) {
        lastPageRegisterPath = $location.path();
        $location.path('/login');
        $rootScope.success = null;
    } else {
        var userEventsURL = 'api/event/userevents/' + $rootScope.user_logged_in;
        $http.get(userEventsURL).success(function (result) {
            $scope.events = result;
        });
    }
});
