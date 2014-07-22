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