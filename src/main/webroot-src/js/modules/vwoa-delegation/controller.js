module.exports = function($scope, delegationService, $routeParams, $http, $uibModal) {
  var tools = require("../../scope-tools")($scope);
  $scope.person = {};
  $scope.loadPersonData = function(pk) {
    delegationService
      .fetchPerson($routeParams.uuid, pk)
      .then(putInScope("person"), tools.showError());
  };
  $scope.register_user = function() {
    delegationService
      .savePerson($routeParams.uuid, $scope.person)
      .then(tools.showSuccess(), tools.showError())
      .then(function() {
        return delegationService.fetchList($routeParams.uuid);
      })
      .then(putInScope("presentPersons"), tools.showError());
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
    .then(putInScope("presentPersons"), tools.showError());
  delegationService
    .fetchMetadata($routeParams.uuid)
    .then(putInScope(), tools.showError());

};
