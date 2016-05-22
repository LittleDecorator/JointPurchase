(function(){
    angular.module('clss',[]);
})();

(function() {
    'use strict';

    angular.module('clss')
        .controller('categoryClssController',['$scope','dataResources','eventService','$modalInstance',function($scope,dataResources,eventService,$modalInstance){

            console.log("categoryClssController");

            $scope.categories = [];

            dataResources.categoryMap.get(function(result){
                var good;
                angular.forEach(result, function (comp) {
                    if(wasSelected(comp.id)){
                        good = true;
                    } else {
                        good = false;
                    }
                    var obj = {id:comp.id,name:comp.name,selected:good};
                    $scope.categories.push(obj);
                });
            });

            $scope.select = function(){
                var data = [];
                angular.forEach($scope.categories,function(elem){
                    console.log(elem);
                    if(elem.selected){
                        data.push(elem);
                        return true;
                    } else {
                        return false;
                    }
                });
                console.log(data);
                eventService.onCategoryClssSelected(data);
            };

            function wasSelected(id){
                var res;
                $modalInstance.params.some(function(elem){
                    if(elem.value == id){
                        return res=true;
                    } else {
                        return res=false;
                    }
                });
                return res;
            }

        }])

        .controller('itemClssController',['$scope','dataResources','resolved',function($scope,dataResources,resolved){

            $scope.items = [];

            dataResources.item.all().$promise.then(function(result){
                angular.forEach(result, function (item) {
                    item.selected=false;
                    $scope.items.push(item);
                });
                if(resolved){
                    console.log("resolved present")
                    console.log(resolved);
                    findSelected($scope.items);
                }
            });

            $scope.select = function(){
                var data = [];
                angular.forEach($scope.items,function(elem){
                    if(elem.selected){
                        data.push(elem);
                        return true;
                    } else {
                        return false;
                    }
                });
                $scope.closeThisDialog(data);
            };
            
            function findSelected(array){
                var selected = resolved.map(function(item){
                    return resolved['id'];
                });
                
                angular.forEach(array, function(item){
                    if(selected.includes(item.id)){
                        item.selected = true;
                    }
                });
            }
        }])

})();
