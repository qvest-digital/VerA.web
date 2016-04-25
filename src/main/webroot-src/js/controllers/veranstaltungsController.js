module.exports = function($scope, $http, $rootScope, $location) {
  $scope.success = null;
  $scope.error = null;
  $rootScope.cleanMessages();


  var userEventsURL = 'api/event/userevents/' + $rootScope.user_logged_in;
  $http.get(userEventsURL).success(function(result) {
    $scope.events = result;
  });

};
