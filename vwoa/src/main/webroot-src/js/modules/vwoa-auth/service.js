module.exports = function($q, $http, param, $rootScope) {
  var cache = null;
  var updateCache = function(data) {
    cache = data;
    return cache;
  }
  var transformResponse = function(resp) {
    return resp.data;
  };

  var transformError = function(e){
    if(e.status===401){
      throw new Error("GENERIC_MESSAGE_USER_OR_PASSWORD_WRONG");
    }
    throw new Error("GENERIC_ERROR");
  };
  var emit = function(data){
    $rootScope.$broadcast("vwoa-auth:authentication-state-changed",data);
    return data;
  }
  var login = function(username, password) {
    return $http({
        method: 'POST',
        url: 'api/idm/login/' + encodeURIComponent(username),
        headers: {
          "Content-Type": undefined
        },
        data: param({
          password: password
        })
      })
      .then(transformResponse,transformError)
      .then(updateCache)
      .then(emit);
  };
  var logout = function() {

    return $http({
        method: 'POST',
        url: 'api/idm/logout',
        headers: {
          "Content-Type": undefined
        }
      })
      .then(transformResponse)
      .then(updateCache)
      .then(emit);
  };
  var queryStatus = function() {
    if (!cache) {
      return $http.get("api/idm/login")
        .then(transformResponse)
        .then(updateCache)
        .then(emit);
    }
    return $q.resolve(cache);

  };
  return {
    login: login,
    logout: logout,
    queryStatus: queryStatus
  }
};
