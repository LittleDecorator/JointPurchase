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
            dataResources.typeMap.get(function(res) {
                $scope.types=[];
                angular.forEach(res, function (comp) {
                    //console.log(comp);
                    $scope.types.push(comp);
                    if($scope.selected){
                        $scope.selected.type = helpers.findInArrayById($scope.types, $scope.selected.typeId);
                    }
                });
            });

            //filter
            $scope.filter = {};
            $scope.filter.selectedCompany = "";
            $scope.filter.selectedType = "";

            //selected
            $scope.selected = {};
            $scope.selected.company = {};
            $scope.selected.type = {};

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
            } else {
                //get all items
                dataResources.item.query(function (data) {
                    angular.forEach(data, function (item) {
                        var company = helpers.findInArrayById($scope.companyNames, item.companyId);
                        item.companyName = company.name;
                        var type = helpers.findInArrayById($scope.types, item.typeId);
                        item.type = type.name;
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
                $scope.selected.type = {};
            };

            //edit item
            $scope.editItem = function (id) {
                console.log($state);
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

            //modal button save listener
            $scope.save = function () {
                $scope.selected.companyId = $scope.selected.company.id;
                $scope.selected.typeId = $scope.selected.type.id;
                dataResources.item.save($scope.selected);
            };

            //filter table
            $scope.filter.apply = function () {
                $scope.items = [];
                dataResources.itemFilter.apply(helpers.getFilterItem(), function (data) {
                    angular.forEach(data, function (item) {
                        var company = helpers.findInArrayById($scope.companyNames, item.companyId);
                        item.companyName = company.name;
                        var type = helpers.findInArrayById($scope.types, item.typeId);
                        item.type = type.name;
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
                        var type = helpers.findInArrayById($scope.types, item.typeId);
                        item.type = type.name;
                        $scope.items.push(item);
                    });
                    $scope.currPage = 1;
                    $scope.subList();
                });
            };

            $scope.companyChanged = function(){
                console.log("company changed");
                var elem = $('#company .select-wrapper');
                console.log($scope.selected.company)
                if($scope.filter.selectedCompany == null || $scope.selected.company == null){
                    console.log("add inactive");
                    angular.element(elem).addClass('inactive');
                } else {
                    if(angular.element(elem).hasClass('inactive')){
                        console.log("remove inactive");
                        angular.element(elem).removeClass('inactive');
                    }
                }
            };

            $scope.typeChanged = function(){
                console.log("type changed");
                var elem = $('#type .select-wrapper');
                if($scope.filter.selectedType == null || $scope.selected.type == null){
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
            };

            //изъятие\включение в продажу
            $scope.forSaleToggle = function(idx){
                console.log(idx);
                console.log($scope.filteredItems);
                var item = $scope.filteredItems[idx];
                console.log(item);
                dataResources.notForSale.toggle({itemId:item.id,notForSale:item.notForSale});
            };

            console.log($scope);

        }])



})();
