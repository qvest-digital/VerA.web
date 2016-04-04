module.exports = function($http, $scope, $rootScope, $routeParams, $location, $translate) {
    var activatePressUserUrl = 'api/media/activation/confirm/' + $routeParams.pressUserActivationToken
    $http({
        method: 'GET',
        url: activatePressUserUrl
    }).success(function (result) {
        if (result.status == 'OK') {
            $translate('MEDIA_ACTIVATION_USER_MESSAGE_SUCCESS').then(function (text) {
                $rootScope.success = text;
                $rootScope.error = null;
            });
        } else if (result.status == 'PRESS_USER_ALREADY_ACTIVATED') {
            $translate('MEDIA_REPRESEINTATIVES_ACTIVATION_ALREADY_ACTIVATED').then(function (text) {
               $rootScope.success = text;
               $rootScope.error = null;
            });
        }
        $location.path('/login');
    }).error(function (data, status, headers, config) {
        $translate('MEDIA_ACTIVATION_USER_MESSAGE_FAILED').then(function (text) {
            $rootScope.error = text;
        });
    });

};
