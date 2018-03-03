module.exports = function($http, $scope, $rootScope, $routeParams, $location, show) {
    var activatePressUserUrl = 'api/media/activation/confirm/' + $routeParams.pressUserActivationToken
    $http({
        method: 'GET',
        url: activatePressUserUrl
    }).then(function (result) {
        if (result.status == 'OK') {
          show.success('MEDIA_ACTIVATION_USER_MESSAGE_SUCCESS');
        } else if (result.status == 'PRESS_USER_ALREADY_ACTIVATED') {
          show.success('MEDIA_REPRESEINTATIVES_ACTIVATION_ALREADY_ACTIVATED');
        }
        $location.path('/login');
    }).catch(function (rejection) {
        show.error('MEDIA_ACTIVATION_USER_MESSAGE_FAILED');
    });

};
