module.exports = function($http, $scope, $location, $routeParams, $translate) {
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
        // FIXME Wrong message?
        $translate('GENERIC_MISSING_GENDER_MESSAGE').then(function (text) {
            $scope.error = text;
        });
    });
};
