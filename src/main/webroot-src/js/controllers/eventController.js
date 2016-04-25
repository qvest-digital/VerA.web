module.exports = function($scope, $http, $rootScope, param) {
    $http.get('api/event/list/' + $rootScope.user_logged_in).success(function (result) {
        $scope.events = result;
    });
};
