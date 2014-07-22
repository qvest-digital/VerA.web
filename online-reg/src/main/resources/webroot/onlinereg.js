/**
 * Created by mley on 21.07.14.
 */

$('.navbar-lower').affix({
    offset: {top: 100}
});



var onlineRegApp = angular.module('onlineRegApp', [ 'ngRoute', 'ui.bootstrap' ]);



onlineRegApp.config(function ($routeProvider) {

    $routeProvider.when('/login', {
        templateUrl: 'partials/login.html',
        controller: 'LoginController'
    }).when('/welcome', {
        templateUrl: 'partials/welcome.html',
        controller: 'WelcomeController'
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

onlineRegApp.controller('WelcomeController', function ($scope) {



});