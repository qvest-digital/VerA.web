onlineRegApp.controller('UserRefreshActivationController', function($http, $scope, $rootScope, $routeParams, $location, $translate) {
    var resetUserActivationUrl = 'api/user/update/activation/data';
    $location.path('/login');
    $http({
        method: 'POST',
        url: resetUserActivationUrl,
        headers: {"Content-Type" : undefined},
        data: $.param({
            username: $routeParams.userName
        })
    }).success(function (result) {
      $translate('ACTIVATION_RESEND_USER_MESSAGE_SUCCESS').then(function (text) {
          $rootScope.success = text;
      });
    }).error(function (data, status, headers, config) {
      $translate('ACTIVATION_RESEND_USER_MESSAGE_FAILED').then(function (text) {
          $rootScope.error = text;
      });
    });
});
