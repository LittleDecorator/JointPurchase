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

        .controller('itemClssController',['$scope','dataResources','eventService','$modalInstance',function($scope,dataResources,eventService,$modalInstance){
            console.log("itemClssController");

            $scope.items = [];

            dataResources.item.query(function(result){
                var good;
                angular.forEach(result, function (item) {
                    if(wasSelected(item.id)){
                        good = true;
                    } else {
                        good = false;
                    }
                    var obj = {id:item.id,name:item.name,selected:good,article:item.article,price:item.price};
                    $scope.items.push(obj);
                    console.log($scope.items);
                });
            });

            $scope.select = function(){
                var data = [];
                angular.forEach($scope.items,function(elem){
                    console.log(elem);
                    if(elem.selected){
                        data.push(elem);
                        return true;
                    } else {
                        return false;
                    }
                });
                console.log(data);
                eventService.onItemClssSelected(data);
            };

            function wasSelected(id){
                var res;
                $modalInstance.params.some(function(elem){
                    console.log(elem);
                    if(elem.id == id){
                        return res=true;
                    } else {
                        return res=false;
                    }
                });
                return res;
            }
        }])

})();
