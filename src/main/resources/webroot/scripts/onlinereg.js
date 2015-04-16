/**
 * Created by mley on 21.07.14.
 */

var onlineRegApp = angular.module('onlineRegApp', [ 'ngRoute', 'ui.bootstrap', 'pascalprecht.translate' ]);

onlineRegApp.run(function ($rootScope) {
    $rootScope.parseDate = function (dt) {
        return moment(dt).toDate();
    };

    $rootScope.isUserLoged = function () {
    	return $rootScope.user_logged_in != null;
    }

    $rootScope.cleanMessages = function() {
    	$rootScope.previousMessage = null;
    	$rootScope.messageContent = null;
    }
});


onlineRegApp.config(function ($routeProvider, $translateProvider) {

    $routeProvider.when('/login', {
        templateUrl: 'partials/login.html',
        controller: 'LoginController'
    }).when('/event', {
        templateUrl: 'partials/event.html',
        controller: 'EventController'
    }).when('/register/:eventId', {
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
    }).when('/update/:eventId', {
		templateUrl: 'partials/update.html',
		controller: 'UpdateController'
    }).when('/reset/password/:uuid', {
		templateUrl: 'partials/reset_password.html',
		controller: 'ResetPasswordController'
    }).otherwise({
      redirectTo: '/event'
    })

	$translateProvider.useStaticFilesLoader({
		prefix: '/languages/lang-',
		suffix: '.json'
	});

    $translateProvider.preferredLanguage('de_DE');
});

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

onlineRegApp.controller('LangCtrl', function ($scope, $translate) {
  $scope.changeLang = function (key) {
    $translate.use(key).then(function (key) {
      console.log("Sprache zu " + key + " gewechselt.");
    }, function (key) {
      console.log("Irgendwas lief schief.");
    });
  };
});

onlineRegApp.controller('ResetPasswordController', function($http, $scope, $routeParams, $location,$rootScope) {
	$scope.resetPassword = function() {
		if ($scope.resetPasswordForm.password.$viewValue != $scope.resetPasswordForm.passwordRepeat.$viewValue) {
			$scope.error = "Die Wiederholung stimmt nicht mit dem Passwort überein!"
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
				$rootScope.success = "Ihr Passwort wurde erfolgreich geändert!"
				$location.path('/event');
			});
		}

	}
});

