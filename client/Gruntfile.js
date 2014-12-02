module.exports = function(grunt) {
  grunt.initConfig({

    // Clean
    clean: {
      dist: {
        src: ["dist/"]
      },
      options: {
        'no-write': false
      }
    },

    // LESS
    less: {
      dist: {
        options: {
          compress: true,
          optimization: 2
        },
        files: {
          'dist/css/main.css': 'src/less/main.less'
        }
      }
    },

    // Watch
    watch: {
      less: {
        files: ['src/less/*'],
        tasks: ['less']
      },
      dev: {
        files: [
          'src/**/*.html',
          'src/**/*.js'
          ],
        tasks: ['concat', /*'uglify',*/ 'copy']
      }
    },

    // Concat
    concat: {
      dist: {
        options: {
          sourceMap: false
        },
        src: ['src/js/angular.js', 'src/js/jquery.js', 'src/js/*.js', 'src/app/app.js', 'src/app/app.*.js', 'src/app/*/*.js'],
        dest: 'dist/js/n3twork.min.js'
      }
    },

    // Uglify
    uglify: {
      dist: {
        options: {
          beautify: false,
          sourceMap: false,
          report: 'gzip',
          mangle: false
        },
        files:{
          'dist/js/n3twork.min.js' : 'dist/js/n3twork.min.js', //'src/js/*.js'],
          //'dist/js/stuff.min.js' : ['src/js/*.js', '!**/*.min.js', '!**/app.js']
        }
      }
    },

    // Copy
    copy: {
      dist: {
        expand: true,
        cwd: 'src/',
        src: [
          'app/**/*.html',
          //'app/**',
          '*.html',
          'css/*',
          'fonts/*',
          'js/n3twork.js',
          //'js/*.min.js.map',
          'img/**'
        ],
        dest: 'dist/'
      },
    }
  });

  // Load Modules
  grunt.loadNpmTasks('grunt-contrib-clean');
  grunt.loadNpmTasks('grunt-contrib-less');
  grunt.loadNpmTasks('grunt-contrib-watch');
  grunt.loadNpmTasks('grunt-contrib-uglify');
  grunt.loadNpmTasks('grunt-contrib-copy');
  grunt.loadNpmTasks('grunt-contrib-concat');

  // Register Other Tasks
  grunt.registerTask('default', ['dev', 'watch']);
    grunt.registerTask('dev', [
    'clean',
    'less',
    'concat',
    'copy'
  ]);
  grunt.registerTask('dist', [
    'clean',
    'less',
    'concat',
    'uglify',
    'copy'
  ]);

};
