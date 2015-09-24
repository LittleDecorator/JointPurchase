(function(){
    angular.module('guide',[]);
})();

(function() {
    'use strict';

    angular.module('guide')
        .controller('guideController',function() {
            $('ul.tabs').tabs();
        })

        .controller('typeController',['$scope','dataResources',function($scope,dataResources){

            $scope.current = {type :null};
            $scope.types = [];

            dataResources.categoryTypes.get(function(result) {
                angular.forEach(result, function (node) {
                    $scope.types.push(node);
                });
            });

            //select type element
            $scope.select = function(idx){
                $scope.current.type = angular.copy($scope.types[idx]);
                $('a').removeClass('active');
                $('#'+$scope.types[idx].id).addClass('active');

            };

            /* on client only */
            $scope.edit = function(){
                var orig = helpers.findInArrayById($scope.types,$scope.current.type.id);
                orig.name = $scope.current.type.name;
                dataResources.categoryTypes.update(orig);
            };

            /* work only on client */
            $scope.delete = function(){
                var orig = helpers.findInArrayById($scope.types,$scope.current.type.id);
                var idx = $scope.types.indexOf(orig);
                $scope.types.splice(idx,1);
                dataResources.categoryTypes.delete({id: orig.id});
            };

            /* use db for generate */
            $scope.add = function() {
                var copy = angular.copy($scope.current.type);
                dataResources.categoryTypes.update(copy, function(data){
                    $scope.current.type = angular.copy(data);
                    $scope.types.push(data);
                });
            };
        }])

        .controller('categoryController',['$scope','dataResources',function($scope,dataResources){

            $scope.categories = [];
            $scope.tree = {};

            $scope.selected = null;
            $scope.types = [];
            $scope.filteredTypes = [];
            $scope.selectedCopy = null;
            $scope.newCategory = "";

            var newCategoryList=[],editCategoryList=[],deleteCategoryList=[];

            dataResources.categoryTree.get(function(result){
                angular.forEach(result, function (node) {
                    $scope.categories.push(node);
                });
                $scope.tree.expand_all();
            });

            dataResources.categoryTypes.get(function(data){
                angular.forEach(data,function(rec){
                    console.log(rec);
                    $scope.types.push(rec);
                });
            });

            //filter total array
            $scope.treeHandler = function(branch) {
                console.log(branch);
                $scope.selected = branch;
                $scope.selectedCopy = angular.copy(branch);
                $scope.filteredTypes = helpers.filterArray($scope.types,$scope.selected.types);

            };

            $scope.edit = function(){
                console.log($scope.selected.title);

                $scope.selected.title = $scope.selectedCopy.title;

                //check branch in new list
                var b = helpers.findInArrayById(newCategoryList,$scope.selectedCopy.id);

                if(!helpers.isEmptyObject(b)){
                    console.log(b.title);
                    b.title = $scope.selected.title;
                    console.log(b.title);
                } else {
                    //check in edit list
                    var e = helpers.findInArrayById(editCategoryList,$scope.selectedCopy.id);
                    if(!helpers.isEmptyObject(e)){
                        e.title = $scope.selected.title;
                    } else {
                        editCategoryList.push($scope.selected);
                    }
                }
            };

            $scope.delete = function(){
                var selected = $scope.tree.get_selected_branch();
                var parent = $scope.tree.get_parent_branch(selected);
                var idx = parent.nodes.indexOf(selected);
                parent.nodes.splice(idx,1);
                //TODO add new and edit list check, add to del list

                //check branch in new list
                var b = helpers.findInArrayById(newCategoryList,$scope.selectedCopy.id);

                if(!helpers.isEmptyObject(b)){
                    console.log(b.title);
                    b.title = $scope.selected.title;
                    console.log(b.title);
                } else {
                    //check in edit list
                    var e = helpers.findInArrayById(editCategoryList,$scope.selectedCopy.id);
                    if(!helpers.isEmptyObject(e)){
                        e.title = $scope.selected.title;
                    } else {
                        editCategoryList.push($scope.selected);
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
                var newNode = {id:id,title:$scope.newCategory,nodes:null,parentId:parentId};
                //insert new node in tree
                if(parentId==null){
                    $scope.tree.add_branch(null,newNode);
                } else {
                    $scope.tree.add_branch(parent,newNode);
                }
                //stash new node for DB update
                newCategoryList.push({id:id,name:$scope.newCategory,parentId:parentId});
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

            $scope.addType = function(idx){
                $scope.selected.types.push(angular.copy($scope.filteredTypes[idx]));
                $scope.filteredTypes.splice(idx,1);
            };

            $scope.removeType = function(idx){
                $scope.filteredTypes.push(angular.copy($scope.selected.types[idx]));
                $scope.selected.types.splice(idx,1);
            };

            $scope.save = function(){
                console.log($scope.categories);
                //TODO send all lists
            }
        }]);
})();


