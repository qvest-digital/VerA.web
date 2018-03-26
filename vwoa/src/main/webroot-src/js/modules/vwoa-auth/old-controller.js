module.exports = function($scope, $location, $http, $rootScope, $translate, $routeParams, param) {
    //setStatus will be set to 1 by RegisterUserController, if
    //registering was successful
    if(setStatus == 1|| setStatus==42) {
        $scope.status = setStatus;
        setStatus = null;
    }else{
        $scope.status=null;
    }

    $scope.login = function () {
        $scope.button = true;

        $http({
            method: 'POST',
            url: 'api/idm/login/' + encodeURIComponent($scope.username),
            headers: {"Content-Type" : undefined},
            data: param({
                password: $scope.password,
                delegation: $routeParams.delegation
            })
        }).then(function (result) {
            $scope.error = null;
            $scope.button = false;
           if (result.data != "") {//result=object
               if(result.data.status.localeCompare("disabled")==0){
                   $rootScope.protocol=result.data.protocol;
                   $rootScope.host=result.data.host;
                   $rootScope.port=result.data.port;
                   $rootScope.suffix=result.data.suffix;
                   setStatus=42;
                   $scope.status=42;
                   $rootScope.status= null;
                   $rootScope.error=null;
                   $rootScope.inactiveLoginAttemptBy=$scope.username;
                   $location.path($scope.nextPage);
               }else{
                   $rootScope.user_logged_in = $scope.username;
                   $rootScope.userinfo = result.data.status;
                   $rootScope.status = null;
                   $rootScope.messageContent = null;
                   $location.path($scope.nextPage);
               }
            } else {
                $rootScope.status = "danger";
                setStatus=null;
                $scope.status=null;
                $translate('GENERIC_MESSAGE_USER_OR_PASSWORD_WRONG').then(function (text) {
                    $rootScope.messageContent = text;
                });
            }
        }).catch(function (rejection) {
            $scope.button = false;
            $rootScope.status = "danger";
            $translate('GENERIC_ERROR').then(function (text) {
                $rootScope.messageContent = text;
            });
        });

        $rootScope.resendConfirmationMail = function() {
          username=$rootScope.inactiveLoginAttemptBy;

          $location.path('/login');
          //change token+sxp date
          //getnewurl
          //sendmail
          //confirm msg
        }
    }
};
