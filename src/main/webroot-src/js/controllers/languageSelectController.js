module.exports = function($scope, $translate) {
  $scope.langKey = 'de_DE';
  $scope.changeLang = function (key) {
    $translate.use(key).then(function (key) {
    }, function (key) {
    });
  };
};
