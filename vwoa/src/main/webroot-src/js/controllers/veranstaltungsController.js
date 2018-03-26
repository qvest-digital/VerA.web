module.exports = function($scope, $http, $location) {

  var userEventsURL = 'api/event/userevents';
  $http.get(userEventsURL).then(function(result) {
    $scope.events = result.data;
  });

};
