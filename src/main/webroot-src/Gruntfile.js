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

// watches files for changes and runs tasks based on the changed files
    watch: {
        bower: {
            files: ['bower.json'],
            tasks: ['wiredep']
        },
        js: {
            files: ['<%= appConfig.app %>/js/{,*/}*.js'],
            tasks: ['newer:jshint:all'],
            options: {
                livereload: '<%= connect.options.livereload %>'
            }
        },
        compass: {
            files: ['<%= appConfig.app %>/css/{,*/}*.{scss,sass}'],
            tasks: ['compass:server', 'autoprefixer']
        },
        gruntfile: {
            files: ['Gruntfile.js']
        },
        template: {
            files: ['<%= appConfig.app %>/{,*}*.ejs'],
            tasks: ['template']
        },
        livereload: {
            options: {
                livereload: '<%= connect.options.livereload %>'
            },
            files: [
                '<%= appConfig.app %>/{,*/}*.html',
                '.tmp/css/{,*/}*.css',
                '.tmp/{,*/}*.html',
                '<%= appConfig.app %>/images/{,*/}*.{png,jpg,jpeg,gif,webp,svg}'
            ]
        }
    },

        // The actual grunt server settings
        connect: {
            options: {
                port: 9100,
                hostname: '0.0.0.0',
                livereload: 35729
            },
            livereload: {
                options: {
                    open: false, // Open default browser after grunt start
                    middleware: function (connect) {
                        return [
                            connect.static('../resources/webroot'),
                            connect().use(
                                    '/bower_components',
                                    connect.static('./bower_components')
                            ),
                            connect().use(
                                    '/fonts',
                                    connect.static('../resources/webroot/fonts')
                            ),
                            connect().use(
                                    '/js',
                                    connect.static('./webroot-src/js')
                            ),
                            connect.static(appConfig.app),
                            function(req, res) {
                                if (req.url.match(/\/api\/entry\/.*$/)) {
                                    // serve /api fixtures from dev_fixtures/api/
                                    res.setHeader("Content-Type", "application/json");
                                    res.end(grunt.file.read("dev_fixtures/entry/" + req.url.replace("/api/entry/","") + "_" + req.method + ".json"));
                                } else if (req.url.match(/\/api\/user$/) || req.url.match(/\/user\/.*$/)) {
                                    // serve /api fixtures from dev_fixtures/api/
                                    res.setHeader("Content-Type", "application/json");
                                    res.end(grunt.file.read("dev_fixtures/user/" + req.url.replace("/api/user/","") + "_" + req.method + ".json"));
                                } else if (req.url.match(/\/api\/payment$/) || req.url.match(/\/payment\/.*$/)) {
                                    // serve /api fixtures from dev_fixtures/api/
                                    res.setHeader("Content-Type", "application/json");
                                    res.end(grunt.file.read("dev_fixtures/payment/" + req.url.replace("/api/payment/","") + "_" + req.method + ".json"));
                                } else if (! req.url.match(/\/$|\./)) {
                                    // always return index, if not a file or directory is requested
                                    res.end(grunt.file.read('../resources/webroot/index.html'));
                                } else {
                                    res.statusCode = 404;
                                    // use index also on 404:
                                    // Our client side routing takes care of displaying an apropriate error
                                    // message
                                    res.end(grunt.file.read('../resources/webroot/index.html'));
                                }
                            }
                        ];
                    }
                }
            }
        },

        useminPrepare: {
            html: ['partials/*.html'],
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
                        'bower_components/bootstrapvalidator/dist/css/bootstrapValidator.min.css',
                        'bower_components/angular-material/angular-material.min.css'
                    ],
                    '<%= appConfig.dist %>/css/site.css': [
                         'css/style.css'
                    ]
                }
            }
        },

        uglify: {
            options: {
                mangle: false
            },
            dist: {
                files: {
                    '<%= appConfig.dist %>/js/vendor.js': [
                        'bower_components/jquery/dist/jquery.min.js',
                        'bower_components/angular/angular.min.js',
                        'bower_components/angular-route/angular-route.min.js',
                        'bower_components/momentjs/min/moment-with-locales.min.js',
                        'bower_components/angular-material/angular-material.min.js',
                        'bower_components/angular-animate/angular-animate.min.js',
                        'bower_components/angular-aria/angular-aria.min.js',
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

        // Automatically inject Bower components into the app
        wiredep: {
            app: {
                src: ['<%= appConfig.app %>/{index,404,header,footer}.{html,ejs}'],
                ignorePath:    /\.\.\//,
                exclude: [/bootstrap-sass-official\//]
            },
            sass: {
                src: ['<%= appConfig.app %>/css/{,*/}*.{scss,sass}'],
                ignorePath: /(\.\.\/){1,2}bower_components\//,
                exclude: [/bootstrap-sass-official\//]
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
                        'partials/{,*/}*.*'
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

    grunt.loadNpmTasks('grunt-newer');
    grunt.registerTask('build', [
        'update',
        'wiredep',
        'ngAnnotate',
        'copy:dist',
        'useminPrepare',
        'concat',
        'newer:cssmin',
        'newer:uglify',
        'usemin'
    ]);

    grunt.registerTask('update', [
        'shell:npmInstall',
        'shell:bowerInstall'
    ]);

    grunt.registerTask('default', [
        'build'
    ]);

    grunt.registerTask('serve', 'Compile then start a connect web server', function (target) {
        if (target === 'dist') {
            return grunt.task.run(['build', 'connect:dist:keepalive']);
        }

        grunt.task.run([
            'connect:livereload',
            'watch'
        ]);
    });
};
