module.exports = function($scope) {
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
  return {
    putInScope: putInScope
  }
};
