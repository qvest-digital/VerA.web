var angular = require("angular");

var auth = angular.module("vwoa-auth", [
  require('../vwoa-alerts'),
  require('../vwoa-utilities'),
  require('angular-route')
]);

auth.factory("authService", require("./service"));

auth.controller("LoginController", require("./controller"));

/*
 * redirect to login, if user has not authenticated yet.
 */
auth.run(function($rootScope, $location, authService) {
  var isPublic = function(path){
    return [
      '/login',
      '/createAccount',
      '/forgotLogin',
      '/forgotPassword',
      '/media',
      '/user/activate',
      '/reset/password',
      '/imprint',
      '/event'
    ].some(function(prefix){return 0 === path.indexOf(prefix);});
  };
  $rootScope.setNextPage = function(){};
  var originalPath = undefined;
  var redirectIfNotAuthenticated = function(user) {
    if (!user.authenticated && ! isPublic($location.path())) {
      originalPath = originalPath || $location.path();
      $location.path('/login');
    }
    if (user.authenticated && originalPath){
      $location.path(originalPath);
      originalPath = null;
    }
  };
  $rootScope.$on('$routeChangeStart', function() {
    authService.queryStatus()
      .then(redirectIfNotAuthenticated);
  });
  $rootScope.$on('vwoa-auth:authentication-state-changed', function(ev,user){
    redirectIfNotAuthenticated(user);
  });
});

module.exports = auth.name;
