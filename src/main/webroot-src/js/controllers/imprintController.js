module.exports = function($scope, $http, $rootScope, param) {
    $http.get('api/imprint/' ).success(function (result) {
        $scope.parts = result;
    });
};