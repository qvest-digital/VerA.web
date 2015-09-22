'use strict';

module.exports = function (grunt) {

  // Disables annoying terminal icon bounces on OSX.
  grunt.option('color', false);

  // Load grunt tasks automatically
  require('load-grunt-tasks')(grunt);

  // Configurable paths for the application
  // dist muss später "../resources..." sein
  var appConfig = {
    app: '.',
    dist: 'dist'
  };

  // Define the configuration for all the tasks
  grunt.initConfig({

    // Project settings
    appConfig: appConfig,

    shell: {
      options: {
        stdout: true
      },
      npmInstall: {
        command: 'npm install'
      },
      bowerInstall: {
        command: 'bower install'
      }
    },

    // Empties folders to start fresh
    clean: {
      dist: {
        files: [{
          dot: true,
          src: [
            '.tmp',
            '<%= appConfig.dist %>/{,*/}*',
            '!<%= appConfig.dist %>/.git{,*/}*'
          ]
        }]
      }
    },

    // Copies remaining files to places other tasks can use
    copy: {
      dist: {
        files: [
        {
          expand: true,
          dot: true,
          cwd: '<%= appConfig.app %>',
          dest: '<%= appConfig.dist %>',
          src: [
            '*.{ico,png,txt}',
            '.htaccess',
            'index.html',
            'images/**/*',
            'languages/{,*/}*.*',
            'partials/{,*/}*.*',
            'js/{,*/}*.*',
            'css/{,*/}*.*',
            'bower_components/{,*/}*.*'
          ]
        }]
      }
    }
  });

  grunt.registerTask('build', [
    'clean:dist',
    'update',
    'copy:dist'
  ]);

  grunt.registerTask('update', [
    'shell:npmInstall',
    'shell:bowerInstall'
  ]);

  grunt.registerTask('default', [
    'build'
  ]);

};
