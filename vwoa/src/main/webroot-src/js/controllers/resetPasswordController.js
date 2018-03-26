module.exports = function($http, $scope, $routeParams, $location, $rootScope, show, $timeout, vwoa, param) {
  $scope.resetPassword = function() {
    if ($scope.resetPasswordForm.password.$viewValue != $scope.resetPasswordForm.passwordRepeat.$viewValue) {
      show.error('REGISTER_USER_MESSAGE_PASSWORD_REPEAT_ERROR');
    } else {
      $http({
        method: 'POST',
        url: 'api/reset/password/' + $routeParams.uuid,
        headers: {
          "Content-Type": undefined
        },
        data: param({
          password: $scope.resetPasswordForm.password.$viewValue
        })
      }).then(function(result) {
        if (result.status === "OK") {
          show.success('USER_PASSWORD_CHANGE_SUCCESS_MESSAGE');
          $location.path('/event');
        } else {
          // TODO: für Status "WRONG_LINK", "OK" UND "GETTING_USER_FAILED" braucht man je eine Hinweismeldung
          show.error("GENERIC_ERROR")
        }
      }).catch(function (rejection) {
        show.error("GENERIC_ERROR")
      });
    }
  };
};
