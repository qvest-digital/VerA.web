onlineRegApp.controller('DirectLoginController',
                        function ($scope, $location, $http, $rootScope, $translate, $routeParams) {
    $scope.button = false;
    $rootScope.cleanMessages();

    $scope.logout = function () {
        $http({
            method: 'POST',
            url: 'api/idm/logout/'
        }).success(function (result) {
            $rootScope.cleanMessages();
            $rootScope.error = null;
            $scope.directusername = null;
            $scope.directpassword = null;

            $translate('GENERIC_LOGOUT_SUCCESSFUL_MESSAGE').then(function (text) {
                $rootScope.messageContent = text;
            });

            $rootScope.status = "success";
            $rootScope.user_logged_in = null;
            $rootScope.userinfo = null;
            $location.path("/");
        }).error(function (data, status, headers, config) {
        });
    }

    $scope.direct_login = function () {
        $scope.button = true;

        $http({
            method: 'POST',
            url: 'api/idm/login/' + encodeURIComponent($scope.directusername),
            headers: {"Content-Type" : undefined},
            data: $.param({
                    password: $scope.directpassword,
                    delegation: $routeParams.delegation
            })
        }).success(function (result) {
            $scope.button = false;
            $rootScope.error = null;
            if (result != "") {
              if(result.status.localeCompare("disabled")||result.status=="disabled"){
                   $scope.status=42;
                   $location.path($scope.nextPage);
               }else{
                    $rootScope.userinfo = result.status;
                    $rootScope.user_logged_in = $scope.directusername;
                    $rootScope.status = null;
                    $rootScope.messageContent = null;
                    $location.path($scope.nextPage);
                }
            } else {
                $rootScope.userinfo = null;
                $translate('GENERIC_MESSAGE_USER_OR_PASSWORD_WRONG').then(function (text) {
                    $rootScope.messageContent = text;
                });
                $rootScope.status = "danger";
            }
        }).error(function (data, status, headers, config) {
            $scope.button = false;
            $translate('GENERIC_ERROR').then(function (text) {
                $rootScope.messageContent = text;
            });
            $rootScope.status = "danger";
        });
    }

    $scope.setStatus = function(value) {
            $scope.x = value;
    }

    $scope.setMessageContent = function(value) {
            $scope.x = value;
    }

    $scope.setNextPage = function(value) {
        $scope.nextPage = "/" + value;
    }
});