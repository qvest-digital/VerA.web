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
  var show = function(type) {
    return function(data) {
      $scope.$emit('vwoa-alerts:message', {
        body: data && data.message || data,
        type: type
      });
    };
  };
  return {
    show: show,
    showError: function() {
      return show("error");
    },
    showSuccess: function() {
      return show("success");
    },
    putInScope: putInScope
  }
};
