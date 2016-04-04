module.exports = function($scope, $http, $rootScope, $location, $routeParams, $translate, $route, $timeout, $anchorScroll, param) {
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
        lastPageRegisterPath = $location.path();
        if ($routeParams.uuid != null) {
            $scope.setNextPage('delegation/' + $routeParams.uuid);
            $location.path('/login/' + $routeParams.uuid);
        }
    } else {
        $scope.genderOptions = [
            {id: 0, name:"GENERIC_PLEASE_SELECT"},
            {id: 1, name:"GENERIC_SALUTATION_MALE"},
            {id: 2, name:"GENERIC_SALUTATION_FEMALE"}
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
                data: param({
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
                $translate('GENERIC_MESSAGE_MISSING_FIRSTNAME').then(function (text) {
                    $scope.error = text;
                });
            } else if ($scope.nachname == null) {
                $translate('GENERIC_MESSAGE_MISSING_LASTNAME').then(function (text) {
                    $scope.error = text;
                });
            } else if($scope.vorname.length > 35) {
                $translate('GENERIC_MESSAGE_FIRSTNAME_MAX').then(function (text) {
                    $scope.error = text;
                });
            } else if($scope.nachname.length > 35) {
                $translate('GENERIC_MESSAGE_LASTNAME_MAX').then(function (text) {
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

                        data: param({
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
                data: param({
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
};
