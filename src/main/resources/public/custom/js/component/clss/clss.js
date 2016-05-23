(function(){
    angular.module('clss',[]);
})();

(function() {
    'use strict';

    angular.module('clss')
        .controller('categoryClssController',['$scope','dataResources','resolved',function($scope,dataResources,resolved){

            $scope.categories = [];
            console.log(resolved);

            dataResources.categoryTree.get().$promise.then(function(data){
                $scope.categories = data;
            });

            // dataResources.categoryMap.get().$promise.then(function(result){
            //     angular.forEach(result, function (category) {
            //         var obj = {id:category.id,name:category.name,selected:false};
            //         $scope.categories.push(obj);
            //     });
            //     if(resolved){
            //         findSelected($scope.categories);
            //     }
            // });

            $scope.select = function(){
                var data = [];
                angular.forEach($scope.categories,function(elem){
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
                var selected = resolved.map(function(category){
                    return category['id'];
                });
                if(selected){
                    angular.forEach(array, function(category){
                        if(selected.indexOf(category.id)!=-1){
                            category.selected = true;
                        }
                    });
                }
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
                    return item['id'];
                });
                if(selected){
                    angular.forEach(array, function(item){
                        if(selected.indexOf(item.id)!=-1){
                            item.selected = true;
                        }
                    });
                }
            }
        }])

})();
