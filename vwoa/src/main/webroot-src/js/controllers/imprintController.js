module.exports = function($scope, $http, $rootScope, $translate, param) {
    $http({
        method: 'GET',
        url: 'api/imprint/',
        params: {
            current_language: $translate.use()
        }
    }).success(function (result) {
        $scope.parts = result;
    });
};
