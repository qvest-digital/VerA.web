module.exports = function($http, $routeParams, expectStatus, $location, $scope, show) {

  var handleResponse = function(msg0, redirect0,forceError) {
    return function(result) {
      var status;
      if(result instanceof Error || forceError){
        status = result.status;
      }else if(typeof result === "object" || result.hasOwnProperty("data") ){
        status = result.data.status;
      }
      var lookup = function(obj) {
        if (typeof obj == "object") {
          if (obj.hasOwnProperty(status)) {
            return obj[status];
          } else if (obj.hasOwnProperty('*')) {
            return obj['*']
          } else {
            return null;
          }
        }

        return obj;
      };

      var message = lookup(msg0),
        redirect = lookup(redirect0);
      if (redirect) {
        $location.path(redirect);
      }
      var type;
      if (forceError || (result instanceof Error)) {
        show.error(message);
      } else {
        show.success(message);
      }
    };
  };

  $http.get('api/user/activate/' + $routeParams.activation_token)
    .then(expectStatus('OK'))
    .then(
      handleResponse('ACTIVATION_USER_MESSAGE_SUCCESS', '/veranstaltungen'),
      handleResponse({
        LINK_INVALID: 'ACTIVATION_USER_MESSAGE_LINK_INVALID',
        LINK_EXPIRED: 'ACTIVATION_USER_MESSAGE_LINK_EXPIRED',
        '*': 'ACTIVATION_USER_MESSAGE_FAILED'
      }, {
        LINK_INVALID: '/login',
        LINK_EXPIRED: '/login'
      },true)
    );
};
