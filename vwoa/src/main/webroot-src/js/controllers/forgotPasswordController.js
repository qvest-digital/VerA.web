module.exports = function($http, $scope, $location, show, $timeout, param, $translate) {
  $scope.forgotPassword = function() {
        $http({
            method: 'POST',
            url: 'api/user/request/reset-password-link',
                headers: {
                    "Content-Type" : undefined
                },
                data: param({
                    username: encodeURIComponent($scope.osiam.userName),
                    current_language: $translate.use()
            })
        }).then(function (result) {
            switch(result.data.status) {
            case 'OK':
                show.success("USER_PASSWORD_RESET_MAIL");
                break;
            default:
                show.error("GENERIC_ERROR");
            }

        }).catch(function (rejection) {
                show.error("GENERIC_ERROR");
        });
  };
};
