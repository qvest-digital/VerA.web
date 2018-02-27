module.exports = function($scope, $http, $location, param, show, $translate) {

        //<div ng-show="status == 'e'" class="alert alert-danger" role="alert">{{'GENERIC_ERROR'|translate}}</div>
                //<div ng-show="status == 'e1'" class="alert alert-danger" role="alert">{{'REGISTER_USER_MESSAGE_USER_EXISTS_ERROR'|translate}}</div>
                //<div ng-show="status == 'e2'" class="alert alert-danger" role="alert">{{'REGISTER_USER_MESSAGE_VALIDATION_ERROR'|translate}}</div>

    $scope.register = function(isValid) {
        if(!isValid) { return; }
        $http({
            method: 'POST',
            url: 'api/user/register/' + encodeURIComponent($scope.osiam.userName) + '',
                headers: {"Content-Type" : undefined},
                data: param({
                osiam_firstname: $scope.osiam.firstName,
                osiam_secondname: $scope.osiam.lastName,
                osiam_password1: $scope.osiam.password,
                osiam_email: $scope.osiam.email,
                current_language: $translate.use()
            })
        }).success(function (result) {
            switch(result.status) {
            case 'OK':
                $location.path('/login');
                show.success("REGISTER_USER_MESSAGE_SUCCESS");
                break;
            case 'USER_EXISTS':
                show.error("REGISTER_USER_MESSAGE_USER_EXISTS_ERROR");
                break;
            case 'INVALID_USERNAME':
                show.error("REGISTER_USER_MESSAGE_VALIDATION_ERROR");
                break;
            default:
                show.error("GENERIC_ERROR");
            }

        }).error(function (data, status, headers, config) {
                show.error("GENERIC_ERROR");
        });
    };
};
