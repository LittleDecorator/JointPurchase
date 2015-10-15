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
                    if(elem.selected){
                        data.push(elem);
                        return true;
                    } else {
                        return false;
                    }
                });
                eventService.onClssSelected(data);
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

        }]);

})();
