module.exports = function(grunt) {
    grunt.initConfig({

        distFolder: 'build',

        pkg: grunt.file.readJSON('package.json'),

        useminPrepare: {
            html: '../src/main/resources/public/index.html',
            options: {
                dest: '<%= distFolder %>'
            }
        },

        usemin: {
            html: ['<%= distFolder %>/index.html']
        },

        copy:{
            task1: {
                src:'../src/main/resources/public/index.html', dest:'<%= distFolder %>/index.html'
            },
            task2: {
                files:[{
                    expand: true, cwd:'<%= distFolder %>/',src: ['**'] , dest:'classes/public/'
                }]
            }
        },

        clean:{
            removeOld: {
                src:['classes/public/custom/js/*', '!classes/public/custom/js/libs/**', 'classes/public/custom/css/*']
            }
        },

        uglify: {
            options: {
                // beautify: true,
                //mangle: true
                mangle: false
            }
        }
    });

    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-cssmin');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-usemin');

    // Register our own custom task alias.
    grunt.registerTask('default', [
        'copy:task1',
        'useminPrepare',
        'concat',
        'uglify',
        'cssmin',
        'usemin',
        'clean:removeOld',
        'copy:task2'
    ]);
};