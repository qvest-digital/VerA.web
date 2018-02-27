module.exports=function($scope, authService, show) {
  var tools = require("../../scope-tools")($scope);
  $scope.login = function(){
    authService
    .login($scope.user.accountName, $scope.user.password)
    .then(tools.putInScope("user"),show.error);
  };

  $scope.logout = function(){
    authService
    .logout()
    .then(tools.putInScope("user"),show.error);
  };


  $scope.setNextPage = function(){
  };
  authService.queryStatus().then(tools.putInScope("user"));
  $scope.$on("vwoa-auth:authentication-state-changed",function(ev,data){
    tools.putInScope("user")(data);
  });
};
