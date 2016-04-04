module.exports = function($scope, $rootScope, $location, $routeParams, $http, $translate, param) {
    if ($rootScope.user_logged_in == null) {

        $scope.setNextPage('register/' + $routeParams.eventId);
        $location.path('/login');
    } else {
        $scope.acceptanceOptions = [
            {id: 0, name:"USER_EVENTS_STATUS_TYPE_OPEN"},
            {id: 1, name:"USER_EVENTS_STATUS_TYPE_ACCEPTANCE"},
            {id: 2, name:"USER_EVENTS_STATUS_TYPE_REFUSE"}
        ];

        $http.get('api/update/' + $routeParams.eventId).success(function (result) {
            $scope.event = result;
            $scope.acceptance = $scope.acceptanceOptions[$scope.event.status];
            $scope.noteToHost = $scope.event.message;
        }).error(function (data, status, headers, config) {
            $translate('USER_EVENTS_STATUS_CHANGED_ERROR_MESSAGE').then(function (text) {
                $scope.error = text;
            });
        });

        $http.get('api/update/isreserve/' + $routeParams.eventId + '/' + $rootScope.user_logged_in).success(function (result) {
            $scope.event = result;
            if (result) {
                $translate('REGISTER_USER_MESSAGE_TO_RESERVE_LIST').then(function (text) {
                    $scope.error = text;
                });
            }
            else $scope.error = null;
        }).error(function (data, status, headers, config) {
            $translate('USER_EVENTS_STATUS_CHANGED_ERROR_MESSAGE').then(function (text) {
                $scope.error = text;
            });
        });

        $scope.update = function () {
            $http({
                method: 'POST',
                url: 'api/update/' + $routeParams.eventId + '/update',
                headers: {"Content-Type" : undefined},
                data: param({
                        notehost: $scope.noteToHost,
                        invitationstatus: $scope.acceptance.id
                })
            }).success(function (result) {
                if (result.status === 'OK') {
                    $translate(['USER_EVENTS_STATUS_CHANGED_SUCCESSFUL_MESSAGE_PART_ONE','USER_EVENTS_STATUS_CHANGED_SUCCESSFUL_MESSAGE_PART_TWO']).then(function (translations) {
                        $rootScope.previousMessage = translations['USER_EVENTS_STATUS_CHANGED_SUCCESSFUL_MESSAGE_PART_ONE'] + " \"" + $scope.event.shortname + "\" " + translations['USER_EVENTS_STATUS_CHANGED_SUCCESSFUL_MESSAGE_PART_TWO'];
                    });

                    $scope.setNextPage('veranstaltungen');
                    $location.path($scope.nextPage);
                } else if (result.status === 'NOT_REGISTERED') {
                    $translate('USER_EVENTS_STATUS_CHANGED_ERROR_MESSAGE').then(function (text) {
                        $scope.error = text;
                    });
                }
            });
        }
    }
};
