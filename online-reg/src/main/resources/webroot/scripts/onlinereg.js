/**
 * Created by mley on 21.07.14.
 */

var onlineRegApp = angular.module('onlineRegApp', [ 'ngRoute', 'ui.bootstrap' ]);

onlineRegApp.run(function ($rootScope) {
    $rootScope.parseDate = function (dt) {
        return moment(dt).toDate();
    };
});


onlineRegApp.config(function ($routeProvider) {

    $routeProvider.when('/login', {
        templateUrl: 'partials/login.html',
        controller: 'LoginController'
    }).when('/welcome', {
        templateUrl: 'partials/welcome.html',
        controller: 'WelcomeController'
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
    }).when('/kontaktdaten', {
        templateUrl: 'partials/kontaktdaten.html',
        controller: 'KontaktdatenController'
    }).otherwise({
        redirectTo: '/event'
    });
});

onlineRegApp.controller('DirectLoginController', function ($scope, $location, $http, $rootScope) {
    $scope.button = false;

    $rootScope.no_messages = function() {
        $rootScope.status = null;
        $rootScope.messageContent = null;
    }
    
    $scope.isUserLoged = function () {
    	console.log("Checking USER CONNECTION ...");
    	return $rootScope.user_logged_in != null;
    }
    
    $scope.logout = function () {
        $http({
            method: 'POST',
            url: '/api/idm/logout/'
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
            url: '/api/idm/login/' + $scope.directusername,
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
            url: '/api/idm/login/' + $scope.username,
            params: {
                password: $scope.password
            }

        }).success(function (result) {
            $scope.button = false;

            if (result === "true") {
                console.log("Login erfolgreich");
                $location.path("/event");
				$rootScope.user_logged_in = $scope.username;
                $rootScope.status = null;
                $rootScope.messageContent = null;
                
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

onlineRegApp.controller('WelcomeController', function ($scope, $location) {


});

onlineRegApp.controller('EventController', function ($scope, $http, $rootScope) {
    $http.get('/api/event/list').success(function (result) {
        console.log("loaded data");
        $scope.events = result;
    });
});

onlineRegApp.controller('RegisterController', function ($scope, $routeParams, $http) {

    // currently hardwired to 2
    $scope.userId = 2;

    $scope.acceptanceOptions = [
        {id: 0, label: "Offen"},
        {id: 1, label: "Zusage"},
        {id: 2, label: "Absage"}
    ];

    $scope.acceptance = $scope.acceptanceOptions[0];

    $http.get('/api/event/' + $routeParams.eventId).success(function (result) {
        $scope.event = result;
        console.log("Auswahl: " + $scope.event.shortname);
    });

    $http.get('/api/event/' + $routeParams.eventId + '/register/' + $scope.userId).success(function (result) {
        if (result.invitationstatus) {
            $scope.acceptance = $scope.acceptanceOptions[result.invitationstatus];
        }
        if (result.notehost) {
            $scope.noteToHost = result.notehost;
        }
        console.log("Teilnahme: " + $scope.acceptance.label);
    });

    $scope.save = function () {
        $http({
            method: 'POST',
            url: '/api/event/' + $routeParams.eventId + '/register/' + $scope.userId,
            params: {
                invitationstatus: $scope.acceptance.id,
                notehost: $scope.noteToHost
            }
        }).success(function (result) {
            console.log("Teilnahme gespeichert: " + result);
        });
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
            url: '/api/user/register/' + $scope.osiam_username,
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

    onlineRegApp.controller('VeranstaltungsController', function ($scope) {
        $http.get('/api/event/list/{userid}/').success(function (result) {
                console.log("loaded data");
                $scope.events = result;
        });
    });
    
    onlineRegApp.controller('KontaktdatenController', function ($scope) {
        $http.get('/api/event/list/{userid}/').success(function (result) {
                console.log("loaded data");
                $scope.events = result;
        });
    });
});
