/**
 * Created by mley on 21.07.14.
 */


/*$(document).ready(function () {
    // verA.web brand logo bar
    $('.navbar-lower').affix({
        offset: {top: 100}
    });

});*/

var onlineRegApp = angular.module('onlineRegApp', [ 'ngRoute', 'ui.bootstrap' ]);

onlineRegApp.run(function ($rootScope) {
    $rootScope.parseDate = function (dt) {
        return moment(dt, "YYYY-MM-DD HH:mm:ss").toDate();
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
    }).otherwise({
        redirectTo: '/login'
    });
});

onlineRegApp.controller('LoginController', function ($scope, $location) {

    $scope.login = function () {
        console.log("logging in.");
        $location.path("/welcome");
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
    });

    $http.get('/api/event/' + $routeParams.eventId + '/register/'+$scope.userId).success(function (result) {
        if (result.invitationstatus) {
            $scope.acceptance = $scope.acceptanceOptions[result.invitationstatus];
        }
        if (result.notehost) {
            $scope.noteToHost = result.notehost;
        }
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
