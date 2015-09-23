onlineRegApp.controller('RegisterUserController', function($scope, $http, $location, $rootScope) {
    $scope.success = null;
    $scope.error = null;
    $rootScope.cleanMessages();

    $scope.status = 0;

    $scope.register = function(isValid) {
        if(!isValid) { return; }
        $http({
            method: 'POST',
            url: 'api/user/register/' + encodeURIComponent($scope.osiam.userName) + '',
                headers: {"Content-Type" : undefined},
                data: $.param({
                osiam_firstname: $scope.osiam.firstName,
                osiam_secondname: $scope.osiam.lastName,
                osiam_password1: $scope.osiam.password
            })
        }).success(function (result) {
            switch(result.status) {
            case 'OK':
                $scope.status = 1;

                setStatus = 1;

                $scope.update = function (parm1, parm2) {
                    $scope.status = parm1 + ": " + parm2;
                };

                break;
            case 'USER_EXISTS':
                $scope.status = 'e1';
                break;
            case 'INVALID_USERNAME':
                $scope.status = 'e2';
                break;
            default:
                $scope.status = 'e';
            }

            if($scope.status == 1) {
                $location.path('/login');
            }

        }).error(function (data, status, headers, config) {
            $scope.status = 'e';
        });
    };
});
