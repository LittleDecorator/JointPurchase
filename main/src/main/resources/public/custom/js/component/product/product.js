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
                console.log("itemView");
                $state.go("product.detail", {itemId: id});
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

            $(".collapsible").collapsible();

    }])

        .controller('detailController',['$scope','$state','product',function ($scope, $state, product) {
            console.log("enter detail controller");

            $scope.mainImage = null;
            $scope.item = angular.extend({},product);

            $scope.THUMB_URL = "media/image/thumb/";
            $scope.PREVIEW_URL = "media/image/preview/";
            $scope.VIEW_URL = "media/image/view/";
            $scope.ORIG_URL = "media/image/";

            if($scope.item.media.length>1){
                $scope.mainImage = $scope.item.media[0];
                //$scope.item.media.splice(0,1);
            }

            $scope.show = function(id){
                //$scope.item.media.push($scope.mainImage);

                var keepGoing = true;
                var res = null;

                $scope.item.media.forEach(function(elem,index){
                    if (keepGoing) {
                        if(elem === id) {
                            res = index;
                            keepGoing = false;
                        }
                    }
                });
                $scope.mainImage = id;
                //$scope.item.media.splice(res,1);
            };

            /*$scope.view= function(){
                console.log("in view");
                console.log($scope);

                //угобая центролизация modal dialog'а
                function centerModal() {
                    $(this).css('display', 'block');
                    var $dialog = $(this).find(".modal-dialog");
                    var offset = ($(window).height()-150 - $dialog.height()) / 2;
                    // Center modal vertically in window
                    $dialog.css("margin-top", offset);
                }

                $('.modal').on('show.bs.modal', centerModal);
                $(window).on("resize", function () {
                    $('.modal:visible').each(centerModal);
                });
            };*/



        }]);
})();
