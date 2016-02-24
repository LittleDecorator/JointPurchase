(function(){
    angular.module('product',[]);
})();

(function() {
    'use strict';

    angular.module('product')
        .controller('productController', ['$scope', '$state','dataResources','$timeout','eventService',function ($scope, $state, dataResources,$timeout,eventService) {
            console.log("Enter Product controller");

            //$scope.data = [];
            $scope.items = [];
            $scope.searchResult = [];
            $scope.searchCriteria = [];
            $scope.prop = {stopLoad:false, searchInputOpen:false};

            var filter = {portion:0, limit:30,categoryId:null};

            //dataResources.categoryTree.get(function(content){
            //    $scope.data.push(content);
            //});

            //maps
            $scope.companyNames = dataResources.companyMap.get();
            $scope.categoryTypes = dataResources.categoryMap.get();



            //get all items
            $scope.loadData = function(){
                filter.offset = filter.portion*filter.limit;
                dataResources.previewItems.filter(filter,function (data) {
                    console.log(data);
                    if(data.length < filter.limit){
                        $scope.prop.stopLoad = true;
                    }
                    angular.forEach(data, function (item) {
                        item.item.url = item.imageUrl;
                        console.log(item);
                        $scope.items.push(item.item);
                    });
                    filter.portion++;
                    console.log($scope.items);
                });
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

            /* transition to item view */
            $scope.itemView = function(id){
                console.log("itemView");
                $state.go("product.detail", {itemId: id});
                console.log("transition failed");
            };

            /* search items */
            $scope.search = function(){
                console.log("press search");
                if($scope.searchCriteria && $scope.searchCriteria.length > 0){
                    // делаем поисковый запрос
                    $scope.searchResult = [];
                        dataResources.searchItem.search({criteria:$scope.searchCriteria},function(data){
                            console.log(data);
                            angular.forEach(data, function (item) {
                                $scope.searchResult.push(item);
                            });
                        })
                } else {
                    $scope.toggleSearch();
                }
            };

            $scope.toggleSearch = function(){
                $scope.prop.searchInputOpen = !$scope.prop.searchInputOpen;
                if(!$scope.prop.searchInputOpen){
                    $scope.searchCriteria = null;
                    $scope.searchResult = [];
                }
            };

            /* start search by enter press */
            $scope.keyPress = function(keyCode) {
                console.log("keyPress");
                if (keyCode == 13){
                    $scope.search();
                }
            };

            /* filter items by category */
            $scope.filterByCategory = function(categoryId){
                console.log(categoryId);
                filter.categoryId = categoryId;
                filter.portion = 0;
                $scope.items = [];
                $scope.prop.stopLoad = false;
                $scope.loadData();
            };

            /* something for filter */
            /* with will work when side click */
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

            $scope.$on('onSearchHide', function(){
                console.log("onSearchHide");
                $scope.prop.searchInputOpen = false;
                $scope.searchCriteria = null;
                $scope.searchResult = [];
            });

            /* load page timeout */
            $timeout(function() {
                console.log("DONE!");
                //$(".collapsible").collapsible();

                //new UISearch( document.getElementById( 'sb-search' ) );

            }, 100);

            /* show\hide side panel */
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
