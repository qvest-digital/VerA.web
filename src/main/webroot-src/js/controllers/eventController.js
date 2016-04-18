module.exports = function($scope, $http, $rootScope, param) {
    $scope.setNextPage('veranstaltungen');
    $http.get('api/event/list/' + $rootScope.user_logged_in).success(function (result) {
        $scope.events = result;
    });
};
