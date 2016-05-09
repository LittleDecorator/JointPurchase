(function(){
    angular.module('category',[]);
})();

(function() {
    'use strict';

    angular.module('category')
        .controller('categoryController',['$scope','dataResources','itemClssModal','categoryNodes','eventService','$timeout', function($scope,dataResources,itemClssModal,categoryNodes,eventService,$timeout){

            var newCategoryList=[],editCategoryList=[],deleteCategoryList=[];
            $scope.categories = categoryNodes;
            $scope.tree = {};

            $scope.selected = null;
            $scope.selectedCopy = null;
            $scope.newCategory = "";

            // $scope.tree.expand_all();

            //get selected category items
            function getCategoryItems(categoryId){
                var items = [];
                dataResources.categoryItems.get({categoryId:categoryId},function(data){
                    angular.forEach(data,function(rec){
                        console.log(rec);
                        items.push(rec);
                    });
                });
                return items;
            }

            //get selected node
            $scope.treeHandler = function(branch) {
                $scope.selected = branch;
                $scope.selectedCopy = angular.copy(branch);
                if(!$scope.selected.items.length>0){
                    $scope.selected.items = $scope.selectedCopy.items = getCategoryItems($scope.selectedCopy.id);
                }

                $("#add_sibling").removeClass('disabled');
                $("#add").removeClass('disabled');
                // $('.toc-wrapper').pushpin({ offset: 50});
            };

            /* toggle listener */
            $scope.toggleHandler = function(branch){
                if(branch.nodes.length == 0 && branch.noLeaf){
                    console.log("bla");
                    // dataResources.settings.tournamentLevel.get({},branch).$promise.then(function(data){
                    //     angular.forEach(data, function(node){
                    //         $scope.tree.add_branch(branch,node);
                    //     });
                    // })
                }
            };

            //edit node
            $scope.edit = function(){
                $scope.selected.title = $scope.selectedCopy.title;

                //check branch in new list
                var b = helpers.findInArrayById(newCategoryList,$scope.selectedCopy.id);
                //if found change stashed title
                if(!helpers.isEmptyObject(b)){
                    b.title = $scope.selected.title;
                } else {
                    //check in edit list
                    var e = helpers.findInArrayById(editCategoryList,$scope.selectedCopy.id);
                    //if found update title
                    if(!helpers.isEmptyObject(e)){
                        e.title = $scope.selected.title;
                    } else {
                        editCategoryList.push($scope.selected);
                    }
                }
            };

            $scope.delete = function(){
                //remove node from tree
                var selected = $scope.tree.get_selected_branch();
                var parent = $scope.tree.get_parent_branch(selected);
                var idx = parent.nodes.indexOf(selected);
                parent.nodes.splice(idx,1);


                //check if removed branch in new list
                var b = helpers.findInArrayById(newCategoryList,selected.id);
                //remove from new list deleted branch
                if(!helpers.isEmptyObject(b)){
                    var b_idx = newCategoryList.indexOf(b);
                    newCategoryList.splice(b_idx,1);
                } else {
                    //add to remove list
                    deleteCategoryList.push(selected);

                    //check if deleted branch in edit list
                    var e = helpers.findInArrayById(editCategoryList,selected.id);
                    //remove from edit list
                    if(!helpers.isEmptyObject(e)){
                        var e_idx = editCategoryList.indexOf(e);
                        editCategoryList.splice(e_idx,1);
                    }
                }
            };

            $scope.add = function(selected) {
                var parent,id = helpers.guid();
                if(!selected){
                    parent = $scope.tree.get_selected_branch();
                } else {
                    parent = selected;
                }
                //check that parent exists and not empty
                var parentId = (parent && !$.isEmptyObject(parent))?parent.id:null;
                //prepare added node
                var newNode = {id:id,title:$scope.newCategory,nodes:[],parentId:parentId,types:[]};
                //insert new node in tree
                if(parentId==null){
                    $scope.tree.add_branch(null,newNode);
                } else {
                    $scope.tree.add_branch(parent,newNode);
                }
                //stash new node for DB update
                newCategoryList.push(newNode);
            };

            //add new node on the same level
            $scope.addAsSibling = function() {
                var selected = $scope.tree.get_selected_branch();
                var parent = $scope.tree.get_parent_branch(selected);
                $scope.add(parent);
            };

            //add new node as root
            $scope.addAsRoot = function() {
                $scope.add({});
            };

            $scope.removeItem = function(idx){
                $scope.selectedCopy.items.splice(idx,1);
                $scope.selected.items = $scope.selectedCopy.items;
                //TODO check in edit and new lists
                //check branch in new list
                var b = helpers.findInArrayById(newCategoryList,$scope.selected.id);
                //if found change stashed title
                if(!helpers.isEmptyObject(b)){
                    b.items = [].concat($scope.selected.items);
                } else {
                    //check in edit list
                    var e = helpers.findInArrayById(editCategoryList,$scope.selected.id);
                    //if found update title
                    if(!helpers.isEmptyObject(e)){
                        e.items = [].concat($scope.selected.items);
                    } else {
                        editCategoryList.push($scope.selected);
                    }
                }
            };

            $scope.save = function(){
                console.log({new:newCategoryList,update:editCategoryList,delete:deleteCategoryList});
                dataResources.categoryTree.post({new:newCategoryList,update:editCategoryList,delete:deleteCategoryList});
                //clear stash
                newCategoryList = [];
                editCategoryList = [];
                deleteCategoryList = [];
            };

            $scope.showClss = function(){
                console.log($scope.selectedCopy.items);
                $scope.modal = itemClssModal($scope.selectedCopy.items);
            };

            $scope.$on('onItemClssSelected',function(){
                $scope.selectedCopy.items = [];
                angular.forEach(eventService.data,function(item){
                    $scope.selectedCopy.items.push(item);
                });
                $scope.selected.items = $scope.selectedCopy.items;

                //check branch in new list
                    var b = helpers.findInArrayById(newCategoryList,$scope.selected.id);
                    //if found change stashed title
                    if(!helpers.isEmptyObject(b)){
                        b.items = [].concat($scope.selected.items);
                    } else {
                        //check in edit list
                        var e = helpers.findInArrayById(editCategoryList,$scope.selected.id);
                        //if found update title
                        if(!helpers.isEmptyObject(e)){
                            e.items = [].concat($scope.selected.items);
                        } else {
                            editCategoryList.push($scope.selected);
                        }
                    }
            });

            $timeout(function(){
                $('.toc-wrapper').pushpin({ offset: 100});
            },100);

        }]);
})();

