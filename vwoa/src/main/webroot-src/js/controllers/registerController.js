module.exports = function($scope, $rootScope, $location, $routeParams, $http, show, param) {

    var eventId = undefined;

    $http.get('api/event/uuid/' + $routeParams.eventUuid).then(function(result) {
        if(result.data.status != 'ERROR') {
            eventId = result.data.status;

            $http.get('api/event/guestlist/status/' + eventId).then(function(result) {
                //save result.data.status in scope for next functions
                $scope.guestStatus = result.data.status;
                //second status to save status of registering in waiting list
                $scope.registeredOnWaitingList = result.data.status;

                if (result.data.status === 'WAITING_LIST_FULL' && $routeParams.noLoginRequiredUUID == null) {
                    $scope.registerButton = false;

                    show.error('REGISTER_USER_MESSAGE_EVENT_FULL');
                } else {
                    $scope.registerButton = true;
                }
            });

            $http.get('api/event/registered/' + eventId)
                .then(function(isUserRegistered) {

                if (!isUserRegistered.data) {
                    $scope.noLoginRequiredUUID = $routeParams.noLoginRequiredUUID;

                    $http.get('api/event/' + eventId).then(function(result) {
                        $scope.event = result.data;
                    });
                } else {
                    // redirect to update site because the user is already registered
                    $location.path('/update/' + $routeParams.eventUuid);
                }
            });

            $scope.save = function() {
                if ($scope.noLoginRequiredUUID == null) {
                    $http({
                        method: 'POST',
                        url: 'api/event/' + eventId + '/register',
                        headers: {
                        "Content-Type": undefined
                        },
                        data: param({
                            notehost: $scope.noteToHost,
                            guestStatus: $scope.guestStatus
                        })
                    }).then(function(result) {
                        if ($scope.registeredOnWaitingList === 'WAITING_LIST_OK') {
                            show.success('REGISTER_USER_MESSAGE_TO_RESERVE_LIST');
                            $location.path('veranstaltungen');
                        } else if (result.data.status === 'OK') {
                            show.success('USER_EVENT_REGISTER_MESSAGE_SUCCESSFUL',{name:$scope.event.shortname});
                            $location.path('veranstaltungen');
                        } else if (result.data.status === 'REGISTERED') {
                            show.error('USER_EVENTS_STATUS_CHANGED_ERROR_MESSAGE');
                        }
                    });
                } else {
                    $http({
                        method: 'POST',
                        url: 'api/event/' + eventId + '/register/nologin',
                        headers: {
                        "Content-Type": undefined
                        },
                        data: param({
                            notehost: $scope.noteToHost,
                            noLoginRequiredUUID: $scope.noLoginRequiredUUID
                        })
                    }).then(function(result) {
                    if (result.data.status === 'OK') {
                            show.success('REGISTER_USER_MESSAGE_TO_RESERVE_LIST');
                            $location.path('veranstaltungen');
                            $scope.noteToHost = null;
                        } else if (result.data.status === 'REGISTERED') {
                            show.error('USER_EVENTS_STATUS_WITHOUT_LOGIN_CHANGED_ERROR_MESSAGE');
                        }
                    });
                }
            }
        } else {
            $location.path('/page_not_found');
        }
    });
};
