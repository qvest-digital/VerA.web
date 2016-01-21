onlineRegApp.controller('UserActivationController', function($http, $scope, $rootScope, $routeParams, $location, $translate) {
    var activateUserUrl = 'api/user/activate/' + $routeParams.activation_token
    $http({
        method: 'GET',
        url: activateUserUrl
    }).success(function (result) {
        $translate('ACTIVATION_USER_MESSAGE_SUCCESS').then(function (text) {
            $rootScope.success = text;
        });
        $location.path('/login');
    }).error(function (data, status, headers, config) {
        $translate('ACTIVATION_USER_MESSAGE_FAILED').then(function (text) {
            $rootScope.error = text;
        });
    });
});