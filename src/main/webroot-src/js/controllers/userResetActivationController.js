onlineRegApp.controller('UserRefreshActivationController', function($http, $scope, $rootScope, $routeParams, $location, $translate) {
    var resetUserActivationUrl = 'api/user/refreshactivationdata/';
        $location.path('/login');
        $test=$rootScope.inactiveLoginAttemptBy;
    $http({
        method: 'POST',
        url: resetUserActivationUrl,
        headers: {"Content-Type" : undefined},
        data: $.param({
            username: $test
        })
    }).success(function (result) {
      $translate('ACTIVATION_USER_MESSAGE_SUCCESS').then(function (text) {
          $rootScope.success = text;
      });
    }).error(function (data, status, headers, config) {
      $translate('ACTIVATION_USER_MESSAGE_FAILED').then(function (text) {
          $rootScope.error = text;
      });
    });

});
