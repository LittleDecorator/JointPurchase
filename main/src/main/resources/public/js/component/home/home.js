(function(){
        angular.module('home',[]);
})();

(function(){
    'use strict';

    angular.module('home')
        .controller('homeController',['$scope', function ($scope) {
            angular.element(document).ready(function () {
                $('.slider').slider({full_width: true});
            });
        }]);
})();
