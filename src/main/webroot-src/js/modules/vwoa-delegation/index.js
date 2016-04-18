var angular = require("angular");

var delegation = angular.module("vwoa-delegation", [
  require('../vwoa-alerts'),
  require('../vwoa-utilities'),
  require('angular-route'),
  require('angular-ui-bootstrap')
]);
delegation.directive('extraField', function() {
  return {
    restrict: 'E',
    scope: {
      field: '=field',
      person: '=person'
    },
    templateUrl: 'partials/extra-field.html'
  };
});
delegation.directive('imageUpload', require("./image-upload"));

delegation.factory("delegationService", require("./service"));

delegation.controller("delegationController", require("./controller"));

module.exports = delegation.name;
