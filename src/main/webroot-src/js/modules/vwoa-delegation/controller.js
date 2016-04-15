module.exports = function($scope, delegationService, $routeParams, $http) {
  var putInScope = function(name) {
    if (typeof name == "undefined") {
      return function(obj) {
        Object.keys(obj).forEach(function(name) {
          $scope[name] = obj[name];
        });
      };
    }
    return function(value) {
      $scope[name] = value;
    };
  };
  var showError = function(error) {
    $scope.$emit('vwoa-alerts:message', {
      body: error.message,
      type: 'error'
    });
  };
  var showSuccess = function(message) {
    $scope.$emit('vwoa-alerts:message', {
      body: message || "GENERIC_SUCCESS",
      type: 'success'
    });
  };
  $scope.person = {};
  $scope.loadPersonData = function(pk) {
    delegationService
      .fetchPerson($routeParams.uuid, pk)
      .then(putInScope("person"), showError);
  };
  $scope.register_user = function() {
    delegationService
      .savePerson($routeParams.uuid, $scope.person)
      .then(showSuccess,showError)
      .fetchList($routeParams.uuid)
      .then(putInScope("presentPersons"), showError);
  };
  delegationService
    .fetchList($routeParams.uuid)
    .then(putInScope("presentPersons"), showError);
  delegationService
    .fetchMetadata($routeParams.uuid)
    .then(putInScope(), showError);
    
};
