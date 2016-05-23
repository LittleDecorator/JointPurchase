(function(){
    angular.module('clss',[]);
})();

(function() {
    'use strict';

    angular.module('clss')
        .controller('categoryClssController',['$scope','dataResources','resolved',function($scope,dataResources,resolved){

            $scope.categories = [];
            console.log(resolved);
            $scope.tree = {};

            dataResources.categoryTree.get().$promise.then(function(data){
                angular.forEach(data, function(node){
                    node.checked = false;
                    if(node.nodes.length>0){
                        angular.forEach(node.nodes, function(child){
                            child.checked = false;
                        });
                    }
                    $scope.categories.push(node);
                });
                //$scope.categories = data;
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

            $scope.treeHandler = function(branch) {
                branch.checked = !branch.checked;
                if(branch.nodes.length>0){
                    angular.forEach(branch.nodes, function(child){
                        child.checked = branch.checked
                    })
                }
                var parent = $scope.tree.get_parent_branch(branch);
                var siblings = $scope.tree.get_siblings(branch).map(function(child){
                    return child['checked']
                });

                if(parent){
                    if(parent.checked && !branch.checked){
                        parent.checked = !parent.checked
                    }

                    if(!parent.checked && branch.checked){
                        if(siblings.indexOf(false)==-1){
                            parent.checked = !parent.checked;
                        }
                    }
                }

                /* need for reselect */
                branch.selected = false;
            };

            $scope.toggleHandler = function(branch){
                console.log(branch);
            };

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
