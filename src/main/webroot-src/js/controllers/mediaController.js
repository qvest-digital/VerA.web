onlineRegApp.controller('MediaController', function ($scope, $http, $rootScope, $location, $routeParams, $translate) {
    $scope.success = null;
    $scope.error = null;

    $rootScope.cleanMessages();

    $scope.genderOptions = [
        {id: 0, name:"GENERIC_PLEASE_SELECT"},
        {id: 1, name:"GENERIC_SALUTATION_MALE"},
        {id: 2, name:"GENERIC_SALUTATION_FEMALE"}
    ];

    $scope.gender = $scope.genderOptions[0];

    if ($rootScope.user_logged_in == null) {
        $http.get('api/media/' + $routeParams.uuid).then(function(presentPersons) {
            $scope.presentPersons = presentPersons.data;
        });

        $scope.register_pressevertreter = function () {

            if ($scope.gender.id == 0) {
                $translate('GENERIC_MISSING_GENDER_MESSAGE').then(function (text) {
                    $scope.error = text;
                });
            } else if ($scope.gender.id == 1 || $scope.gender.id == 2){
                if ($scope.lastname != null && $scope.firstname != null && $scope.email != null && $scope.address != null && $scope.plz != null && $scope.city != null && $scope.country != null) {
                    $translate('GENERIC_ERROR').then(function (text) {
                        var ERROR_TEXT = text;
                    });
                    $scope.button = true;
                    $http({
                        method: 'POST',
                        url: 'api/media/' + $routeParams.uuid + '/register',
                        headers: {"Content-Type" : undefined},
                        data: $.param({
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
                    }).success(function (result) {
                        if (result.status === 'NO_EVENT_DATA') {
                            $translate('GENERIC_MESSAGE_EVENT_DOESNT_EXISTS').then(function (text) {
                                $scope.error = text;
                            });
                            $scope.success = null;
                        }  else if (result.status === 'WRONG_EVENT') {
                            $translate('GENERIC_MESSAGE_EVENT_DOESNT_EXISTS').then(function (text) {
                                $scope.error = text;
                            });
                            $scope.success = null;
                        } else if (result.status === 'OK') {
                            $translate('MEDIA_REPRESEINTATIVES_REGISTER_SUCCESSFUL_MESSAGE').then(function (text) {
                                $scope.success = text;
                            });
                            $http.get('api/delegation/' + $routeParams.uuid).then(function(presentPersons) {
                                $scope.presentPersons = presentPersons.data;
                            });

                            $scope.lastname = null;
                            $scope.firstname = null;
                            $scope.gender = $scope.genderOptions[0];
                            $scope.email = null;
                            $scope.address = null;
                            $scope.plz = null;
                            $scope.city = null;
                            $scope.country = null;
                        } else if (result.status === 'PRESS_USER_EXISTS_ALREADY') {
                            $translate('MEDIA_REPRESEINTATIVES_ACTIVATION_ALREADY_EXIST').then(function (text) {
                                $scope.error = text;
                            });
                            $scope.success = null;
                        } else {
                            $scope.error = ERROR_TEXT;
                        }
                        $scope.button = false;
                    }).error(function (data, status, headers, config) {
                        $scope.error = ERROR_TEXT;
                        $scope.button = false;
                    });
                } else {
                    $translate('GENERIC_MESSAGE_FILL_IN_ALL_FIELDS').then(function (text) {
                        $scope.error = text;
                    });
                }
            }
        }
    }
    else {
        $location.path('/veranstaltungen');
    }
});