module.exports = function($http, $scope, $location, $routeParams, show) {
    if ($routeParams.noLoginRequiredUUID != null) {
        var freeVisitorsUrl = 'api/freevisitors/'+ $routeParams.uuid + '/' + $routeParams.noLoginRequiredUUID
    } else {
        var freeVisitorsUrl = 'api/freevisitors/'+ $routeParams.uuid
    }
    $http({
        method: 'GET',
        url: freeVisitorsUrl
    }).success(function (result) {
        if(result.status != 'ERROR') {
            $location.path('/register/' + result.status);
        } else {
            $location.path('/page_not_found');
        }
    }).error(function (data, status, headers, config) {
        show.error("GENERIC_ERROR");
    });
};
