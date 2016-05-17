var angular = require('angular');
var moment = require('moment');
var param = require('jquery-param');
var UnexpectedStatusError = require('../unexpected-status-error');

//Moment loads locales via dynamic require calls.
//Since browserify cannot infer such dependencies, we need to help a bit
//We could use bulkify to force them all to be loaded, but that seems a bit over the top?
require('moment/locale/de');
require('moment/locale/fr');
require('moment/locale/es');

module.exports = angular
  .module("vwoa-utilities", [])
  .factory("param", function() {
    return param;
  })
  .factory("moment", function() {
    return moment;
  })
  .factory("UnexpectedStatusError", function(){
    return UnexpectedStatusError;
  })
  .factory("expectStatus", function() {
    return function(expectedStatus, messages) {
      return function(result) {
        console.log("comparing",result.data.status, expectedStatus);
        if (result.data.status != expectedStatus) {
          throw new UnexpectedStatusError(expectedStatus, result.data.status);
        }
        return result;
      };
    };
  })
  .name;
