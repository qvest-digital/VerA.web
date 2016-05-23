module.exports = function($scope, $rootScope, $location, $routeParams, $http, show, param) {

  $http.get('api/event/guestlist/status/' + $routeParams.eventId).success(function(result) {
    //save result.status in scope for next functions
    $scope.guestStatus = result.status;
    //second status to save status of registering in waiting list
    $scope.registeredOnWaitingList = result.status;

    if (result.status === 'WAITING_LIST_FULL' && $routeParams.noLoginRequiredUUID == null) {
      $scope.registerButton = false;

      show.error('REGISTER_USER_MESSAGE_EVENT_FULL');
    } else {
      $scope.registerButton = true;
    }
  });


  $http.get('api/event/registered/' + $routeParams.eventId)
    .success(function(isUserRegistered) {

      if (!isUserRegistered) {
        $scope.noLoginRequiredUUID = $routeParams.noLoginRequiredUUID;

        $http.get('api/event/' + $routeParams.eventId).success(function(result) {
          $scope.event = result;
        });
      } else {
        // redirect to update site because the user is already registered
        $location.path('/update/' + $routeParams.eventId);
      }
    });


  $scope.save = function() {
    if ($scope.noLoginRequiredUUID == null) {
      $http({
        method: 'POST',
        url: 'api/event/' + $routeParams.eventId + '/register',
        headers: {
          "Content-Type": undefined
        },
        data: param({
          notehost: $scope.noteToHost,
          guestStatus: $scope.guestStatus
        })
      }).success(function(result) {
        if ($scope.registeredOnWaitingList === 'WAITING_LIST_OK') {
          show.success('REGISTER_USER_MESSAGE_TO_RESERVE_LIST');
          $location.path('veranstaltungen');
        } else if (result.status === 'OK') {
          show.success('USER_EVENT_REGISTER_MESSAGE_SUCCESSFUL',{name:$scope.event.shortname});
          $location.path('veranstaltungen');
        } else if (result.status === 'REGISTERED') {
          show.error('USER_EVENTS_STATUS_CHANGED_ERROR_MESSAGE');
        }
      });
    } else {
      $http({
        method: 'POST',
        url: 'api/event/' + $routeParams.eventId + '/register/nologin',
        headers: {
          "Content-Type": undefined
        },
        data: param({
          notehost: $scope.noteToHost,
          noLoginRequiredUUID: $scope.noLoginRequiredUUID
        })
      }).success(function(result) {
        if (result.status === 'OK') {
          show.success('REGISTER_USER_MESSAGE_TO_RESERVE_LIST');
          $location.path('veranstaltungen');
          $scope.noteToHost = null;
        } else if (result.status === 'REGISTERED') {
          show.error('USER_EVENTS_STATUS_WITHOUT_LOGIN_CHANGED_ERROR_MESSAGE');
        }
      });
    }

  }

};
