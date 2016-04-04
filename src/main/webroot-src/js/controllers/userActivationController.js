onlineRegApp.controller('UserActivationController', function($http, $scope, $rootScope, $routeParams, $location, $translate, vwoa) {
    var activateUserUrl = 'api/user/activate/' + $routeParams.activation_token
    $http({
        method: 'GET',
        url: activateUserUrl
    })
    .then(vwoa.expectStatus('OK'))
    .then(
        vwoa.handleResponse('ACTIVATION_USER_MESSAGE_SUCCESS','/login'),
        vwoa.handleResponse({
            LINK_INVALID: 'ACTIVATION_USER_MESSAGE_LINK_INVALID',
            LINK_EXPIRED: 'ACTIVATION_USER_MESSAGE_LINK_EXPIRED',
            '*':'ACTIVATION_USER_MESSAGE_FAILED'
        }, {
            LINK_INVALID: '/login',
            LINK_EXPIRED: '/login'
        })
    );
});