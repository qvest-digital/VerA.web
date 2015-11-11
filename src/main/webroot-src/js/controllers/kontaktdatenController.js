onlineRegApp.controller('KontaktdatenController', function($rootScope, $translate, $location, $scope) {
if ($rootScope.user_logged_in == null) {
        $scope.setNextPage('/kontaktdaten');
        $location.path('/login');
    }
});
