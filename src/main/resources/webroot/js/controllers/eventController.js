onlineRegApp.controller('EventController', function ($scope, $http, $rootScope) {
    $scope.success = null;
    $scope.error = null;
    $rootScope.cleanMessages();
    $scope.setNextPage('veranstaltungen');
    $http.get('api/event/list/' + $rootScope.user_logged_in).success(function (result) {
        $scope.events = result;
    });
});