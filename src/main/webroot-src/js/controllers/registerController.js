onlineRegApp.controller('RegisterController', function ($scope, $rootScope, $location, $routeParams, $http, $translate) {
    $scope.success = null;
    $scope.error = null;
    $scope.registerButton = true;

    $http.get('api/event/guestlist/status/' + $routeParams.eventId).success(function(result) {
        //save result.status in scope for next functions
        $scope.guestStatus = result.status;
        //second status to save status of registering in waiting list
        $scope.registeredOnWaitingList = result.status;

        if (result.status === 'WAITING_LIST_FULL' && $routeParams.noLoginRequiredUUID == null) {
            $scope.registerButton = false;

            $translate('REGISTER_USER_MESSAGE_EVENT_FULL').then(function (text) {
                $scope.error = text;
            });
        }
        else {
            $scope.registerButton = true;
        }
    });

    if ($rootScope.user_logged_in == null && $routeParams.noLoginRequiredUUID == null) {
        $scope.setNextPage('register/' + $routeParams.eventId);
        $location.path('/login');
    } else {

        $http.get('api/event/registered/' + $rootScope.user_logged_in + '/' + $routeParams.eventId)
        .success(function (isUserRegistered) {

            if (!isUserRegistered) {
                $scope.noLoginRequiredUUID = $routeParams.noLoginRequiredUUID;

                $http.get('api/event/' + $routeParams.eventId).success(function (result) {
                    $scope.event = result;
                });
            }
            else {
                // redirect to update site because the user is already registered
                $location.path('/update/' + $routeParams.eventId);
            }
        });

//        $http.get('api/event/' + $routeParams.eventId + '/register/' + $scope.userId).success(function (result) {
//            if (!isUserLoged()) {
//                $location.path('/login');
//            } else {
//                if (result.invitationstatus) {
//                    $scope.acceptance = $scope.acceptanceOptions[result.invitationstatus];
//                }
//                if (result.notehost) {
//                    $scope.noteToHost = result.notehost;
//                }
//            }
//        });

        $scope.save = function () {
            if ($scope.noLoginRequiredUUID == null) {
                $http({
                    method: 'POST',
                    url: 'api/event/' + $routeParams.eventId + '/register',
                    headers: {"Content-Type" : undefined},
                    data: $.param({
                        notehost: $scope.noteToHost,
                        guestStatus: $scope.guestStatus
                    })
                }).success(function (result) {
                    if ($scope.registeredOnWaitingList === 'WAITING_LIST_OK') {
                        $translate('REGISTER_USER_MESSAGE_TO_RESERVE_LIST').then(function (text) {
                            $rootScope.previousErrorMessage = text;
                        });
                        $scope.setNextPage('veranstaltungen');
                        $location.path($scope.nextPage);
                    }
                    else if (result.status === 'OK') {
                        $translate(['USER_EVENT_REGISTER_MESSAGE_SUCCESSFUL_PART_ONE','USER_EVENT_REGISTER_MESSAGE_SUCCESSFUL_PART_TWO']).then(function (translations) {
                            $rootScope.previousMessage = translations['USER_EVENT_REGISTER_MESSAGE_SUCCESSFUL_PART_ONE'] + " \"" + $scope.event.shortname + "\" " + translations['USER_EVENT_REGISTER_MESSAGE_SUCCESSFUL_PART_TWO'];
                        });
                        $scope.setNextPage('veranstaltungen');
                        $location.path($scope.nextPage);
                    } else if (result.status === 'REGISTERED') {
                        $translate('USER_EVENTS_STATUS_CHANGED_ERROR_MESSAGE').then(function (text) {
                            $scope.error = text;
                        });
                    }
                });
            } else {
                $http({
                    method: 'POST',
                    url: 'api/event/' + $routeParams.eventId + '/register/nologin',
                    headers: {"Content-Type" : undefined},
                    data: $.param({
                        notehost: $scope.noteToHost,
                        noLoginRequiredUUID: $scope.noLoginRequiredUUID
                    })
                }).success(function (result) {
                    if (result.status === 'OK') {
                        $scope.error = null;
                        $translate(['USER_EVENT_REGISTER_MESSAGE_SUCCESSFUL_PART_ONE','USER_EVENT_REGISTER_MESSAGE_SUCCESSFUL_PART_TWO']).then(function (translations) {
                            $scope.success = translations['USER_EVENT_REGISTER_MESSAGE_SUCCESSFUL_PART_ONE'] + " \"" + $scope.event.shortname + "\" " + translations['USER_EVENT_REGISTER_MESSAGE_SUCCESSFUL_PART_TWO'];
                        });
                        $scope.noteToHost = null;
                    } else if (result.status === 'REGISTERED') {
                        $scope.success = null;
                        $translate('USER_EVENTS_STATUS_WITHOUT_LOGIN_CHANGED_ERROR_MESSAGE').then(function (text) {
                            $scope.error = text;
                        });
                    }
                });
            }

        }
    }
});
