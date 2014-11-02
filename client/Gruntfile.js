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
        tasks: ['uglify', 'copy'],
        options: {
          spawn: false,
        }
      }
    },

    // Uglify
    uglify: {
      dist: {
        options: {
          beautify: true,
          sourceMap: true
        },
        files:{
          // 'dist/js/app.min.js' : 'src/app/**/*.js',
          'dist/js/stuff.min.js' : ['src/js/*.js', '!**/*.min.js']
        }
      }
    },

    // Copy
    copy: {
      dist: {
        expand: true,
        cwd: 'src/',
        src: [
          'app/**',
          '*.html',
          'css/*',
          'fonts/*',
          'js/*.min.*'
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

  // Register Other Tasks
  grunt.registerTask('default', ['watch']);
  grunt.registerTask('dist', [
    'clean',
    'less',
    'uglify',
    'copy'
  ]);

};
