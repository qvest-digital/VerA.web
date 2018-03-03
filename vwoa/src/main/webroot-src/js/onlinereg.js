/**
 * Created by mley on 21.07.14.
 */
var angular = require('angular');
var bulk = require('bulk-require');
//TODO: shouldn't we try to group stuff by feature instead?
var controllers = bulk(__dirname, ['controllers/*.js']).controllers;
var factories = bulk(__dirname, ['factories/*.js']).factories;
var customModules = bulk(__dirname, ['modules/*']).modules;
var utils = require("./utils");
var UnexpectedStatusError = require('./unexpected-status-error')
//there are some misbehaving angular modules that require us to
//get them via bower through the debowerify transform.... sigh....

var app = angular.module('onlineRegApp', [
  require('angular-route'),
  require('angular-material'),
  require('angular-messages'),
  require('angular-animate'),
  require('angular-aria'),
  require('angular-translate'),
  require('angular-translate-loader-static-files')
  // load our own customModules last:
].concat(utils.values(customModules)));

//convert hyphen-separated (a.k.a. kebap-case or lisp-case) to camelCase

if (controllers) {
  Object.keys(controllers).forEach(function(fileName) {
    var controllerName = utils.capitalize(utils.kebapToCamelCase(fileName));
    app.controller(controllerName, controllers[fileName]);
  });
}
if (factories) {
  Object.keys(factories).forEach(function(fileName) {
    var factoryName = utils.kebapToCamelCase(fileName);
    app.factory(factoryName, controllers[fileName]);
  });
}

app.run(function($rootScope,moment) {
  $rootScope.parseDate = function(dt) {
    return moment(dt).toDate();
  };

  $rootScope.isUserLoged = function() {
    return $rootScope.user_logged_in != null;
  };

  $rootScope.cleanMessages = function() {
    $rootScope.button = false;
    $rootScope.status = null;
    $rootScope.previousMessage = null;
    $rootScope.messageContent = null;
    $rootScope.error = null;
    $rootScope.success = null;
  };

  $rootScope.cleanImageControls = function() {
    $rootScope.correctImageFormat = true;
    $rootScope.correctImageSize = true;
  };
  //Only required for LoginController
  setStatus = null;
});

/* initialise app configuration */
app.config(['$locationProvider', '$routeProvider', '$translateProvider', '$mdDateLocaleProvider',
 function ($locationProvider, $routeProvider, $translateProvider, $mdDateLocaleProvider) {
  $locationProvider.hashPrefix('');

  $routeProvider.when('/login', {
    templateUrl: 'partials/login.html',
    controller: 'LoginController'
  }).when('/login/:delegation', {
    templateUrl: 'partials/login.html',
    controller: 'LoginController'
  }).when('/event', {
    templateUrl: 'partials/event.html',
    controller: 'EventController'
  }).when('/register/:eventUuid', {
    templateUrl: 'partials/register.html',
    controller: 'RegisterController'
  }).when('/register/:eventUuid/:noLoginRequiredUUID', {
    templateUrl: 'partials/register.html',
    controller: 'RegisterController'
  }).when('/createAccount/', {
    templateUrl: 'partials/register_user.html',
    controller: 'RegisterUserController'
  }).when('/forgotLogin/', {
    templateUrl: 'partials/forgot_login.html',
    controller: 'ForgotLoginController'
  }).when('/forgotPassword/', {
    templateUrl: 'partials/forgot_password.html',
    controller: 'ForgotPasswordController'
  }).when('/veranstaltungen', {
    templateUrl: 'partials/meine-veranstaltungen.html',
    controller: 'VeranstaltungsController'
  }).when('/kontaktdaten', {
    templateUrl: 'partials/kontaktdaten.html',
    controller: 'KontaktdatenController',
  }).when('/delegation/:uuid', {
    templateUrl: 'partials/delegation.html',
    controller: 'delegationController'
  }).when('/media/:uuid', {
    templateUrl: 'partials/media.html',
    controller: 'MediaController'
  }).when('/freevisitors/:uuid', {
    template: "",
    controller: 'FreeVisitorController'
  }).when('/freevisitors/:uuid/:noLoginRequiredUUID', {
    template: "",
    controller: 'FreeVisitorController'
  }).when('/update/:eventUuid', {
    templateUrl: 'partials/update.html',
    controller: 'UpdateController'
  }).when('/reset/password/:uuid', {
    templateUrl: 'partials/reset_password.html',
    controller: 'ResetPasswordController'
  }).when('/page_not_found', {
    templateUrl: 'partials/page_not_found.html',
    controller: 'PageNotFoundController'
  }).when('/user/activate/:activation_token', {
    template: "",
    controller: 'UserActivationController'
  }).when('/user/resend/confirmationmail/:activation_token', {
    template: "",
    controller: 'UserRefreshActivationController'
  }).when('/media/activation/confirm/:pressUserActivationToken', {
    template: "",
    controller: 'MediaRepresentativeActivationController'
 }).when('/imprint', {
    templateUrl: 'partials/imprint.html',
    controller: 'ImprintController'
  }).otherwise({
    redirectTo: '/event'
  });

  $translateProvider.useSanitizeValueStrategy(/* 'sanitize' */ 'escape'); // until issue 1101 is resolved, see https://angular-translate.github.io/docs/#/guide/19_security
  $translateProvider.useStaticFilesLoader({
    prefix: 'languages/lang-',
    suffix: '.json'
  });

  $translateProvider.preferredLanguage('de_DE');

  //Datepicker configuration
  $mdDateLocaleProvider.firstDayOfWeek = 1;
}]);

app.directive('equals', function() {
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
app.factory("vwoa", function($rootScope, $location, $timeout, $translate,show) {

  var expectStatus = function(expectedStatus, messages) {
    return function(result) {
      if (result.data.status != expectedStatus) {
        throw new UnexpectedStatusError(expectedStatus, result.data.status);
      }
      return result;
    };
  };

  var lookup = function(obj) {
    if (typeof obj == "object") {
      if (obj.hasOwnProperty(result.data.status)) {
        return obj[result.data.status];
      } else if (obj.hasOwnProperty('*')) {
        return obj['*']
      } else {
        return null;
      }
    }

    return obj;
  };

  var handleResponse = function(msg0, redirect0) {
    return function(result) {
      var message = lookup(msg0),
        redirect = lookup(redirect0);
      if(result instanceof Error){
        show.error(message);
      } else {
        show.success(message);
      }
      if (redirect) {
        $location.path(redirect);
      }
    };
  };

  return {
    expectStatus: expectStatus,
    handleResponse: handleResponse
  };
});
