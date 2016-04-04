onlineRegApp.controller('EventController', function ($scope, $http, $rootScope) {
    $scope.setNextPage('veranstaltungen');
    $http.get('api/event/list/' + $rootScope.user_logged_in).success(function (result) {
        $scope.events = result;
    });
});