(function(){
    angular.module('category',[]);
})();

(function() {
    'use strict';

    angular.module('category')
        .controller('categoryController',['$scope','dataResources',function($scope,dataResources){

            $scope.categories = [];
            $scope.tree = {};

            $scope.selected = null;
            $scope.selectedCopy = null;
            $scope.newCategory = "";
            var newCategoryList=[];

            dataResources.categoryTree.get(function(result){
                $scope.categories.push(result);
                $scope.tree.expand_all();
            });

            $scope.treeHandler = function(branch) {
                $scope.selected = branch;
                $scope.selectedCopy = angular.copy(branch);
            };

            $scope.edit = function(){
                $scope.selected.title = $scope.selectedCopy.title;
            };

            $scope.delete = function(){
                var selected = $scope.tree.get_selected_branch();
                var parent = $scope.tree.get_parent_branch(selected);
                var idx = parent.nodes.indexOf(selected);
                parent.nodes.splice(idx,1);
            };

            /**
             *
             * @param selected
             * if null then find selected and use it as parent.
             * if NOT null use as parent
             * if empty object then create as ROOT node
             */
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
                var newNode = {id:id,title:$scope.newCategory,nodes:null,parentId:parentId};
                //insert new node in tree
                if(parentId==null){
                    $scope.tree.add_branch(null,newNode);
                } else {
                    $scope.tree.add_branch(parent,newNode);
                }
                //stash new node for DB update
                newCategoryList.push({id:id,name:$scope.newCategory,parentId:parentId});
                console.log(newCategoryList);
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


        }]);
})();