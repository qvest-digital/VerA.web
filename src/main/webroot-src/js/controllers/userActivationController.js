module.exports = function($http, $routeParams, expectStatus, $login) {
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

  var handleResponse = function(msg0, redirect0) {
    return function(result) {
      var message = lookup(msg0),
        redirect = lookup(redirect0);
      if (result instanceof Error) {
        show.error(message);
      } else {
        show.success(message);
      }
      if (redirect) {
        $location.path(redirect);
      }
    };
  };

  $http.get('api/user/activate/' + $routeParams.activation_token)
    .then(vwoa.expectStatus('OK'))
    .then(
      vwoa.handleResponse('ACTIVATION_USER_MESSAGE_SUCCESS', '/login'),
      vwoa.handleResponse({
        LINK_INVALID: 'ACTIVATION_USER_MESSAGE_LINK_INVALID',
        LINK_EXPIRED: 'ACTIVATION_USER_MESSAGE_LINK_EXPIRED',
        '*': 'ACTIVATION_USER_MESSAGE_FAILED'
      }, {
        LINK_INVALID: '/login',
        LINK_EXPIRED: '/login'
      })
    );
};
