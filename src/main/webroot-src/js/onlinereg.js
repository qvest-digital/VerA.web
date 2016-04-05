/**
 * Created by mley on 21.07.14.
 */
var angular = require('angular');
var bulk = require('bulk-require');
var moment = require('moment');
var param = require('jquery-param');
var controllers = bulk(__dirname, ['controllers/*.js']).controllers;
//there are some misbehaving angular modules that require us to 
//get them via bower through the debowerify transform.... sigh....

//ng-flow expects a global variable Flow. Welcome to the ninetees :-&
window.Flow = require('flow.js');
var NGFlow = require('ng-flow');

//Moment loads locales via dynamic require calls.
//Since browserify cannot infer such dependencies, we need to help a bit
//We could use bulkify to force them all to be loaded, but that seems a bit over the top?
require('moment/locale/de');
require('moment/locale/fr');
require('moment/locale/es');



var app = angular.module('onlineRegApp', [
    require('angular-route'),
    require('angular-material'),
    require('angular-messages'),
    require('angular-animate'),
    require('angular-aria'),
    require('angular-bootstrap-npm'),
    require('angular-translate'),
    require('angular-translate-loader-static-files'),
    //the following few are do not quite follow the usual conventions
    //let's just hope everything works.
    'flow',
    'flow.img',
    'flow.init',
    'flow.provider' 
    
]);
//register controllers
/*
[
  'DelegationController',
  'DirectLoginController',
  'EventController',
  'FreeVisitorController',
  'KontaktdatenController',
  'LanguageSelectController',
  'LoginController',
  'MediaController',
  'MediaRepresentativeActivationController',
  'PageNotFoundController',
  'RegisterController',
  'RegisterUserController',
  'ResetPasswordController',
  'UpdateController',
  'UserActivationController',
  'UserResetActivationController',
  'VeranstaltungsController'
].forEach(function(name){
  var modulePath = "./controllers/"+name.substring(0,1).toLowerCase()+name.substring(1);
  app.controller(name,require(modulePath));
});
*/

Object.keys(controllers).forEach(function(fileName){
  var controllerName = fileName.substring(0,1).toUpperCase()+fileName.substring(1);
  app.controller(controllerName,controllers[fileName]);
});


app.run(function ($rootScope) {
    $rootScope.parseDate = function (dt) {
            return moment(dt).toDate();
    };

    $rootScope.isUserLoged = function () {
        return $rootScope.user_logged_in != null;
    };

    $rootScope.cleanMessages = function() {
        $rootScope.button = false;
        $rootScope.status = null;
        $rootScope.previousMessage = null;
        $rootScope.messageContent = null;
        $rootScope.error=null;
        $rootScope.success=null;
    };

    $rootScope.cleanImageControls = function () {
        $rootScope.correctImageFormat = true;
        $rootScope.correctImageSize = true;
    };
    //Only required for LoginController
    setStatus = null;
});



app.config(function ($routeProvider, $translateProvider) {
    $routeProvider.when('/login', {
        templateUrl: 'partials/login.html',
        controller: 'LoginController'
    }).when('/login/:delegation', {
        templateUrl: 'partials/login.html',
        controller: 'LoginController'
    }).when('/event', {
        templateUrl: 'partials/event.html',
        controller: 'EventController'
    }).when('/register/:eventId', {
        templateUrl: 'partials/register.html',
        controller: 'RegisterController'
    }).when('/register/:eventId/:noLoginRequiredUUID', {
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
    }).when('/freevisitors/:uuid/:noLoginRequiredUUID', {
        template: "",
        controller: 'FreeVisitorController'
    }).when('/update/:eventId', {
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
    }).otherwise({
        redirectTo: '/event'
    });

    $translateProvider.useStaticFilesLoader({
        prefix: 'languages/lang-',
        suffix: '.json'
    });

    $translateProvider.preferredLanguage('de_DE');
});


app.config(['flowFactoryProvider', function (flowFactoryProvider) {
    flowFactoryProvider.defaults = {
        permanentErrors: [404, 500, 501],
        maxChunkRetries: 1,
        chunkRetryInterval: 5000,
        simultaneousUploads: 4,
        singleFile: true
    };
}]);

//Datepicker configuration
app.config(function($mdDateLocaleProvider) {
    $mdDateLocaleProvider.firstDayOfWeek = 1;
});

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
app.factory("param", function(){
  return param;
});
app.factory("moment", function(){
  return moment; 
});
app.factory("vwoa", function($rootScope, $location, $timeout, $translate){
    var UnexpectedStatusError =  function(expectedStatus,actualStatus){
        this.expectedStatus=expectedStatus;
        this.status=actualStatus;
        this.message="Expected: "+expectedStatus+", actual: "+actualStatus;
        this.name="UnexpectedStatusError";
    };
    UnexpectedStatusError.prototype = Error.prototype;

    var expectStatus = function(expectedStatus, messages){
        return function(result){
            if (result.data.status != expectedStatus) {
                throw new UnexpectedStatusError(expectedStatus,result.data.status);
            }
            return result;
        };
    };
    
    var lookup = function(obj){
        if(typeof obj == "object"){
            if(obj.hasOwnProperty(result.data.status)){
                return obj[result.data.status];
            } else if (obj.hasOwnProperty('*')){
                return obj['*']
            } else {
                return null;
            }
        }

        return obj;
    };

    var handleResponse = function(msg0,redirect0){
        return function(result){
            var message = lookup(msg0), redirect=lookup(redirect0);

            $translate(message).then(function (text) {
                $rootScope.success = text;
                $timeout(function(){
                    $rootScope.cleanMessages();
                }, 5000);
            });
            if(redirect){
                $location.path(redirect);
            }
        };
    };

    return {
        expectStatus: expectStatus,
        handleResponse: handleResponse
    };
});
