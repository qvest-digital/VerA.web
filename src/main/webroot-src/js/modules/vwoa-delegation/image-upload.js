module.exports = function() {
  var applied = function(scope, fn) {
    return function() {
      var args = arguments;
      scope.$apply(function() {
        fn.apply(this, args);
      });
    };
  };
  return {
    restrict: 'E',
    scope: {
      image: "=image"
    },
    link: function(scope, elem, attrs) {
      var input = elem.find('input');
      var button = elem.find('span');
      button.on("click", function(ev) {
        input[0].click(ev);
      });
      input.bind("change", applied(scope, function(event) {
        scope.inputChanged(event);
      }));
    },
    controller: function($scope) {
      var fileReader = new FileReader();
      $scope.state = 'empty'
      $scope.$watch('image',function(){
        $scope.state = $scope.image ? "loaded" : "empty";
      });
      $scope.inputChanged = function(event) {
        $scope.state = 'loading';
        fileReader.readAsDataURL(event.target.files[0]);
      };
      fileReader.onload = function(event) {
        var image = new Image();

        image.onload = applied($scope, function(event) {
          var canvas = document.createElement('canvas');
          var HEIGHT = 245;
          var WIDTH = 186;
          var scale = Math.min(WIDTH / image.width, HEIGHT / image.height);
          image.width *= scale;
          image.height *= scale;

          var ctx = canvas.getContext("2d");
          ctx.clearRect(0, 0, canvas.width, canvas.height);
          canvas.height = image.height;
          canvas.width = image.width;

          ctx.drawImage(image, 0, 0, image.width, image.height);
          $scope.image = canvas.toDataURL();
        });
        image.src = fileReader.result;
      };

    },
    templateUrl: 'partials/fileupload.html'
  };
};
