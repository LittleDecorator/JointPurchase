(function(){
        angular.module('item',[]);
})();

(function(){
    'use strict';

    angular.module('item')
        .controller('itemController',['$scope','$state','dataResources','$timeout', function ($scope, $state, dataResources,$timeout) {
            console.log("enter item controller");
            //maps
            dataResources.companyMap.get(function(res){
                $scope.companyNames = [];
                angular.forEach(res, function (comp) {
                    //console.log(comp);
                    $scope.companyNames.push(comp);
                    if($scope.selected){
                        $scope.selected.company = helpers.findInArrayById($scope.companyNames, $scope.selected.companyId);
                    }
                });

            });

            //filter
            $scope.filter = {};
            $scope.filter.selectedCompany = "";
            $scope.filter.selectedCategory = "";

            //main
            $scope.index = null;
            $scope.items = [];
            $scope.filteredItems = [];

            //pagination
            $scope.currPage = 1;
            $scope.maxPage = 5;
            $scope.itemsPerPage = 10;
            $scope.totalItems = null;

            $scope.needInit= true;

            //get all items
            $timeout(function(){
                dataResources.item.query(function (data) {
                    angular.forEach(data, function (item) {
                        var company = helpers.findInArrayById($scope.companyNames, item.companyId);
                        item.companyName = company.name;
                        //var category = helpers.findInArrayById($scope.categories, item.categoryId);
                        //item.category = category.name;
                        $scope.items.push(item);
                    });
                    //get total items cou, for pagination
                    $scope.totalItems = $scope.items.length;

                    //listener for some scope variables
                    $scope.$watch('currPage + itemsPerPage', function () {
                        $scope.subList();
                    });
                });
            },100);


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
                $state.go("item.detail", {id: id});
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


            //filter table
            $scope.filter.apply = function () {
                $scope.items = [];
                dataResources.itemFilter.apply(helpers.getFilterItem(), function (data) {
                    angular.forEach(data, function (item) {
                        var company = helpers.findInArrayById($scope.companyNames, item.companyId);
                        item.companyName = company.name;
                        //var category = helpers.findInArrayById($scope.types, item.typeId);
                        //item.category = category.name;
                        $scope.items.push(item);
                    });
                    $scope.currPage = 1;
                    $scope.subList();
                })
            };

            //clear filter
            $scope.filter.clear = function () {
                helpers.clearFilterItem();
                $('.filter-item .active').removeClass('active');
                $scope.items = [];
                dataResources.item.query(function (data) {
                    angular.forEach(data, function (item) {
                        var company = helpers.findInArrayById($scope.companyNames, item.companyId);
                        item.companyName = company.name;
                        //var category = helpers.findInArrayById($scope.categories, item.categoryId);
                        //item.category = category.name;
                        $scope.items.push(item);
                    });
                    //get total items cou, for pagination
                    $scope.totalItems = $scope.items.length;

                    $scope.currPage = 1;
                    $scope.subList();
                });
            };

            $scope.showGallery = function (id) {
                $scope.currentItem = helpers.findInArrayById($scope.filteredItems, id);
                $state.go("item.detail.gallery", {id: id});
            };

            //изъятие\включение в продажу
            $scope.forSaleToggle = function(idx){
                console.log(idx);
                console.log($scope.filteredItems);
                var item = $scope.filteredItems[idx];
                console.log(item);
                dataResources.notForSale.toggle({itemId:item.id,notForSale:item.notForSale});
            };

        }])

        .controller('itemDetailController',['$scope','$state','item','dataResources','categoryClssModal','eventService', function ($scope, $state, item, dataResources,categoryClssModal,eventService){

            console.log("itemDetailController");

            var elt = $('#multiple');

            dataResources.categoryMap.get(function(res) {
                $scope.categories=[];
                angular.forEach(res, function (comp) {
                    $scope.categories.push({id:comp.id,name:comp.name});
                });
            });

            if(item){
                $scope.selected = angular.copy(item);
                if(!$scope.selected.inStock){
                    $scope.selected.inStock = 0;
                }
            } else {
                $scope.selected = {company:{},type:{}};
            }

            //company map
            dataResources.companyMap.get(function(res){
                $scope.companyNames = [];
                angular.forEach(res, function (comp) {
                    $scope.companyNames.push(comp);
                });
                if($scope.selected.companyId){
                    $scope.selected.company = helpers.findInArrayById($scope.companyNames, $scope.selected.companyId);
                }

            });

            $scope.showClss = function(){
                $scope.modal = categoryClssModal( elt.materialtags('items'));
            };

            //modal button save listener
            $scope.save = function () {
                $scope.selected.companyId = $scope.selected.company.id;
                dataResources.item.save($scope.selected, function (data){
                    $scope.selected = data;
                    elt.materialtags('removeAll');
                    angular.forEach(eventService.data,function(cat){
                        elt.materialtags('add', { "value": cat.id , "text": cat.name});
                        $scope.selected.categories.push(cat);
                    });
                });
            };

            function initMulti() {

                var categories = new Bloodhound({
                    datumTokenizer: Bloodhound.tokenizers.obj.whitespace('text'),
                    queryTokenizer: Bloodhound.tokenizers.whitespace
                });

                categories.initialize();

                elt.materialtags({
                    itemValue: 'value',
                    itemText: 'text',
                    typeaheadjs: {
                        name: 'cities',
                        displayKey: 'text',
                        source: categories.ttAdapter()
                    }
                });

            }

            function initCategories() {
                angular.forEach($scope.selected.categories,function(cat){
                    elt.materialtags('add', { "value": cat.id , "text": cat.name});
                });
            }

            initMulti();

            initCategories();

            $scope.$on('onCategoryClssSelected',function(){
                $scope.selected.categories = [];
                elt.materialtags('removeAll');
                angular.forEach(eventService.data,function(cat){
                    elt.materialtags('add', { "value": cat.id , "text": cat.name});
                    $scope.selected.categories.push(cat);
                });
            });

            $scope.companyChanged = function(){
                var elem = $('#company .select-wrapper');
                if(($scope.filter && $scope.filter.selectedCompany == null) || $scope.selected.company == null){
                    angular.element(elem).addClass('inactive');
                } else {
                    if(angular.element(elem).hasClass('inactive')){
                        angular.element(elem).removeClass('inactive');
                    }
                }
            };

            $scope.showGallery = function () {
                $state.go("item.detail.gallery", {id: item.id});
            };

            //$scope.typeChanged = function(){
            //    var elem = $('#type .select-wrapper');
            //    if($scope.filter.selectedType == null || $scope.selected.type == null){
            //        angular.element(elem).addClass('inactive');
            //    } else {
            //        if(angular.element(elem).hasClass('inactive')){
            //            angular.element(elem).removeClass('inactive');
            //        }
            //    }
            //};

        }])

})();
