/**
 * Created by mley on 21.07.14.
 */

var onlineRegApp = angular.module('onlineRegApp', [ 'ngRoute', 'ui.bootstrap', 'pascalprecht.translate', 'flow', 'flow.img', 'flow.init', 'flow.provider' ]);

onlineRegApp.run(function ($rootScope) {
    $rootScope.parseDate = function (dt) {
            return moment(dt).toDate();
    };

    $rootScope.isUserLoged = function () {
        return $rootScope.user_logged_in != null;
    }

    $rootScope.cleanMessages = function() {
        $rootScope.button = false;
        $rootScope.status = null;
        $rootScope.previousMessage = null;
        $rootScope.messageContent = null;
        $rootScope.error=null;
        $rootScope.success=null;
    }

    $rootScope.cleanImageControls = function () {
        $rootScope.correctImageFormat = true;
        $rootScope.correctImageSize = true;
    }
    //Only required for LoginController
    setStatus = null;
});

onlineRegApp.config(function ($routeProvider, $translateProvider) {
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
    }).when('/user/resend/confirmationmail/:userName', {
        template: "",
        controller: 'UserRefreshActivationController'
    }).otherwise({
        redirectTo: '/event'
    })

    $translateProvider.useStaticFilesLoader({
        prefix: 'languages/lang-',
        suffix: '.json'
    });

    $translateProvider.preferredLanguage('de_DE');

});


onlineRegApp.config(['flowFactoryProvider', function (flowFactoryProvider) {
    flowFactoryProvider.defaults = {
        permanentErrors: [404, 500, 501],
        maxChunkRetries: 1,
        chunkRetryInterval: 5000,
        simultaneousUploads: 4,
        singleFile: true
    };
}]);

onlineRegApp.directive('equals', function() {
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
