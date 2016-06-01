(function(){
    angular.module('catalog',[]);
})();

(function() {
    'use strict';

    angular.module('catalog')
        .controller('catalogController', ['$scope', '$state','dataResources','$timeout','eventService',function ($scope, $state, dataResources,$timeout,eventService) {

            console.log("catalogController");

            $scope.items = [];
            $scope.searchFilter = {category:null, company:null, criteria:null, price:null, offset:0, limit:30};
            $scope.sideFilters = [];

            var busy = false;
            var portion = 0;
            $scope.showResults = false;
            $scope.showSideFilter = false;
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
                    dataResources.previewItems.filter($scope.searchFilter).$promise.then(function (data) {
                        console.log(data.items.length);
                        if(data.items.length < $scope.searchFilter.limit){
                            $scope.stopLoad = true;
                        }
                        if(isClean){
                            $scope.items = [];
                        }

                        angular.forEach(data.items, function (item) {
                            item.item.url = item.imageUrl;
                            $scope.items.push(item.item);
                        });

                        if($scope.searchFilter.category){
                            angular.forEach(data.filter, function (category) {
                                $scope.sideFilters.push({id:category.id, name:category.name});
                            })
                        }
                        console.log($scope.stopLoad);
                        portion++;
                        $scope.searchFilter.offset = portion * $scope.searchFilter.limit;
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

            /* something for filter */
            /* with will work when side click */
            $scope.$on('onFilter',function() {
                var node = eventService.data;
                console.log(node);
                if(node.company){
                    $scope.searchFilter.company = node.id;
                    $scope.searchFilter.category = null;
                } else {
                    $scope.searchFilter.company = null;
                    $scope.searchFilter.category = node.id;
                    $scope.showSideFilter = true;
                }
                portion = 0;
                $scope.searchFilter.offset = 0;
                $scope.stopLoad = false;
                $scope.loadData(true);
            });

            /* load page timeout */
            $timeout(function() {
                $(".collapsible").collapsible();
            }, 10);


            $scope.search = function(){
                if(!$scope.searchFilter.criteria){
                    $('#search').focus();
                } else {
                    console.log("click search");
                    dataResources.searchItem.get({criteria:$scope.searchFilter.criteria}).$promise.then(function(data){
                        console.log(data);
                        $scope.searchResult = data;
                        $scope.showResults = true;
                        portion = 0;
                        $scope.searchFilter.offset = 0;
                        $scope.stopLoad = false;
                        $scope.loadData(true);
                    });
                }
            };

            $scope.keyPress = function(keyCode) {
                if (keyCode == 13){
                    $scope.search();
                }
            };

            $scope.clear = function(){
                $scope.searchFilter.criteria = null;
                $scope.showResults = false;
            };


            $scope.removeFilterElem = function(idx){
                $scope.sideFilters.splice(idx,1);
                //TODO: maybe change to server clean

                if($scope.sideFilters.length==0){
                    $scope.showSideFilter = false;
                }
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
