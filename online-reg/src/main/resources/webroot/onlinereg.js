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
    }).when('/register/:osiam_username', {
        templateUrl: 'partials/register_user.html',
        controller: 'RegisterUserController'
    }).otherwise({
        redirectTo: '/login'
    });
});

onlineRegApp.controller('LoginController', function ($scope, $location, $http) {

   $scope.login = function () {
        console.log("logging in.");

        $http({
            method: 'POST',
            url: '/api/idm/login/' + $scope.username,
            params: {
                password: $scope.password
            }

        }).success(function (result) {

            if (result === "true") {
                console.log("Login erfolgreich");
                $location.path("/event");
            } else {
                // Benutzername oder Password falsch
            }

        }).error(function (data, status, headers, config) {

            // Fehler (z.B. OSIAM nicht erreichbar)

        });
    }

});

onlineRegApp.controller('WelcomeController', function ($scope, $location) {


});

onlineRegApp.controller('EventController', function ($scope, $http) {

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

    $scope.register_user = function () {
        console.log("registering user.");
        $http({
            method: 'POST',
            url: '/api/user/register/'+$scope.osiam_username,
            params: {
                osiam_firstname: $scope.osiam_firstname,
                osiam_secondname: $scope.osiam_secondname,
                osiam_password1: $scope.osiam_password1
            }
        }).success(function (result) {
            console.log("User prüfen: " + result);
            $scope.success = "Benutzerdaten wurden gesendet.";
	    $scope.result = result;
	    if(result==='USER_EXISTS'){
		$scope.user_exists=true;
		$scope.user="Benutzer existiert bereits.";			
	    }else {
		$scope.user="Benutzerdaten wurden gespeichert.";
		$scope.user_exists=false;
            }
        }).error(function (data, status, headers, config) {
            $scope.error = "OSIAM error.";
            /*$scope.data=data;
             $scope.status=status;
             $scope.headers=headers;
             $scope.config=config;*/
            // Fehler (z.B. OSIAM nicht erreichbar)/**/

        });
    }
});
