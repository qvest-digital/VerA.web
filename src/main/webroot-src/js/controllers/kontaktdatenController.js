onlineRegApp.controller('KontaktdatenController', function($http, $rootScope, $translate, $location, $scope) {
    if ($rootScope.user_logged_in == null) {
        $scope.setNextPage('kontaktdaten');
        $location.path('/login');
    } else {
        $scope.success = null;
        $scope.error = null;
        $scope.userHasNoEvents = null;
        $rootScope.cleanMessages();
        $scope.setNextPage('kontaktdaten');

        $http({
            method: 'GET',
            url: '/api/user/userdata/existing/event/' + $rootScope.user_logged_in
        }).success(function (result) {
            if (result.status == 'OK') {
                //fields should be enabled
                $scope.userHasNoEvents = false
            } else {
                //fields should be disabled
                $scope.userHasNoEvents = true
            }
        });

        $http.get('api/user/userdata/' + encodeURIComponent($rootScope.user_logged_in)).then(function(person) {
            $scope.person = person.data;

            if($scope.person.birthday_a_e1 && $scope.person.birthday_a_e1 > 0) {
                //TODO TO DATE!!!!
                $scope.person.birthday_a_e1;
            }
        });

        if (!$scope.userHasNoEvents) {
            //$scope.birthday.$mdDateLocaleProvider.parseDate("de");
            // moment.locale("de");

            $scope.salutationOptions = [
                {id: 0, name: "GENERIC_NOT_SELECT"},
                {id: 1, name: "GENERIC_SALUTATION_MALE"},
                {id: 2, name: "GENERIC_SALUTATION_FEMALE"}
            ];

            $scope.genderOptions = [
                {id: 0, name: "GENERIC_NOT_SELECT"},
                {id: 1, name: "GENERIC_GENDER_MALE"},
                {id: 2, name: "GENERIC_GENDER_FEMALE"}
            ];

            $scope.salutation = $scope.salutationOptions[0];
            $scope.gender = $scope.genderOptions[0];

            $scope.birthday = new Date();

            $scope.maxBirthdayDate = new Date($scope.birthday.getFullYear(),
                $scope.birthday.getMonth(), $scope.birthday.getDate());

            $scope.updateUserCoreData = function () {
                $http({
                    method: 'POST',
                    url: 'api/user/userdata/update/' + encodeURIComponent($rootScope.user_logged_in) + '',
                    headers: {"Content-Type": undefined},
                    data: $.param({
                        person_salutation: $scope.salutation,
                        person_title: $scope.title,
                        person_firstName: $scope.firstName,
                        person_lastName: $scope.lastName,
                        person_birthday: $scope.birthday,
                        person_nationality: $scope.nationality,
                        person_languages: $scope.languages,
                        person_gender: $scope.gender
                    })
                    //This stuff left (delegationController for example)
                }).success(function (result) {
                    switch (result.status) {
                        case 'OK':
                            $scope.status = 1;

                            setStatus = 1;

                            $scope.update = function (parm1, parm2) {
                                $scope.status = parm1 + ": " + parm2;
                            };

                            break;
                        default:
                            $scope.status = 'e';
                    }

                    if ($scope.status == 1) {
                        $location.path('/login');
                    }

                }).error(function (data, status, headers, config) {
                    $scope.status = 'e';
                });
            };
        }
    }
});
