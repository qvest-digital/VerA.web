module.exports = function($http, $scope, $location, show, $timeout, param, $translate) {
  $scope.forgotPassword = function() {
        $http({
            method: 'POST',
            // TODO: REST-API call ggf. anpassen
            url: 'api/user/request/reset-password-link',
                headers: {
                    "Content-Type" : undefined
                },
                data: param({
                    username: encodeURIComponent($scope.osiam.userName),
                    current_language: $translate.use()
            })
        }).success(function (result) {
            switch(result.status) {
            case 'OK':
                show.success("USER_PASSWORD_RESET_MAIL");
                break;
            case 'USER_NOT_EXISTS':
                show.error("RESET_MAIL_WAS_NOT_SEND");
                break;
            case 'MORE_THAN_ONE_LINKUUID':
                show.error("MORE_THAN_ONE_LINKUUID");
                break;
            default:
                show.error("GENERIC_ERROR");
            }

        }).error(function (data, status, headers, config) {
                show.error("GENERIC_ERROR");
        });
  };
};
