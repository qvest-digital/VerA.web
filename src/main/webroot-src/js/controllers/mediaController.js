module.exports = function($scope, $http, $routeParams, $translate, param,show) {

  $scope.genderOptions = [{
    id: 1,
    name: "GENERIC_SALUTATION_MALE"
  }, {
    id: 2,
    name: "GENERIC_SALUTATION_FEMALE"
  }];


  $scope.register_pressevertreter = function() {
    $http({
      method: 'POST',
      url: 'api/media/' + $routeParams.uuid + '/register',
      headers: {
        "Content-Type": undefined
      },
      data: param({
        nachname: $scope.lastname,
        vorname: $scope.firstname,
        gender: $scope.gender.label,
        email: $scope.email.trim(),
        address: $scope.address,
        plz: $scope.plz,
        city: $scope.city,
        country: $scope.country,
        current_language: $translate.use()
      })
    }).success(function(result) {
      if (result.status === 'NO_EVENT_DATA') {
        show.error('GENERIC_MESSAGE_EVENT_DOESNT_EXISTS');
      } else if (result.status === 'WRONG_EMAIL') {
        show.error('GENERIC_MESSAGE_EMAIL_NOT_VALID');
      } else if (result.status === 'WRONG_EVENT') {
        show.error('GENERIC_MESSAGE_EVENT_DOESNT_EXISTS');
      } else if (result.status === 'PRESS_USER_EXISTS_ALREADY') {
        show.error('MEDIA_REPRESEINTATIVES_ACTIVATION_ALREADY_EXIST');
      } else if (result.status === 'OK') {
        show.success('MEDIA_REPRESEINTATIVES_REGISTER_SUCCESSFUL_MESSAGE');


        $scope.lastname = null;
        $scope.firstname = null;
        $scope.gender = $scope.genderOptions[0];
        $scope.email = null;
        $scope.address = null;
        $scope.plz = null;
        $scope.city = null;
        $scope.country = null;
      } else {
        show.error('GENERIC_ERROR');
      }
    }).error(function(data, status, headers, config) {
      show.error('GENERIC_ERROR');
    });

  };
};
