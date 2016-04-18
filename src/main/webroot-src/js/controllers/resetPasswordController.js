module.exports = function($http, $scope, $routeParams, $location, $rootScope, $translate, $timeout, vwoa, param) {
    $scope.resetPassword = function() {
        if ($scope.resetPasswordForm.password.$viewValue != $scope.resetPasswordForm.passwordRepeat.$viewValue) {
            $translate('REGISTER_USER_MESSAGE_PASSWORD_REPEAT_ERROR').then(function (text) {
                $scope.error = text;
            });
        } else {
            $scope.error = null;
            $http({
                method: 'POST',
                url: 'api/reset/password/' + $routeParams.uuid,
                headers: {"Content-Type" : undefined},
                data: param({
                    password: $scope.resetPasswordForm.password.$viewValue
                })
            }).success(function () {
                // TODO: für Status "WRONG_LINK", "OK" UND "GETTING_USER_FAILED" braucht man je eine Hinweismeldung
                $translate('USER_PASSWORD_CHANGE_SUCCESS_MESSAGE').then(function (text) {
                    $rootScope.success = text;
                });
                $location.path('/event');
            });
        }
    }
};
