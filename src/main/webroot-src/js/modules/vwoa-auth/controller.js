module.exports=function($scope, authService) {
  var tools = require("../../scope-tools")($scope); 
  $scope.login = function(){
    authService
    .login($scope.user.accountName, $scope.user.password)
    .then(tools.putInScope("user"),tools.showError());
  };

  $scope.logout = function(){
    authService
    .logout()
    .then(tools.putInScope("user"),tools.showError());
  };


  $scope.setNextPage = function(){
    console.log("yay, legacy call.");
  };
  authService.queryStatus().then(tools.putInScope("user"));
  $scope.$on("vwoa-auth:authentication-state-changed",function(ev,data){
    console.log("yep",data);
    tools.putInScope("user")(data);
  });

};
