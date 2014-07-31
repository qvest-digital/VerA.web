/**
 * Created by mley on 21.07.14.
 */
$(document).ready(function () {
    // verA.web brand logo bar
    $('.navbar-lower').affix({
        offset: {top: 100}
    });

});

var onlineRegApp = angular.module('onlineRegApp', [ 'ngRoute', 'ui.bootstrap' ]);


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

    $scope.parseDate = function (dt) {
        // "2014-11-03 00:00:00+02"
        // timezone offset is ignored
        return moment(dt, "YYYY-MM-DD HH:mm:ss").toDate();
    }

    $http.get('/api/event/list').success(function (result) {
        console.log("loaded data");
        $scope.events = result;
    });
});

onlineRegApp.controller('RegisterController', function ($scope, $routeParams, $http) {

    $scope.acceptanceOptions = [
        {id: 1, label: "Offen"},
        {id: 2, label: "Zusage"},
        {id: 3, label: "Absage"}
    ];

    $scope.acceptance = $scope.acceptanceOptions[0];

    $http.get('/api/event/' + $routeParams.eventId).success(function (result) {
        console.log("loaded data");
        $scope.event = result;
    });

    $scope.save = function() {
        console.log("Teilnahme speichern:" + $scope.acceptance+" "+$scope.noteToHost);
    }
});
