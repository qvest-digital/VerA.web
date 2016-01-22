onlineRegApp.controller('UserActivationController', function($http, $scope, $rootScope, $routeParams, $location, $translate) {
    var activateUserUrl = 'api/user/activate/' + $routeParams.activation_token
    $http({
        method: 'GET',
        url: activateUserUrl
    }).success(function (result) {
        if (result.status == 'OK') {
            $translate('ACTIVATION_USER_MESSAGE_SUCCESS').then(function (text) {
                $rootScope.success = text;
                $rootScope.error = null;
            });
        } else if (result.status == 'LINK_INVALID') {
            $translate('ACTIVATION_USER_MESSAGE_LINK_INVALID').then(function (text) {
                $rootScope.success = null;
                $rootScope.error = text;
            });
        } else if (result.status == 'LINK_EXPIRED') {
            $translate('ACTIVATION_USER_MESSAGE_LINK_EXPIRED').then(function (text) {
                $rootScope.success = null;
                $rootScope.error = text;
            });
        }
        $location.path('/login');
    }).error(function (data, status, headers, config) {
        $translate('ACTIVATION_USER_MESSAGE_FAILED').then(function (text) {
            $rootScope.error = text;
        });
    });

});