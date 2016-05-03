module.exports = function($scope, delegationService, $routeParams, $http, $uibModal, show) {
  var tools = require("../../scope-tools")($scope);
  $scope.person = {};
  $scope.loadPersonData = function(pk) {
    delegationService
      .fetchPerson($routeParams.uuid, pk)
      .then(putInScope("person"), show.error;
  };
  $scope.register_user = function() {
    delegationService
      .savePerson($routeParams.uuid, $scope.person)
      .then(show.success, show.error)
      .then(function() {
        return delegationService.fetchList($routeParams.uuid);
      })
      .then(putInScope("presentPersons"), show.error);
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
    .then(putInScope("presentPersons"), show.error);
  delegationService
    .fetchMetadata($routeParams.uuid)
    .then(putInScope(), show.error);

};
