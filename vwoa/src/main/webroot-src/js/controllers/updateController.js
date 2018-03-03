module.exports = function($scope, $rootScope, $location, $routeParams, $http, show, param) {
  $scope.acceptanceOptions = [{
    id: 0,
    name: "USER_EVENTS_STATUS_TYPE_OPEN"
  }, {
    id: 1,
    name: "USER_EVENTS_STATUS_TYPE_ACCEPTANCE"
  }, {
    id: 2,
    name: "USER_EVENTS_STATUS_TYPE_REFUSE"
  }];

  var eventId = undefined;

  $http.get('api/event/uuid/' + $routeParams.eventUuid).then(function(result) {
    if(result.status != 'ERROR') {
      eventId = result.status;

      $http.get('api/update/' + eventId).then(function(result) {
        $scope.event = result.data;
        $scope.acceptance = $scope.acceptanceOptions[$scope.event.status];
        $scope.noteToHost = $scope.event.message;
      }).catch(function (rejection) {
        show.error('USER_EVENTS_STATUS_CHANGED_ERROR_MESSAGE');
      });

      $http.get('api/update/isreserve/' + eventId ).then(function(result) {
        $scope.event = result.data;
        if (result.data) {
          show.error('REGISTER_USER_MESSAGE_TO_RESERVE_LIST');
        } else $scope.error = null;
      }).catch(function (rejection) {
        show.error('USER_EVENTS_STATUS_CHANGED_ERROR_MESSAGE');
      });

      $scope.update = function() {
        $http({
          method: 'POST',
          url: 'api/update/' + eventId + '/update',
          headers: {
            "Content-Type": undefined
          },
          data: param({
            notehost: $scope.noteToHost,
            invitationstatus: $scope.acceptance.id
          })
        }).then(function(result) {
          if (result.status === 'OK') {
            show.success('USER_EVENTS_STATUS_CHANGED_SUCCESSFUL_MESSAGE',{name:$scope.event.shortname});
            $location.path('veranstaltungen');
          } else if (result.status === 'NOT_REGISTERED') {
            show.error('USER_EVENTS_STATUS_CHANGED_ERROR_MESSAGE');
          }
        });
      }
    }else{
      $location.path('/page_not_found');
    }
  });
};
