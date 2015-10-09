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
    dist: '../resources/webroot'
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

    useminPrepare: {
        html: ['partials/delegation.html', 'partials/event.html', 'partials/fileupload.html', 'partials/header.html', 'partials/header_login.html', 'partials/header_logout.html', 'partials/kontaktdaten.html', 'partials/login.html', 'partials/media.html', 'partials/meine-veranstaltungen.html', 'partials/navigation.html', 'partials/page_not_found.html', 'partials/register.html', 'partials/register_user.html', 'partials/reset_password.html', 'partials/update.html', 'partials/welcome.html'],
        options: {
          dest: '<%= appConfig.dist %>',
          flow: {
            html: {
              steps: {
                js: ['concat', 'uglifyjs'],
                css: ['cssmin']
              },
              post: {}
            }
          }
        }
      },

    usemin: {
      html: ['<%= appConfig.dist %>/{,*/}*.html'],
      css: ['<%= appConfig.dist %>/css/{,*/}*.css'],
      options: {
        assetsDimcrs: ['<%= appConfig.dist %>','<%= appConfig.dist %>/images']
      }
    },

    cssmin: {
      dist: {
        files: {
          '<%= appConfig.dist %>/css/vendor.css': [
            'bower_components/bootstrap/dist/css/bootstrap.min.css',
            'bower_components/bootstrapvalidator/dist/css/bootstrapValidator.min.css'
          ],
          '<%= appConfig.dist %>/css/site.css': [
             'css/style.css'
          ]
        }
      }
    },

    uglify: {
       dist: {
         files: {
              '<%= appConfig.dist %>/js/vendor.js': [
                 'bower_components/jquery/dist/jquery.min.js',
                 'bower_components/angular/angular.min.js',
                 'bower_components/angular-route/angular-route.min.js',
                 'bower_components/momentjs/min/moment-with-locales.min.js',
                 'bower_components/bootstrap/dist/js/bootstrap.min.js',
                 'bower_components/bootstrapvalidator/dist/js/bootstrapValidator.min.js',
                 'bower_components/ng-file-upload/ng-file-upload-all.min.js',
                 'bower_components/angular-bootstrap/ui-bootstrap-tpls.min.js',
                 'bower_components/angular-translate/angular-translate.min.js',
                 'bower_components/ng-flow/dist/ng-flow.min.js',
                 'bower_components/flow.js/dist/flow.min.js',
                 'bower_components/angular-translate-loader-static-files/angular-translate-loader-static-files.min.js'
            ],
            '<%= appConfig.dist %>/js/site.js': [
              'js/onlinereg.js',
              'js/controllers/*.js'
            ]
         }
       }
     },

    // ng-annotate tries to make the code safe for minification automatically
    // by using the Angular long form for dependency injection.
     ngAnnotate: {
       dist: {
         files: [{
           expand: true,
           cwd: '/js',
           src: ['onlinereg.js','/controllers/*.js'],
           dest: '<%= appConfig.dist %>/js/'
         }]
       }
     },

     concat: {
       dist: {}
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
            'bower_components/**'
          ]
        }, {
          expand: true,
          cwd: './bower_components/bootstrap/fonts/',
          src: '*',
          dest: '<%= appConfig.dist %>/fonts/'
        }]
      }
    }

  });

  grunt.registerTask('build', [
    'clean:dist',
    'update',
    'ngAnnotate',
    'copy:dist',
    'useminPrepare',
    'concat',
    'cssmin',
    'uglify',
    'usemin'
  ]);

  grunt.registerTask('update', [
    'shell:npmInstall',
    'shell:bowerInstall'
  ]);

  grunt.registerTask('default', [
    'build'
  ]);

};
