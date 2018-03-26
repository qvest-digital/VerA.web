module.exports = function($scope, delegationService, $routeParams, $http, $uibModal, show) {
  var tools = require("../../scope-tools")($scope);
  var putInScope = tools.putInScope;
  var handleError = function(e){
    if(e.status === 401 ){
      $scope.unauthorized = true;
    } else {
      show.error("GENERIC_ERROR");
    }
  };
  $scope.person = {};
  $scope.loadPersonData = function(pk) {
    delegationService
      .fetchPerson($routeParams.uuid, pk)
      .then(putInScope("person"), handleError);
  };
  $scope.register_user = function() {
    delegationService
      .savePerson($routeParams.uuid, $scope.person)
      .then(show.success, handleError)
      .then(function() {
        return delegationService.fetchList($routeParams.uuid);
      })
      .then(putInScope("presentPersons"), handleError);
  };
  $scope.confirm_reset = function() {
    $uibModal.open({
      templateUrl: 'partials/confirm-reset-modal.html',
      controller: function($uibModalInstance,$scope){
        $scope.ok=function(){
          $uibModalInstance.close();
        };
        $scope.cancel=function(){
          $uibModalInstance.dismiss('cancel');
        };
      }
    })
    .result.then(function(){
      $scope.person = {};
    });
  };
  delegationService
    .fetchList($routeParams.uuid)
    .then(putInScope("presentPersons"), handleError);
  delegationService
    .fetchMetadata($routeParams.uuid)
    .then(putInScope(), handleError);

};
