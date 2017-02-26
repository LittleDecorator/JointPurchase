(function(){
    angular.module('catalog',[]);
})();

(function() {
    'use strict';

    angular.module('catalog')
        .controller('catalogController', ['$scope', '$state','dataResources','$timeout','eventService','$stateParams','$rootScope','node',function ($scope, $state, dataResources,$timeout,eventService,$stateParams,$rootScope,node) {

            console.log("catalogController");

            $scope.items = [];
            $scope.searchFilter = {category:null, company:null, criteria:null, offset:0, limit:30};
            $scope.sideFilters = [];

            if(node){
                if($stateParams.type == 'category') {
                    $scope.searchFilter.category = $stateParams.id
                } else {
                    $scope.searchFilter.company = $stateParams.id
                }
            }

            var busy = false;
            var portion = 0;
            $scope.showResults = false;
            $scope.showSideFilter = false;
            $scope.stopLoad=false;
            $scope.allDataLoaded = false;
            $scope.infiniteDistance = 2;

            //get items
            $scope.loadData = function(isClean){
                if(!$scope.stopLoad && !busy){
                    busy = true;
                    dataResources.catalog.list.all($scope.searchFilter).$promise.then(function (data) {

                        if(data.length < $scope.searchFilter.limit){
                            $scope.stopLoad = true;
                        }

                        if(isClean){
                            $scope.items = [];
                        }

                        $scope.items = data;

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
                var type;
                if(node.company){
                    $scope.searchFilter.company = node.id;
                    $scope.searchFilter.category = null;
                    type = 'company';
                } else {
                    $scope.searchFilter.company = null;
                    $scope.searchFilter.category = node.id;
                    $scope.showSideFilter = true;
                    type = 'category';
                }
                $state.go('catalog.type',{id:node.id,type:type},{notify:false}).then(function(){
                    $rootScope.$broadcast('$refreshBreadcrumbs',$state);
                });
                portion = 0;
                $scope.searchFilter.offset = 0;
                $scope.stopLoad = false;
                $scope.loadData(true);
            });

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
            console.log(product);

            $scope.THUMB_URL = "media/image/thumb/";
            $scope.PREVIEW_URL = "media/image/preview/";
            $scope.VIEW_URL = "media/image/view/";
            $scope.ORIG_URL = "media/image/";

            if($scope.item.itemContents == null){
                $scope.mainImage = $scope.item.url;
            } else {
                $scope.mainImage = $scope.ORIG_URL + $scope.item.itemContents[0].contentId;
            }

            $scope.show = function(id){
                var keepGoing = true;
                var res = null;

                $scope.item.itemContents.forEach(function(elem,index){
                    if (keepGoing) {
                        if(elem === id) {
                            res = index;
                            keepGoing = false;
                        }
                    }
                });
                $scope.mainImage = $scope.ORIG_URL + id;
            };

            $scope.showGallery = function () {
                $state.go("product.detail.gallery", {id: $scope.item.id});
            };

        }]);
})();
