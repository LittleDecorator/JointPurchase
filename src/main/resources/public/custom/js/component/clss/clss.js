(function(){
    angular.module('clss',[]);
})();

(function() {
    'use strict';

    angular.module('clss')
        .controller('categoryClssController',['$scope','dataResources','resolved','menu',function($scope,dataResources,resolved, menu){
            var vm = this;

            vm.init = init;
            vm.treeHandler = treeHandler;
            vm.select = select;

            vm.categories = [];
            vm.tree = {};

            /**
             * Инициализация
             */
            function init(){
                dataResources.categoryTree.get().$promise.then(function(data){
                    angular.forEach(data, function(node){
                        check(node);
                        if(node.nodes.length > 0){
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
                        vm.categories.push(node);
                    });

                    function check(node){
                        node.checked = resolved.indexOf(node.id) != -1;
                    }
                    
                });
                
            }

            /**
             * Выбор узла дерева
             * @param branch
             */
            function treeHandler(branch) {
                if(branch.nodes.length>0){
                    angular.forEach(branch.nodes, function(child){
                        child.checked = branch.checked
                    })
                }
                var parent = vm.tree.get_parent_branch(branch);
                var siblings = vm.tree.get_siblings(branch).map(function(child){
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
            }

            /**
             * Подтверждение выбора категорий
             */
            function select(){
                var data = [];
                angular.forEach(vm.categories, function(node){
                    if(node.nodes.length > 0){
                        angular.forEach(node.nodes, function(child){
                            add(child);
                        });
                    } else {
                        add(node);
                    }
                    vm.categories.push(node);
                });
                $scope.closeThisDialog(data);

                function add(node){
                    if(node.checked){
                        data.push({id:node.id,name:node.title})
                    }
                }
            }

            init();

        }])

        .controller('itemClssController',['$scope','dataResources','resolved', function($scope, dataResources, resolved){
            
            var vm = this;
            vm.init = init;
            vm.select = select;
            vm.findSelected = findSelected;
            vm.toogleSelection = toogleSelection;
            vm.apply = apply;
            vm.clear = clear;
            
            vm.items = [];
            // тут будем хранить выбранные
            vm.selected = [];
            vm.filter = {name:null, article:null};

            /**
             * получаем мапу товаров
             */
            function init(clear){
                dataResources.itemMap.get(vm.filter).$promise.then(function(result){
                    if(clear){
                        vm.items = [];
                    }
                    angular.forEach(result, function (item) {
                        item.selected=false;
                        vm.items.push(item);
                    });
                    if(resolved){
                        findSelected(vm.items);
                    }
                });
            }

           /**
            * Подтверждение выбора
            */
           function select(){
              console.log(vm.selected)
              var data = [], result;
              angular.forEach(vm.items,function(elem){
                 if(elem.selected){
                    data.push(elem);
                    return true;
                 } else {
                    return false;
                 }
              });
               result = vm.selected.concat(data).unique('id');
               console.log(result);
                $scope.closeThisDialog(result);
           }

           /**
            * Слушатель события выборв/снятия выбора товара
            */
           function toogleSelection(){

           }

            // поиск отмеченных
            function findSelected(array){
                var selected = resolved.map(function(item){
                    return item['id'];
                });
                if(selected){
                    angular.forEach(array, function(item){
                        if(selected.indexOf(item.id) !== -1){
                            item.selected = true;
                            // откладываем копию в список отмеченых
                           if(!helpers.arrayContainById(vm.selected, item.id)){
                              vm.selected.push(angular.copy(item))
                           }
                        }
                    });
                }
            }
            
            function apply() {
                init(true);
            }
            
            function clear(){
                vm.filter = {name:null, article:null};
                init(true);
            }
            
            init();
            
        }])

})();
