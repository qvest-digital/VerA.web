onlineRegApp.controller('KontaktdatenController', function($http, $rootScope, $translate, $location, $scope, $mdDateLocale) {
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
                //user has events and fields should be enabled
                $scope.userHasNoEvents = false
            } else {
                //user has no events and fields should be disabled
                $scope.userHasNoEvents = true
            }
        });

        if (!$scope.userHasNoEvents) {
            //Datepicker localization
            if($translate.use() == "de_DE") {
                var localeDate = moment.localeData('de');
            } else {
                var localeDate = moment.localeData('en');
            }

            $mdDateLocale.shortMonths = localeDate._monthsShort;
            $mdDateLocale.shortDays = localeDate._weekdaysMin;

            //Array have to be initialised, to push elements
            //So you can set the first value and then an array
            $scope.salutations = [];

            $translate('GENERIC_NOT_SELECT').then(function (text) {
                $scope.salutations.push({'id' : 0, 'name' : text});
            });

            $http.get('api/kontaktdaten/getallsalutations').then(function(salutationList) {
                angular.forEach(salutationList.data, function(value) {
                    $scope.salutations.push({'id' : value.pk, 'name' : value.salutation});
                });
            });

            $scope.genderOptions = [
                {id: 0, name: "GENERIC_NOT_SELECT"},
                {id: 1, name: "GENERIC_GENDER_MALE"},
                {id: 2, name: "GENERIC_GENDER_FEMALE"}
            ];

            $http.get('api/user/userdata/' + encodeURIComponent($rootScope.user_logged_in)).then(function(person) {
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
                    if(value.name == $scope.person.salutation_a_e1) {
                        $scope.salutation = $scope.salutations[value.id];
                    }
                });

                if($scope.person.birthday_a_e1) {
                    //Cast epoch to Date
                    $scope.person.birthday_a_e1 = new Date($scope.person.birthday_a_e1);
                }
            });

            //Sets the max selectable date to "today"
            //Moments.js doesn't work here
            $scope.maxBirthdayDate = new Date();
            $scope.maxBirthdayDate = new Date($scope.maxBirthdayDate.getFullYear(), $scope.maxBirthdayDate.getMonth(),
                $scope.maxBirthdayDate.getDate());

            $scope.update_user_core_data = function () {
                $scope.success = null;
                $scope.error = null;
                $rootScope.cleanMessages();

                if ($scope.person.firstname_a_e1 == null || $scope.person.firstname_a_e1.length == 0) {
                    $translate('GENERIC_MESSAGE_MISSING_FIRSTNAME').then(function (text) {
                        $scope.error = text;
                    });
                } else if ($scope.person.lastname_a_e1 == null || $scope.person.lastname_a_e1.length == 0) {
                    $translate('GENERIC_MESSAGE_MISSING_LASTNAME').then(function (text) {
                        $scope.error = text;
                    });
                } else if ($scope.person.firstname_a_e1.length > 35) {
                    $translate('GENERIC_MESSAGE_FIRSTNAME_MAX').then(function (text) {
                        $scope.error = text;
                    });
                } else if ($scope.person.lastname_a_e1.length > 35) {
                    $translate('GENERIC_MESSAGE_LASTNAME_MAX').then(function (text) {
                        $scope.error = text;
                    });
                } else {
                    $http({
                        method: 'POST',
                        url: 'api/user/userdata/update/' + encodeURIComponent($rootScope.user_logged_in) + '',
                        headers: {"Content-Type": undefined},
                        data: $.param({
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
                            $translate('USER_ACCOUNT_CORE_DATA_UPDATED').then(function (text) {
                                $scope.success = text;
                            });
                        }
                    }).error(function (data, status, headers, config) {
                    });
                }
            }
        }
    }
});
