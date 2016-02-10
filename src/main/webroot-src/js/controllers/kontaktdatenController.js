onlineRegApp.controller('KontaktdatenController', function($http, $rootScope, $translate, $location, $scope) {
if ($rootScope.user_logged_in == null) {
        $scope.setNextPage('kontaktdaten');
        $location.path('/login');
    }

    $scope.success = null;
    $scope.error = null;
    $rootScope.cleanMessages();
    $scope.setNextPage('kontaktdaten');

    $scope.existsUser = $http.get('/api/user/userdata/existing/' + $rootScope.user_logged_in);

    jQuery(function() {
        jQuery( "#birthday").datepicker({
            maxDate: "+0D",
            changeMonth: true,
            changeYear: true
        });
    });
});