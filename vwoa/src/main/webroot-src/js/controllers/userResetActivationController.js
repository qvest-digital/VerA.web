module.exports = function($http, $scope, $rootScope, $routeParams, $location, $translate, param) {
    var resetUserActivationUrl = 'api/user/update/activation/data';
    $location.path('/login');
    $http({
        method: 'POST',
        url: resetUserActivationUrl,
        headers: {"Content-Type" : undefined},
        data: param({
            activation_token: $routeParams.activation_token,
            language: $translate.use()
        })
    }).success(function (result) {
      $rootScope.error = null;
      $translate('ACTIVATION_RESEND_USER_MESSAGE_SUCCESS').then(function (text) {
          $rootScope.success = text;
      });
    }).error(function (data, status, headers, config) {
      $rootScope.success = null;
      $translate('ACTIVATION_RESEND_USER_MESSAGE_FAILED').then(function (text) {
          $rootScope.error = text;
      });
    });
};
