/**
 * Created by mley on 21.07.14.
 */

var onlineRegApp = angular.module('onlineRegApp', [ 'ngRoute', 'ui.bootstrap' ]);

onlineRegApp.run(function ($rootScope) {
    $rootScope.parseDate = function (dt) {
        return moment(dt).toDate();
    };
    
    $rootScope.isUserLoged = function () {
    	return $rootScope.user_logged_in != null;
    }
});


onlineRegApp.config(function ($routeProvider) {

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
    }).otherwise({
      redirectTo: '/event'
    })
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
        	if ($scope.lastname != null && $scope.firstname != null && $scope.email != null && $scope.address != null && $scope.plz != null && $scope.city != null && $scope.country != null) {
	            var ERROR_TEXT = "Ein Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.";
	            $scope.button = true;
	            console.log("registering delegierten in the event.");
	            $http({
	                method: 'POST',
	                url: 'api/media/' + $routeParams.uuid + '/register',
	                params: {
	                    nachname: $scope.lastname,
	                    vorname: $scope.firstname,
	                    gender: $scope.gender.label,
	                    email: $scope.email,
	                    address: $scope.address,
	                    plz: $scope.plz,
	                    city: $scope.city,
	                    country: $scope.country
	                }
	            }).success(function (result) {
	                $scope.success = null;
	                $scope.error = null;
	
	
	                if (result === 'USER_EXISTS') {
	                    $scope.error = "Ein Benutzer mit diesem Benutzernamen existiert bereits.";
	                    $scope.success = null;
	
	                } else if (result === 'INVALID_USERNAME') {
	                    $scope.error = "Der Benutzername darf nur Buchstaben und Zahlen enthalten.";
	                    $scope.success = null;
	
	                } else if (result === 'NO_EVENT_DATA') {
	                    $scope.error = "Der Veranstaltung existiert nicht";
	                    $scope.success = null;
	
	                }  else if (result === 'WRONG_EVENT') {
	                    $scope.error = "Der Veranstaltung existiert nicht";
	                    $scope.success = null;
	
	                } else if (result === 'OK') {
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

         $http.get('api/delegation/' + $routeParams.uuid).then(function(presentPersons) {
            $scope.presentPersons = presentPersons.data;
         });

		 $scope.register_user = function () {
			 if ($scope.gender.id == 0) {
				 $scope.error = "Bitte wählen der Gender Feld";
				 $scope.success = null;
			 }
			 else if ($scope.gender.id == 1 || $scope.gender.id == 2){
			 	var ERROR_TEXT = "Ein Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.";
		        $scope.button = true;
		        console.log("registering delegierten in the event.");
		        $http({
		            method: 'POST',
		            url: 'api/delegation/' + $routeParams.uuid + '/register',
		            params: {
		            	nachname: $scope.nachname,
		                vorname: $scope.vorname,
		                gender: $scope.gender.label
		            }
		        }).success(function (result) {
		            $scope.success = null;
		            $scope.error = null;
		            

		            if (result === 'USER_EXISTS') {
		                $scope.error = "Ein Benutzer mit diesem Benutzernamen existiert bereits.";
		                $scope.success = null;

		            } else if (result === 'INVALID_USERNAME') {
		                $scope.error = "Der Benutzername darf nur Buchstaben und Zahlen enthalten.";
		                $scope.success = null;

		            } else if (result === 'NO_EVENT_DATA') {
		                $scope.error = "Der Veranstaltung existiert nicht";
		                $scope.success = null;

		            }  else if (result === 'WRONG_DELEGATION') {
		                $scope.error = "Die Delegation existiert nicht";
		                $scope.success = null;

		            } else if (result === 'OK') {
		            	$scope.error= null;
		                $scope.success = "Delegiertdaten wurden gespeichert.";
		                $http.get('api/delegation/' + $routeParams.uuid).then(function(presentPersons) {
		                    $scope.presentPersons = presentPersons.data;
		                 });

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
		    }
		 $scope.editInfo = function ($scope) {
			 var person = $scope.presentPersons.get(1)
			 $scope.firstdetailname=person.firstname_a_e1;
			 $scope.lastdetailname=person.lastname_a_e1;
				 
		 }
		 
		
	}
});

onlineRegApp.controller('DirectLoginController', function ($scope, $location, $http, $rootScope) {
    $scope.button = false;

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
            url: 'api/idm/login/' + $scope.directusername,
            params: {
                password: $scope.directpassword
            }
        }).success(function (result) {
            $scope.button = false;	
            $rootScope.error = null;
            if (result === "true") {
                console.log("Login erfolgreich");
		    	$rootScope.user_logged_in = $scope.directusername;
                $rootScope.status = null;
                $rootScope.messageContent = null;
            } else {
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

    $scope.login = function () {
        $scope.button = true;
        console.log("logging in.");

        $http({
            method: 'POST',
            url: 'api/idm/login/' + $scope.username,
            params: {
                password: $scope.password
            }

        }).success(function (result) {
            $scope.button = false;

            if (result === "true") {
				$rootScope.user_logged_in = $scope.username;
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
    $http.get('api/event/list').success(function (result) {
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
	    	}
	    	else {
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
	            url: 'api/event/' + $routeParams.eventId + '/register/' + $scope.userId,
	            params: {
	                invitationstatus: $scope.acceptance.id,
	                notehost: $scope.noteToHost
	            }
	        }).success(function (result) {
	            console.log("Teilnahme gespeichert: " + result);
	        });
	    }
	}
});

onlineRegApp.controller('RegisterUserController', function ($scope, $location, $http) {
    $scope.button = true;

    $scope.changed = function () {
        $scope.button = false;
    }

    var ERROR_TEXT = "Ein Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.";

    $scope.register_user = function () {
        $scope.button = true;
        console.log("registering user.");
        $http({
            method: 'POST',
            url: 'api/user/register/' + $scope.osiam_username,
            params: {
                osiam_firstname: $scope.osiam_firstname,
                osiam_secondname: $scope.osiam_secondname,
                osiam_password1: $scope.osiam_password1
            }
        }).success(function (result) {
            $scope.success = null;
            $scope.register_error = null;

            if (result === 'USER_EXISTS') {
                $scope.register_error = "Ein Benutzer mit diesem Benutzernamen existiert bereits.";

            } else if (result === 'INVALID_USERNAME') {
                $scope.register_error = "Der Benutzername darf nur Buchstaben und Zahlen enthalten.";

            } else if (result === 'OK') {
                $scope.success = "Benutzerdaten wurden gespeichert.";

            } else {
                $scope.register_error = ERROR_TEXT;
            }
            $scope.button = false;

        }).error(function (data, status, headers, config) {
            $scope.register_error = ERROR_TEXT;
            $scope.button = false;
        });
    }

});

onlineRegApp.controller('VeranstaltungsController', function ($scope, $http, $rootScope, $location) {
    if ($rootScope.user_logged_in == null) {
        $location.path('/login');
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
    if ($rootScope.user_logged_in == null) {
        $location.path('/login');
    } else {
        $http.get('api/event/list/{userid}/').success(function (result) {
                console.log("loaded data");
                $scope.events = result;
        });
    }
});