module.exports = function (config) {
  config.set({
    basePath: '../../',

    files: [
        'main/resources/webroot/scripts/jquery-1.10.2.min.js',
        'main/resources/webroot/scripts/angular.min.js',
        'main/resources/webroot/scripts/angular-route.min.js',
        'main/resources/webroot/scripts/ui-bootstrap-tpls.min.js',
        'main/resources/webroot/scripts/moment.min.js',
        'main/resources/webroot/scripts/onlinereg.js',
        'test/js/lib/angular-mocks.js',
        'test/js/unit/*.js',
    ],

    frameworks: ['jasmine'],

    autoWatch: true,

    browsers: ['Firefox'],

    junitReporter: {
      outputFile: '../target/karma_unit.xml',
      suite: 'unit'
    },

    preprocessors: {
      // ..
      'js/*.js': 'coverage'         // (1)
    },

    coverageReporter: {
      type : 'html',                // (2)
      dir : '../target/karma-coverage'
    }
  });
};
