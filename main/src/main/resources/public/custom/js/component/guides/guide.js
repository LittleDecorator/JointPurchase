(function(){
    angular.module('guide',[]);
})();

(function() {
    'use strict';

    angular.module('guide')
        .controller('guideController',function() {
            $('ul.tabs').tabs();
        })

        .controller('typeController',['$scope','dataResources',function($scope,dataResources){

            $scope.current = {type :null};
            $scope.types = [];

            dataResources.categoryTypes.get(function(result) {
                angular.forEach(result, function (node) {
                    $scope.types.push(node);
                });
            });

            //select type element
            $scope.select = function(idx){
                $scope.current.type = angular.copy($scope.types[idx]);
                $('a').removeClass('active');
                $('#'+$scope.types[idx].id).addClass('active');

            };

            /* on client only */
            $scope.edit = function(){
                var orig = helpers.findInArrayById($scope.types,$scope.current.type.id);
                orig.name = $scope.current.type.name;
                dataResources.categoryTypes.update(orig);
            };

            /* work only on client */
            $scope.delete = function(){
                var orig = helpers.findInArrayById($scope.types,$scope.current.type.id);
                var idx = $scope.types.indexOf(orig);
                $scope.types.splice(idx,1);
                dataResources.categoryTypes.delete({id: orig.id});
            };

            /* use db for generate */
            $scope.add = function() {
                var copy = angular.copy($scope.current.type);
                dataResources.categoryTypes.update(copy, function(data){
                    $scope.current.type = angular.copy(data);
                    $scope.types.push(data);
                });
            };
        }])


})();


