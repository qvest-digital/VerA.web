onlineRegApp.controller('KontaktdatenController', function($http, $rootScope, $translate, $location, $scope) {
if ($rootScope.user_logged_in == null) {
        $scope.setNextPage('kontaktdaten');
        $location.path('/login');
    }

    $scope.success = null;
    $scope.error = null;
    $scope.hasUserEvents = null;
    $rootScope.cleanMessages();
    $scope.setNextPage('kontaktdaten');

    $http({
        method: 'GET',
        url: '/api/user/userdata/existing/event/' + $rootScope.user_logged_in
    }).success(function (result) {
        if(result.status == 'OK') {
            //fields should be enabled
            $scope.userHasNoEvents = false
        } else {
            //fields should be disabled
            $scope.userHasNoEvents = true
        }
    });

    $scope.updateUserCoreData = function() {
        $http({
            method: 'POST',
            url: 'api/user/userdata/update/' + encodeURIComponent($rootScope.user_logged_in) + '',
            headers: {"Content-Type" : undefined},
            data: $.param({
                person_salutation: $scope.person.salutation,
                person_title: $scope.person.title,
                person_firstName: $scope.person.firstName,
                person_lastName: $scope.person.lastName,
                person_birthday: $scope.person.birthday,
                person_nationality: $scope.person.nationality,
                person_languages: $scope.person.languages,
                person_gender: $scope.person.gender,
                current_language: $translate.use()
            })
        }).success(function (result) {
            switch(result.status) {
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

            if($scope.status == 1) {
                $location.path('/login');
            }

        }).error(function (data, status, headers, config) {
            $scope.status = 'e';
        });
    };

    $(function() {
        jQuery( ".birthday").datepicker({
            maxDate: "+0D",
            changeMonth: true,
            changeYear: true
        });
    });
});