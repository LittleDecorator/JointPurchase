(function(){
    angular.module('product',[]);
})();

(function() {
    'use strict';

    angular.module('product')
        .controller('productController', ['$scope', '$state','dataResources',function ($scope, $state, dataResources) {
        console.log("Enter Product controller");

        $scope.data = [];

            dataResources.categoryTree.get(function(content){
            $scope.data.push(content);
        });

        //maps
        $scope.companyNames = dataResources.companyMap.get();
        $scope.categoryTypes = dataResources.categoryMap.get();

        $scope.items = [];

        //pagination
        $scope.currPage = 1;
        $scope.maxPage = 5;
        $scope.itemsPerPage = 20;
        $scope.totalItems = null;

        //get all items
            dataResources.previewItems.get(function (data) {
            angular.forEach(data, function (item) {
                $scope.items.push(item);
            });
            //get total items cou, for pagination
            $scope.totalItems = $scope.items.length;

            //listener for some scope variables
            $scope.$watch('currPage + itemsPerPage', function () {
                $scope.subList();
            });

                //show side menu
                $('.slide-outt').addClass('slide-inn');
        });

        $scope.toggle = function(scope) {
            scope.toggle();
        };

        var getRootNodesScope = function() {
            return angular.element(document.getElementById("tree-root")).scope();
        };

        $scope.collapseAll = function() {
            var scope = getRootNodesScope();
            scope.collapseAll();
        };

        $scope.expandAll = function() {
            var scope = getRootNodesScope();
            scope.expandAll();
        };

        $scope.options = {};

        //get sub list of items for single page display
        $scope.subList = function () {
            var begin = (($scope.currPage - 1) * $scope.itemsPerPage),
                end = begin + $scope.itemsPerPage;

            $scope.filteredItems = $scope.items.slice(begin, end);
        };

        $scope.itemView = function(id){
            $state.transitionTo("product.detail", {itemId: id});
        };

        $scope.filterByCategory = function(categoryId){
            console.log(categoryId);
            dataResources.previewItems.filter({categoryId:categoryId},function(data){
                console.log(data);
                $scope.items = [];
                angular.forEach(data, function (item) {
                    $scope.items.push(item);
                });
                //get total items cou, for pagination
                $scope.totalItems = $scope.items.length;

                //listener for some scope variables
                $scope.$watch('currPage + itemsPerPage', function () {
                    $scope.subList();
                });
            })
        };

    }]);
})();
