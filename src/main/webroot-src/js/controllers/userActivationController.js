module.exports = function($http, $routeParams, expectStatus, $location, $scope, show) {

  var handleResponse = function(msg0, redirect0,forceError) {
    return function(result) {
      var lookup = function(obj) {
        if (typeof obj == "object") {
          if (obj.hasOwnProperty(result.data.status)) {
            return obj[result.data.status];
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
      handleResponse('ACTIVATION_USER_MESSAGE_SUCCESS', '/login'),
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
