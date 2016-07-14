module.exports = function($http, $scope, $location, show, $timeout, param, $translate) {
  $scope.forgotLogin = function() {
        $http({
            method: 'POST',
            url: 'api/user/request/resend-login',
                headers: {
                    "Content-Type" : undefined
                },
                data: param({
                    email: $scope.osiam.email,
                    current_language: $translate.use()
            })
        }).success(function (result) {
            switch(result.status) {
            case 'OK':
                show.success("USER_LOGIN_RESEND_MAIL");
                break;
            default:
                show.error("GENERIC_ERROR");
            }

        }).error(function (data, status, headers, config) {
                show.error("GENERIC_ERROR");
        });
  };
};
