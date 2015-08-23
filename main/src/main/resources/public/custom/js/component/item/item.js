(function(){
        angular.module('item',[]);
})();

(function(){
    'use strict';

    angular.module('item')
        .controller('itemController',['$scope','$state','item','dataResources', function ($scope, $state, item, dataResources) {
            console.log("enter item controller");
            //for filter you may use % for replace other symbols

            //maps
            $scope.companyNames = dataResources.companyMap.get();
            $scope.categoryTypes = dataResources.categoryMap.get();

            //filter
            $scope.filter = {};
            $scope.filter.selectedCompany = "";
            $scope.filter.selectedCategory = "";

            //selected
            $scope.selected = {};
            $scope.selected.company = {};
            $scope.selected.category = {};

            //main
            $scope.index = null;
            $scope.items = [];
            $scope.filteredItems = [];

            //pagination
            $scope.currPage = 1;
            $scope.maxPage = 5;
            $scope.itemsPerPage = 10;
            $scope.totalItems = null;

            console.log(item);

            $scope.needInit= true;

            if(item){
                $scope.selected = item;
                //find company in company list for select
                $scope.selected.company = helpers.findInArrayById($scope.companyNames, $scope.selected.companyId);
                //find category in category list for select
                $scope.selected.category = helpers.findInArrayById($scope.categoryTypes, $scope.selected.categoryId);
            } else {
                console.log("get all");
                //get all items
                dataResources.item.query(function (data) {
                    angular.forEach(data, function (item) {
                        var company = helpers.findInArrayById($scope.companyNames, item.companyId);
                        item.companyName = company.name;
                        var category = helpers.findInArrayById($scope.categoryTypes, item.categoryId);
                        item.categoryType = category.name;
                        $scope.items.push(item);
                    });
                    //get total items cou, for pagination
                    $scope.totalItems = $scope.items.length;

                    //listener for some scope variables
                    $scope.$watch('currPage + itemsPerPage', function () {
                        $scope.subList();
                    });
                });
            }

            //get sub list of items for single page display
            $scope.subList = function () {
                var begin = (($scope.currPage - 1) * $scope.itemsPerPage),
                    end = begin + $scope.itemsPerPage;

                $scope.filteredItems = $scope.items.slice(begin, end);
            };

            //open modal for new item creation
            $scope.addItem = function () {
                this.clearSelected();
                $state.transitionTo("item.detail");
            };

            $scope.clearSelected = function(){
                $scope.selected = {};
                $scope.selected.company = {};
                $scope.selected.category = {};
            };

            //edit item
            $scope.editItem = function (id) {
                $state.transitionTo("item.detail", {id: id});

            };

            //delete current item
            $scope.deleteItem = function (id) {
                //TODO: обрабатывать ситуацию, когда на странице был удален последняя запись
                //TODO: обрабатывать ситуацию, когда страница одна
                //delete item from db
                dataResources.item.delete({id: id});
                //find item in array
                var currItem = helpers.findInArrayById($scope.items, id);
                //find item index in array
                var idx = $scope.items.indexOf(currItem);
                //remove item from array
                $scope.items.splice(idx, 1);
                $scope.subList();
            };

            //modal button save listener
            $scope.save = function () {
                $scope.selected.companyId = $scope.selected.company.id;
                $scope.selected.categoryId = $scope.selected.category.id;
                dataResources.item.save($scope.selected);
            };

            //filter table
            $scope.filter.apply = function () {
                $scope.items = [];
                dataResources.itemFilter.apply(helpers.getFilterItem(), function (data) {
                    angular.forEach(data, function (item) {
                        var company = helpers.findInArrayById($scope.companyNames, item.companyId);
                        item.companyName = company.name;
                        var category = helpers.findInArrayById($scope.categoryTypes, item.categoryId);
                        item.categoryType = category.name;
                        $scope.items.push(item);
                    });
                    $scope.currPage = 1;
                    $scope.subList();
                })
            };

            //clear filter
            $scope.filter.clear = function () {
                helpers.clearFilterItem();
                $scope.items = [];
                dataResources.item.query(function (data) {
                    angular.forEach(data, function (item) {
                        var company = helpers.findInArrayById($scope.companyNames, item.companyId);
                        item.companyName = company.name;
                        var category = helpers.findInArrayById($scope.categoryTypes, item.categoryId);
                        item.categoryType = category.name;
                        $scope.items.push(item);
                    });
                    $scope.currPage = 1;
                    $scope.subList();
                });
            };

            $scope.companyChanged = function(){
                console.log("company changed");
                var elem = $('#company .select-wrapper');
                if($scope.filter.selectedCompany == null){
                    angular.element(elem).addClass('inactive');
                } else {
                    if(angular.element(elem).hasClass('inactive')){
                        angular.element(elem).removeClass('inactive');
                    }
                }
            };

            $scope.categoryChanged = function(){
                console.log("category changed");
                var elem = $('#category .select-wrapper');
                if($scope.filter.selectedCategory == null){
                    angular.element(elem).addClass('inactive');
                } else {
                    if(angular.element(elem).hasClass('inactive')){
                        angular.element(elem).removeClass('inactive');
                    }
                }
            };

            $scope.showGallery = function (id) {
                $scope.currentItem = helpers.findInArrayById($scope.filteredItems, id);
                $state.go("item.gallery", {itemId: id});
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


            $scope.mainImage = $scope.item.media[0];
            if($scope.item.media.length>1){
                $scope.item.media.splice(0,1);
            }

            $scope.next = function(id){
                $scope.item.media.push($scope.mainImage);

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
                $scope.item.media.splice(res,1);
            };

            $scope.view= function(){
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
            }

        }]);
})();
