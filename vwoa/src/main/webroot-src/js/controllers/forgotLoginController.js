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
        }).then(function (result) {
            switch(result.data.status) {
            case 'OK':
                show.success("USER_LOGIN_RESEND_MAIL");
                break;
            default:
                show.error("GENERIC_ERROR");
            }

        }).catch(function (rejection) {
                show.error("GENERIC_ERROR");
        });
  };
};
