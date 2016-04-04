module.exports = function($scope, $http, $rootScope, $location) {
    $scope.success = null;
    $scope.error = null;
    $rootScope.cleanMessages();

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
};
