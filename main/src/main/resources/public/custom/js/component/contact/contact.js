(function(){
    angular.module('contact',[]);
})();

(function() {
    'use strict';

    angular.module('contact')
        .controller('contactController',['$scope', function ($scope) {
            $scope.message = 'Contact us! JK. This is just a demo.';
        }]);
})();