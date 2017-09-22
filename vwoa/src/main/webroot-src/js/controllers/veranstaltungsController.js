module.exports = function($scope, $http, $location) {

  var userEventsURL = 'api/event/userevents';
  $http.get(userEventsURL).success(function(result) {
    $scope.events = result;
  });

};
