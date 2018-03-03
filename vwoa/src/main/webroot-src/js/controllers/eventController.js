module.exports = function($scope, $http, $rootScope, param) {
    $http.get('api/event/list' ).then(function (result) {
        $scope.events = result.data;
    });
};
