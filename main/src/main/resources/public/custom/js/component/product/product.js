(function(){
    angular.module('product',[]);
})();

(function() {
    'use strict';

    angular.module('product')
        .controller('productController', ['$scope', '$state','dataResources','$timeout','eventService',function ($scope, $state, dataResources,$timeout,eventService) {
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
                //$('.slide-outt').addClass('slide-inn');
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

            $scope.$on('onFilter',function() {
                var node = eventService.data;
                console.log(node);
                if(node==null || helpers.isEmptyObject(node)){
                    dataResources.previewItems.get(function (data) {
                        $scope.items = [];
                        angular.forEach(data, function (item) {
                            $scope.items.push(item);
                        });
                        $scope.filteredItems = $scope.items;
                    });
                } else {
                    if (!node.company) {
                        //collect all inner types
                        var types = [];

                        if (node.types.length > 0) {
                            angular.forEach(node.types, function (type) {
                                types.push(type.id);
                            })
                        }

                        if (node.nodes.length > 0) {
                            angular.forEach(node.nodes, function (node) {
                                if (node.types.length > 0) {
                                    angular.forEach(node.types, function (type) {
                                        types.push(type.id);
                                    })
                                }
                            })
                        }

                        dataResources.filterByType.filter(types, function (data) {
                            $scope.filteredItems = [];
                            angular.forEach(data, function (item) {
                                $scope.filteredItems.push(item);
                            });
                        })
                    } else {
                        if(node.nodes.length == 0){
                            dataResources.filterByCompany.filter({companyId:node.id}, function (data) {
                                $scope.filteredItems = [];
                                angular.forEach(data, function (item) {
                                    $scope.filteredItems.push(item);
                                });
                            })
                        }
                    }
                }
            });

           $timeout(function() {
                console.log("DONE!");
                $(".collapsible").collapsible();
            }, 300);

            $scope.toggleCategory = function(){
                //show side menu
                var side = $('.slide-outt');
                if(side.hasClass('slide-inn')){
                    side.removeClass('slide-inn')
                } else {
                    side.addClass('slide-inn');
                }

            }

    }])

        .controller('detailController',['$scope','$state','product',function ($scope, $state, product) {
            console.log("enter detail controller");

            $scope.mainImage = null;
            $scope.item = angular.extend({},product);

            $scope.THUMB_URL = "media/image/thumb/";
            $scope.PREVIEW_URL = "media/image/preview/";
            $scope.VIEW_URL = "media/image/view/";
            $scope.ORIG_URL = "media/image/";


            console.log($scope);

            if($scope.item.media.length>0){
                $scope.mainImage = $scope.item.media[0];
            }

            $scope.show = function(id){
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
            };

            $scope.showGallery = function () {
                $state.go("product.detail.gallery", {id: $scope.item.id});
            };

        }]);
})();
