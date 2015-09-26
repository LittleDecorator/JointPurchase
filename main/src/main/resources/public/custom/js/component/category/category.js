(function(){
    angular.module('category',[]);
})();

(function() {
    'use strict';

    angular.module('category')
        .controller('categoryController',['$scope','dataResources',function($scope,dataResources){

            console.log("categoryController");

            $scope.categories = [];
            $scope.tree = {};

            $scope.selected = null;
            $scope.selectedCategoryType = null;
            $scope.selectedType = null;

            $scope.selectedCopy = null;
            $scope.newCategory = "";
            var newCategoryList=[];



            dataResources.categoryTree.get(function(result){
                console.log(result);
                //$scope.categories.push(result);
                //$scope.categories.push("bla");
                $scope.tree.expand_all();
            });

            $scope.types = dataResources.categoryTypes.get();

            $scope.storeType = function(type){
                $scope.selectedType=type;
                console.log($scope.selectedType);
            };

            $scope.storeCategoryType = function(type){
                $scope.selectedCategoryType=type;
                console.log($scope.selectedCategoryType);
            };

            $scope.addType = function(){
                $scope.selected.types.push(angular.copy($scope.selectedType));
                console.log($scope.selected.types);
            };

            $scope.removeType = function(){
                var idx = $scope.selected.types.indexOf($scope.selectedCategoryType);
                $scope.selected.types.splice(idx,1);
                console.log($scope.selected.types);
            };

            $scope.treeHandler = function(branch) {
                $scope.selected = branch;
                $scope.selectedCopy = angular.copy(branch);
                //check if we have any type in category
                /*if($scope.selected.types.length==0){
                    $scope.selected.types.push({name:'&#160'})
                }*/
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