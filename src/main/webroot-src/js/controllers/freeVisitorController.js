module.exports = function($http, $scope, $location, $routeParams, show) {
    if ($routeParams.noLoginRequiredUUID != null) {
        var status = 'api/freevisitors/'+ $routeParams.noLoginRequiredUUID;
         if(status != 'ERROR') {
                    $location.path('/register/' + $routeParams.uuid + '/' + $routeParams.noLoginRequiredUUID);
                } else {
                    $location.path('/page_not_found');
                }
    } else {
        $location.path('/register/' + $routeParams.uuid);
    }
};
