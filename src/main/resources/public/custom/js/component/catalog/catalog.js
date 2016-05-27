(function(){
    angular.module('catalog',[]);
})();

(function() {
    'use strict';

    angular.module('catalog')
        .controller('catalogController', ['$scope', '$state','dataResources','$timeout','eventService',function ($scope, $state, dataResources,$timeout,eventService) {

            console.log("catalogController");

            $scope.items = [];
            $scope.searchFilter = {category:null, company:null, criteria:null, price:null, portion:0, limit:30};


            var busy = false;
            var portion = 0;
            $scope.stopLoad=false;
            $scope.allDataLoaded = false;
            $scope.infiniteDistance = 2;
            // $scope.searchInputOpen=false;

            // $scope.companyNames = dataResources.companyMap.get();
            // $scope.categoryTypes = dataResources.categoryMap.get();


            //get items
            $scope.loadData = function(isClean){
                if(!$scope.stopLoad && !busy){
                    busy = true;
                    dataResources.previewItems.filter($scope.searchFilter,function (data) {

                        if(data.length < $scope.searchFilter.limit){
                            $scope.stopLoad = true;
                        }
                        if(isClean){
                            $scope.items = [];
                        }

                        angular.forEach(data, function (item) {
                            item.item.url = item.imageUrl;
                            $scope.items.push(item.item);
                        });

                        $scope.searchFilter.portion++;
                        $scope.searchFilter.offset = $scope.searchFilter.portion * $scope.searchFilter.limit;
                        $scope.allDataLoaded = true;
                        busy = false;
                    });
                }
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
                $state.go("catalog.detail", {itemId: id});
            };

            /* search items */
            // $scope.search = function(){
            //     if($scope.searchCriteria && $scope.searchCriteria.length > 0){
            //         // делаем поисковый запрос
            //         $scope.searchResult = [];
            //             dataResources.searchItem.search({criteria:$scope.searchCriteria},function(data){
            //                 console.log(data);
            //                 angular.forEach(data, function (item) {
            //                     $scope.searchResult.push(item);
            //                 });
            //             })
            //     } else {
            //         $scope.toggleSearch();
            //     }
            // };

            // $scope.toggleSearch = function(){
            //     $scope.prop.searchInputOpen = !$scope.prop.searchInputOpen;
            //     if(!$scope.prop.searchInputOpen){
            //         $scope.searchCriteria = null;
            //         $scope.searchResult = [];
            //     }
            // };

            /* filter items by category */
            // $scope.filterByCategory = function(categoryId){
            //     $scope.searchFilter.category = categoryId;
            //     portion = 0;
            //     $scope.stopLoad = false;
            //     $scope.loadData();
            // };

            /* something for filter */
            /* with will work when side click */
            $scope.$on('onFilter',function() {
                var node = eventService.data;
                console.log(node);
                // if(node==null || helpers.isEmptyObject(node)){
                //     dataResources.previewItems.get(function (data) {
                //         $scope.items = [];
                //         angular.forEach(data, function (item) {
                //             $scope.items.push(item);
                //         });
                //         $scope.filteredItems = $scope.items;
                //     });
                // } else {
                //     if (!node.company) {
                //         //collect all inner types
                //         var types = [];
                //
                //         if (node.types.length > 0) {
                //             angular.forEach(node.types, function (type) {
                //                 types.push(type.id);
                //             })
                //         }
                //
                //         if (node.nodes.length > 0) {
                //             angular.forEach(node.nodes, function (node) {
                //                 if (node.types.length > 0) {
                //                     angular.forEach(node.types, function (type) {
                //                         types.push(type.id);
                //                     })
                //                 }
                //             })
                //         }
                //
                //         dataResources.filterByType.filter(types, function (data) {
                //             $scope.filteredItems = [];
                //             angular.forEach(data, function (item) {
                //                 $scope.filteredItems.push(item);
                //             });
                //         })
                //     } else {
                //         if(node.nodes.length == 0){
                //             dataResources.filterByCompany.filter({companyId:node.id}, function (data) {
                //                 $scope.filteredItems = [];
                //                 angular.forEach(data, function (item) {
                //                     $scope.filteredItems.push(item);
                //                 });
                //             })
                //         }
                //     }
                // }
            });

            // $scope.$on('onSearchHide', function(){
            //     console.log("onSearchHide");
            //     $scope.prop.searchInputOpen = false;
            //     $scope.searchCriteria = null;
            //     $scope.searchResult = [];
            // });

            /* load page timeout */
            $timeout(function() {
                $(".collapsible").collapsible();
            }, 10);

            /* show\hide side panel */
            // $scope.toggleFilter = function(){
            //     //show side menu
            //     var side = $('.slide-outt');
            //     if(side.hasClass('slide-inn')){
            //         side.removeClass('slide-inn')
            //     } else {
            //         side.addClass('slide-inn');
            //     }
            // };

            $scope.toggleMobileFilter = function(){
                console.log("toggleMobileFilter")
                //show side menu
                var side = $('.slide-outt');
                if(side.hasClass('slide-inn')){
                    side.removeClass('slide-inn')
                }
                $('#mobile-filter').openModal();
            };

            /* init modal if mobile viewport */
            $scope.applyCategory = function(category){
                console.log(category);
                $('#mobile-filter').closeModal();
                $scope.searchFilter.category = category.id;
                portion = 0;
                $scope.stopLoad = false;
                $scope.loadData(true);
            }

        }])

        .controller('catalogCardController',['$scope','$state','product',function ($scope, $state, product) {

            $scope.mainImage = null;
            $scope.item = angular.extend({},product);

            $scope.THUMB_URL = "media/image/thumb/";
            $scope.PREVIEW_URL = "media/image/preview/";
            $scope.VIEW_URL = "media/image/view/";
            $scope.ORIG_URL = "media/image/";

            if($scope.item.media.length>0){
                $scope.mainImage = $scope.item.media[0];
            }

            $scope.show = function(id){
                console.log($scope.mainImage);
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
                console.log($scope.mainImage);
            };

            $scope.showGallery = function () {
                $state.go("product.detail.gallery", {id: $scope.item.id});
            };

        }]);
})();
