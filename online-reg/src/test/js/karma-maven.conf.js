module.exports = function (config) {
  config.set({
    basePath: '../../',

    files: [
        'main/resources/webroot/jquery-1.10.2.min.js',
        'main/resources/webroot/angular.min.js',
        'main/resources/webroot/angular-route.min.js',
        'main/resources/webroot/ui-bootstrap-tpls.min.js',
        'main/resources/webroot/moment.min.js',
        'main/resources/webroot/onlinereg.js',
        'test/js/lib/angular-mocks.js',
        'test/js/unit/*.js',
    ],

    frameworks: ['jasmine'],

    autoWatch: true,

    browsers: ['Chrome'],

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
