module.exports = function($http, $translate, $scope, param, show) {

  $scope.formDisabled = null;

  $http({
    method: 'GET',
    url: '/api/user/userdata/existing/event'
  }).success(function(result) {
    if (result.status == 'OK') {
      //user has events and fields should be enabled
      $scope.formDisabled = false
    } else {
      //user has no events and fields should be disabled
      $scope.formDisabled = true
    }
  });


  //Array have to be initialised, to push elements
  //So you can set the first value and then an array
  $scope.salutations = [];

  $translate('GENERIC_NOT_SELECT').then(function(text) {
    $scope.salutations.push({
      'id': 0,
      'name': text
    });
  });

  $http.get('api/kontaktdaten/getallsalutations').then(function(salutationList) {
    angular.forEach(salutationList.data, function(value) {
      $scope.salutations.push({
        'id': value.pk,
        'name': value.salutation
      });
    });
  });

  $scope.genderOptions = [{
    id: 0,
    name: "GENERIC_NOT_SELECT"
  }, {
    id: 1,
    name: "GENERIC_GENDER_MALE"
  }, {
    id: 2,
    name: "GENERIC_GENDER_FEMALE"
  }];

  $http.get('api/user/userdata').then(function(person) {
    $scope.gender = $scope.genderOptions[0];
    $scope.salutation = $scope.salutations[0];

    $scope.person = person.data;

    if ($scope.person.sex_a_e1 == 'm') {
      $scope.gender = $scope.genderOptions[1];
    } else if ($scope.person.sex_a_e1 == 'f') {
      $scope.gender = $scope.genderOptions[2];
    }

    //Selects the option-value of person's salutation in the template
    angular.forEach($scope.salutations, function(value) {
      if (value.name == $scope.person.salutation_a_e1) {
        $scope.salutation = $scope.salutations[value.id];
      }
    });

    if ($scope.person.birthday_a_e1) {
      //Cast epoch to Date
      $scope.person.birthday_a_e1 = new Date($scope.person.birthday_a_e1);
    }
  });

  //Sets the max selectable date to "today"
  //Moments.js doesn't work here
  $scope.maxBirthdayDate = new Date();
  $scope.maxBirthdayDate = new Date($scope.maxBirthdayDate.getFullYear(), $scope.maxBirthdayDate.getMonth(),
    $scope.maxBirthdayDate.getDate());

  $scope.update_user_core_data = function() {

    if ($scope.updateUserCoreData.$valid) {
      $http({
        method: 'POST',
        url: 'api/user/userdata/update',
        headers: {
          "Content-Type": undefined
        },
        data: param({
          person_fk_salutation: $scope.salutation.id,
          person_salutation: $scope.salutation.name,
          person_title: $scope.person.title_a_e1,
          person_firstName: $scope.person.firstname_a_e1,
          person_lastName: $scope.person.lastname_a_e1,
          person_birthday: $scope.person.birthday_a_e1,
          person_nationality: $scope.person.nationality_a_e1,
          person_languages: $scope.person.languages_a_e1,
          person_gender: $scope.gender.id
        })
      }).success(function(result) {
        switch (result.status) {
          case 'OK':
            show.success('USER_ACCOUNT_CORE_DATA_UPDATED');
            break;
          default:
            show.error('GENERIC_ERROR');
        }

      }).error(function(data, status, headers, config) {
        show.error('GENERIC_ERROR');
      });
    }
  };
};
