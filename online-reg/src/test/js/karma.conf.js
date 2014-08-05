module.exports = function (config) {
  config.set({
    basePath: '../../',

    files: [
        'main/resources/webroot/lib/jquery.min.js',
        'main/resources/webroot/lib/ui/jquery-ui.js',
        'main/resources/webroot/lib/angular.min.js',
        'main/resources/webroot/lib/angular-resource.min.js',
        'main/resources/webroot/lib/ui-bootstrap-tpls.min.js',
	'main/resources/webroot/*.js',
        'test/javascript/lib/angular-mocks.js',
        'test/javascript/unit/*.js',	
    ],

    frameworks: ['jasmine'],

    autoWatch: true,

    browsers: ['Chrome'],

    junitReporter: {
      outputFile: '../target/karma_unit.xml',
      suite: 'unit'
    }
  });
};
