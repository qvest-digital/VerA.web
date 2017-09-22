module.exports = function($scope, $translate) {
    $translate('GENERIC_PAGE_NOT_FOUND').then(function (text) {
        $scope.error = text;
    });
};