onlineRegApp.controller('FreeVisitorController', function($http, $scope, $location, $routeParams) {
	$http({
        method: 'GET',
        url: 'api/freevisitors/'+ $routeParams.uuid
    }).success(function (result) {
    	$location.path('/register/' + result);
    }).error(function (data, status, headers, config) {
    	$scope.error = "Bitte geben sie ihr Geschlecht an.";
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
	        console.log("Auswahl: " + $scope.event.shortname);
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

	        		$translate(['USER_EVENTS_STATUS_CHANGED_SUCCESSFULL_MESSAGE_PART_ONE','USER_EVENTS_STATUS_CHANGED_SUCCESSFULL_MESSAGE_PART_TWO']).then(function (translations) {
                      $rootScope.previousMessage = translations['USER_EVENTS_STATUS_CHANGED_SUCCESSFULL_MESSAGE_PART_ONE'] + " \"" + $scope.event.shortname + "\" " + translations['USER_EVENTS_STATUS_CHANGED_SUCCESSFULL_MESSAGE_PART_TWO'];
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

onlineRegApp.controller('MediaController', function ($scope, $http, $rootScope, $location, $routeParams) {
    $scope.genderOptions = [
        {id: 0, label: "Bitte wählen"},
        {id: 1, label: "Herr"},
        {id: 2, label: "Frau"}
    ];

    $scope.gender = $scope.genderOptions[0];

    $http.get('api/media/' + $routeParams.uuid).then(function(presentPersons) {
        $scope.presentPersons = presentPersons.data;
    });

    $scope.register_pressevertreter = function () {
        if ($scope.gender.id == 0) {
            $scope.error = "Bitte geben sie ihr Geschlecht an.";
            $scope.success = null;
        }
        else if ($scope.gender.id == 1 || $scope.gender.id == 2){
        	if ($scope.lastname != null && $scope.firstname != null && $scope.email != null && $scope.address != null &&
        			$scope.plz != null && $scope.city != null && $scope.country != null) {
	            var ERROR_TEXT = "Ein Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.";
	            $scope.button = true;
	            console.log("registering delegierten in the event.");
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
	                $scope.success = null;
	                $scope.error = null;

	                if (result.status === 'NO_EVENT_DATA') {
	                    $scope.error = "Der Veranstaltung existiert nicht";
	                    $scope.success = null;

	                }  else if (result.status === 'WRONG_EVENT') {
	                    $scope.error = "Der Veranstaltung existiert nicht";
	                    $scope.success = null;

	                } else if (result.status === 'OK') {
	                    $scope.error= null;
	                    $scope.success = "Ihre Daten werden nun überprüft, eine Zusage erfolgt nach positiver Überprüfung.";
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
	                    $scope.success = null;
	                }
	                $scope.button = false;

	            }).error(function (data, status, headers, config) {
	                $scope.error = ERROR_TEXT;
	                $scope.button = false;
	            });
        	}
        	else {
        		$scope.error = "Bitte wählen sie alle Felder.";
                $scope.success = null;
        	}
        }
    }
});

onlineRegApp.controller('DelegationController', function ($scope, $http, $rootScope, $location, $routeParams) {
	$rootScope.cleanMessages();
	if ($rootScope.user_logged_in == null) {
		$scope.setNextPage('delegation/' + $routeParams.uuid);
		$location.path('/login');
	} else {
		$scope.genderOptions = [
		 	{id: 0, label: "Bitte wählen"},
	        {id: 1, label: "Herr"},
	        {id: 2, label: "Frau"}
	    ];

		$scope.gender = $scope.genderOptions[0];
		$scope.success = null;
		$scope.error = null;

         $http.get('api/delegation/' + $routeParams.uuid).then(function(presentPersons) {
            $scope.presentPersons = presentPersons.data;
         });

		 $scope.register_user = function() {
		     if ($scope.gender.id == 0) {
		         $scope.error = "Bitte wählen Sie Ihr Geschlecht aus.";
		         $scope.success = null;
		     } 
		     else if($scope.vorname.length > 35) {
		    	 $scope.error = "Bitte geben Sie einen Vornamen mit maximal 35 Buchstaben ein.";
		         $scope.success = null;
		     } 
		     else if($scope.nachname.length > 35) {
		    	 $scope.error = "Bitte geben Sie einen Nachnamen mit maximal 35 Buchstaben ein.";
		         $scope.success = null;
		     }
		     else if (typeof $scope.vorname == "undefined" && $scope.vorname == null) {
		    	 $scope.error = "Bitte geben Sie einen Vornamen ein.";
		         $scope.success = null;
		     } else if (typeof $scope.nachname == "undefined" && $scope.nachname == null) {
		    	 $scope.error = "Bitte geben Sie einen Nachnamen ein.";
		         $scope.success = null;
		     }
		     else if ($scope.gender.id == 1 || $scope.gender.id == 2 && typeof $scope.vorname != "undefined" &&
		    		 $scope.vorname != null && typeof $scope.nachname != "undefined" && $scope.nachname != null) {
		         var ERROR_TEXT = "Ein Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.";

	             $scope.button = true;
	             console.log("registering delegation in the event.");
	             
	             if (($scope.nachname != null && $scope.nachname != '') && ($scope.vorname != null && $scope.vorname != '')) {
		             $http({
		             	method: 'POST',
		             	url: 'api/delegation/' + $routeParams.uuid + '/register',
		             	dataType: 'text',
		             	headers: {
		             			"Content-Type": undefined
		             	},
		             	data: $.param({
		             		firstname: $scope.nachname,
		             		lastname: $scope.vorname,
		                  	gender: $scope.gender.label
		             	})
		             }).success(function(result) {
		             	 $scope.success = null;
		             	 $scope.error = null;
	
		             	 if (result.status === 'NO_EVENT_DATA') {
		             		 $scope.error = "Die Veranstaltung existiert nicht";
		             		 $scope.success = null;
		             	 } else if (result.status === 'WRONG_DELEGATION') {
		             		 $scope.error = "Die Delegation existiert nicht";
		             		 $scope.success = null;
		             	 } else if (result.status === 'OK') {
		             		 $scope.error = null;
		             		 $scope.success = "Delegierten Daten wurden gespeichert.";
		             		 $scope.gender = $scope.genderOptions[0];
		             		 $scope.nachname = null;
		             		 $scope.vorname = null;
	
		             		 $http.get('api/delegation/' + $routeParams.uuid).then(function(presentPersons) {
		             			 $scope.presentPersons = presentPersons.data;
		             		 });
		                  } else {
		                      $scope.error = ERROR_TEXT;
		                      $scope.success = null;
		                  }
	
		             	 $scope.button = false;
	
		             }).error(function(data, status, headers, config) {
	
		             });
	             }
		     }
		     else {
	            $scope.error = "Bitte füllen Sie alle Felder aus.";
	            $scope.success = null;
		     }
		 }

		 $scope.showOptionalFields = function (personId) {

			 $scope.targetPersonId=personId;
			 var ERROR_TEXT = "Zu diesem Event existieren keine weiteren Felder.";

			 $scope.success = null;
			 $scope.error = null;
			 $scope.labellist = {};

			 $http.get('api/delegation/' + $routeParams.uuid + '/' + personId + '/data/').then(function(fields) {
                 $scope.fields = fields.data;
                 console.log("number of fields: "+$scope.fields.length)
                 if ($scope.fields.length == 0) {
                   $scope.error_dialog = ERROR_TEXT;
 		           $scope.success = null;
 		           $scope.hideDialog = true;
                 }
                 else {
                    $scope.error_dialog = null;
   		            $scope.hideDialog = false;
                	console.log(JSON.stringify($scope.fields));

                 	for(var prop in $scope.fields){
	    				 var curField = $scope.fields[prop];

	                	 if(curField.value != null){

	    					 $scope.labellist[curField.pk] = curField.value;
	    					 console.log(curField.value + "|" + $scope.labellist[curField.pk]);
	    				 }
	    			 }
                 }
              });
		 }

		 $scope.saveOptionalFields = function () {
			 $scope.success = null;
			 $scope.error = null;

			 var list = $scope.labellist;
			 console.log("labels " + list);

			 	$http({
		            method: 'POST',
		            url: 'api/delegation/'+ $routeParams.uuid + '/fields/save',
		            headers: {"Content-Type" : undefined},
		            data: $.param({
		            	fields: JSON.stringify($scope.labellist),
		            	personId: $scope.targetPersonId
		            })
		        }).success(function (result) {
		            console.log('Optional Felder speichern...');

		            $scope.error= null;
	                $scope.success = "Delegiertdaten wurden gespeichert.";


		        }).error(function (data, status, headers, config) {
		            console.log('ERROR! Optional fields not saved!”');
		            $scope.error= "Ein Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.";
	                $scope.success = null;
		        });
		 }
	}
});

onlineRegApp.controller('DirectLoginController', function ($scope, $location, $http, $rootScope) {
    $scope.button = false;
    $rootScope.cleanMessages();
    $rootScope.no_messages = function() {
        $rootScope.status = null;
        $rootScope.messageContent = null;
    }

    $scope.logout = function () {
        $http({
            method: 'POST',
            url: 'api/idm/logout/'
        }).success(function (result) {
            console.log('You have successfully logged out!')
            $rootScope.error = null;
            $scope.directusername = null;
            $scope.directpassword = null;
            $rootScope.messageContent = "Erfolgreich abgemeldet!";
            $rootScope.status = "success";
            $rootScope.user_logged_in = null;
            $rootScope.userinfo = null;
            $location.path('/');
        }).error(function (data, status, headers, config) {
            console.log('ERROR! Logout failed!”')
        });
    }

    $scope.direct_login = function () {
        $scope.button = true;
        console.log("logging in.");

        $http({
            method: 'POST',
            url: 'api/idm/login/' + encodeURIComponent($scope.directusername),
            headers: {"Content-Type" : undefined},
            data: $.param({
                password: $scope.directpassword
            })
        }).success(function (result) {
            $scope.button = false;
            $rootScope.error = null;
            if (result != "") {
				$rootScope.userinfo = result.status;
                console.log("Login erfolgreich");
		    	$rootScope.user_logged_in = $scope.directusername;
                $rootScope.status = null;
                $rootScope.messageContent = null;
            } else {
            	$rootScope.userinfo = null;
                $rootScope.messageContent = "Der Benutzername oder das Passwort ist falsch.";
                $rootScope.status = "danger";
            }
        }).error(function (data, status, headers, config) {
            $scope.button = false;
            $rootScope.messageContent = "Es ist ein Fehler aufgetreten. Bitte versuchen Sie es später erneut.";
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


onlineRegApp.controller('LoginController', function ($scope, $location, $http, $rootScope) {
    $rootScope.button = false;
    $rootScope.status = null;
    $rootScope.messageContent = null;
    $rootScope.cleanMessages();

    $scope.login = function () {
        $scope.button = true;
        console.log("logging in.");

        $http({
            method: 'POST',
            url: 'api/idm/login/' + encodeURIComponent($scope.username),
            headers: {"Content-Type" : undefined},
            data: $.param({
            	password: $scope.password
            })
        }).success(function (result) {
            $scope.button = false;

            if (result != "") {
				$rootScope.user_logged_in = $scope.username;
				$rootScope.userinfo = result.status;
                $rootScope.status = null;
                $rootScope.messageContent = null;
                $location.path($scope.nextPage);

            } else {
                $rootScope.status = "danger";
                $rootScope.messageContent = "Der Benutzername oder das Passwort ist falsch.";
            }
        }).error(function (data, status, headers, config) {
            $scope.button = false;
            $rootScope.status = "danger";
            $rootScope.messageContent = "Es ist ein Fehler aufgetreten. Bitte versuchen Sie es später erneut.";
        });
    }
});

onlineRegApp.controller('EventController', function ($scope, $http, $rootScope) {
    $http.get('api/event/list/' + $rootScope.user_logged_in).success(function (result) {
        console.log("loaded data");
        $scope.events = result;
    });
});

onlineRegApp.controller('RegisterController', function ($scope, $rootScope, $location, $routeParams, $http) {

	if ($rootScope.user_logged_in == null) {

		$scope.setNextPage('register/' + $routeParams.eventId);
		$location.path('/login');
	} else {
	    // currently hardwired to 2
	    $scope.userId = 2;

	    $scope.acceptanceOptions = [
	        {id: 0, label: "Offen"},
	        {id: 1, label: "Zusage"},
	        {id: 2, label: "Absage"}
	    ];

	    $scope.acceptance = $scope.acceptanceOptions[0];

	    $http.get('api/event/' + $routeParams.eventId).success(function (result) {
	        $scope.event = result;
	        console.log("Auswahl: " + $scope.event.shortname);
	    });

	    $http.get('api/event/' + $routeParams.eventId + '/register/' + $scope.userId).success(function (result) {
	    	if (!isUserLoged()) {

	    		$location.path('/login');
	    	} else {
		        if (result.invitationstatus) {
		            $scope.acceptance = $scope.acceptanceOptions[result.invitationstatus];
		        }
		        if (result.notehost) {
		            $scope.noteToHost = result.notehost;
		        }
		        console.log("Teilnahme: " + $scope.acceptance.label);
	    	}
	    });

	    $scope.save = function () {
	        $http({
	            method: 'POST',
	            url: 'api/event/' + $routeParams.eventId + '/register',
	            headers: {"Content-Type" : undefined},
	            data: $.param({
	                notehost: $scope.noteToHost
	            })
	        }).success(function (result) {
	        	if (result.status === 'OK') {
	        		$rootScope.previousMessage="Sie haben sich erfolgreich für die Veranstaltung \"" + $scope.event.shortname + "\" angemeldet.";
	        		console.log("Teilnahme gespeichert: " + result);
	        		$scope.setNextPage('veranstaltungen');
	        		$location.path($scope.nextPage);
	        	} else if (result.status === 'REGISTERED'){
	        		$scope.error = 'Sie sind bereits für diese Veranstaltung angemeldet.';
	        	}
	        });
	    }
	}
});

onlineRegApp.controller('RegisterUserController',  function($scope, $http) {
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

		    window.console.log("User registered with response:  '" + result + "'.");
		}).error(function (data, status, headers, config) {
			$scope.status = 'e';

			window.console.log("ERROR while userregistration.");
		});
    };
});



onlineRegApp.controller('VeranstaltungsController', function ($scope, $http, $rootScope, $location) {
    if ($rootScope.user_logged_in == null) {
        $location.path('/login');
        $rootScope.success = null;
    } else {
        console.log("DEBUG: " + $rootScope.user_logged_in);
        var userEventsURL = 'api/event/userevents/' + $rootScope.user_logged_in;
        $http.get(userEventsURL).success(function (result) {
            console.log("Loading user's subscribed events...");
            $scope.events = result;
        });
    }
});

onlineRegApp.controller('KontaktdatenController', function ($scope, $location, $rootScope) {
	$rootScope.cleanMessages();
    if ($rootScope.user_logged_in == null) {
        $location.path('/login');
    } else {
        $http.get('api/event/list/{userid}/').success(function (result) {
                console.log("loaded data");
                $scope.events = result;
        });
    }
});