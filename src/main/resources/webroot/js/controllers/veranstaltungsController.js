onlineRegApp.controller('VeranstaltungsController', function ($scope, $http, $rootScope, $location) {
    $scope.success = null;
    if($rootScope.previousErrorMessage != null && $rootScope.previousErrorMessage != "") {
        $scope.error = $rootScope.previousErrorMessage;
    } else {
        $scope.error = null;
    }

    $rootScope.previousErrorMessage = null;

    if ($rootScope.user_logged_in == null) {
        lastPageRegisterPath = $location.path();
        $location.path('/login');
        $rootScope.success = null;
    } else {
        var userEventsURL = 'api/event/userevents/' + $rootScope.user_logged_in;
        $http.get(userEventsURL).success(function (result) {
            $scope.events = result;
        });
    }
});