(function(){
    angular.module('about',[]);
})();

(function() {
    'use strict';

    angular.module('about')
        .controller('aboutController',['$scope', function ($scope) {
            $scope.message = 'Look! I am an about page.';
        }]);
})();