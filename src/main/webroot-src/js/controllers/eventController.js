module.exports = function($scope, $http, $rootScope, param) {
    $http.get('api/event/list' ).success(function (result) {
        $scope.events = result;
    });
};
