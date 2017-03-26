(function(){
    angular.module('clss',[]);
})();

(function() {
    'use strict';

    angular.module('clss')
        .controller('categoryClssController',['$scope','dataResources','resolved',function($scope,dataResources,resolved){

            $scope.categories = [];
            $scope.tree = {};

            dataResources.categoryTree.get().$promise.then(function(data){
                angular.forEach(data, function(node){
                    check(node);
                    if(node.nodes.length>0){
                        angular.forEach(node.nodes, function(child){
                            check(child);
                        });
                        var siblings = node.nodes.map(function(child){
                            return child['checked']
                        });
                        if(siblings.indexOf(false)==-1){
                            node.checked = true;
                        }
                    }
                    $scope.categories.push(node);
                });

                function check(node){
                    if(resolved.indexOf(node.id)!=-1){
                        node.checked = true;
                    } else {
                        node.checked = false;
                    }
                }
            });

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

            $scope.select = function(){
                var data = [];
                angular.forEach($scope.categories, function(node){
                    if(node.nodes.length>0){
                        angular.forEach(node.nodes, function(child){
                            add(child);
                        });
                    } else {
                        add(node);
                    }
                    $scope.categories.push(node);
                });
                $scope.closeThisDialog(data);

                function add(node){
                    if(node.checked){
                        data.push({id:node.id,name:node.title})
                    }
                }
            };



        }])

        .controller('itemClssController',['$scope','dataResources','resolved',function($scope,dataResources,resolved){

            $scope.items = [];
            
            // получаем мапу товаров
            dataResources.itemMap.get().$promise.then(function(result){
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
