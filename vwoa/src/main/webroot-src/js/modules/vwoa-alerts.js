var alerts = require('angular').module('vwoaAlerts', [
  require('angular-translate'),
  require('angular-translate-loader-static-files')
]);

alerts
  .config(function($translateProvider) {
    $translateProvider.useStaticFilesLoader({
      prefix: 'languages/lang-',
      suffix: '.json'
    });

    $translateProvider.preferredLanguage('de_DE');
  })
  .component('alerts', {
    templateUrl: 'partials/vwoa-alerts.html',
    controller: function($rootScope, $timeout, $scope) {
      var nextId, remove, defaultClasses;

      defaultClasses = function(payload) {
        switch (payload.type) {
          case "error":
            return ["alert", "alert-danger"];
          case "success":
            return ["alert", "alert-success"];
          default:
            return ["alert"];
        }
      };

      remove = function(msg) {
        return function() {
          var i = $scope.messages.indexOf(msg);
          if (i !== -1) {
            $scope.messages.splice(i, 1);
          }
        };
      };
      $scope.messages = [];
      nextId = 0;
      $rootScope.$on('vwoa-alerts:message', function(ev, payload) {
        var msg = {
          sn: nextId++,
          type: payload.type || 'info',
          body: typeof payload === "string" ? payload : payload.body,
          cssClasses: payload.cssClasses || defaultClasses(payload)
        };
        $scope.messages.push(msg);
        $timeout(remove(msg), payload.timeout || 5000);
      });
    }
  })
  .factory("show", function($rootScope, $translate) {
    var showMessage = function(type, code, placeholders) {

      $translate(code && code.message || code, placeholders).then(function(text) {
        $rootScope.$emit('vwoa-alerts:message', {
          body: text,
          type: type
        });
      });
    };
    return {
      success: function(code, placeholders) {
        showMessage("success", code, placeholders);
      },
      error: function(code, placeholders) {
        showMessage("error", code, placeholders);
      },
      message: showMessage
    };
  });

module.exports = alerts.name;
