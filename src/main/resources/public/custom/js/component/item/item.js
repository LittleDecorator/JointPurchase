(function(){
        angular.module('item',[]);
})();

(function(){
    'use strict';

    angular.module('item')
        .controller('itemController',['$scope','$state','dataResources','$timeout','companies', function ($scope, $state, dataResources,$timeout, companies) {
            var templatePath = "pages/fragment/items/";
            $scope.companyNames = companies;
            $scope.items = [];

            $scope.filter = {name:null, article:null, companyId:null, categoryId:null, limit:30, offset:0};
            $scope.confirmedFilter = angular.copy($scope.filter);

            var busy = false;
            var portion = 0;
            $scope.stopLoad=false;
            $scope.allDataLoaded = false;
            $scope.infiniteDistance = 2;

            $scope.loadData = function(isClean){
                if(!$scope.stopLoad && !busy){
                    busy = true;

                    dataResources.item.all($scope.confirmedFilter).$promise.then(function(data){
                        console.log(data);
                        if(data.length < $scope.confirmedFilter.limit){
                            $scope.stopLoad = true;
                        }

                        if(isClean){
                            $scope.items = [];
                        }

                        $scope.items = data;

                        //angular.forEach(data, function (item) {
                        //    var company = helpers.findInArrayById(companies, item.companyId);
                        //    item.companyName = company.name;
                        //    $scope.items.push(item);
                        //});

                        portion++;
                        $scope.confirmedFilter.offset = portion * $scope.confirmedFilter.limit;
                        $scope.allDataLoaded = true;
                        busy = false;
                    });
                }
            };

            //open modal for new item creation
            $scope.addItem = function () {
                $state.transitionTo("item.detail");
            };

            //edit item
            $scope.editItem = function (id) {
                $state.go("item.detail", {id: id});
            };

            //delete current item
            $scope.deleteItem = function (id,idx) {
                //TODO: обрабатывать ситуацию, когда на странице был удален последняя запись
                //delete item from db
                dataResources.item.delete({id: id});
                //find item in array
                $scope.items.splice(idx, 1);
            };

            //clear filter
            $scope.clear = function () {
                portion = 0;
                $scope.filter = {name:null, article:null, selectedCompany:null, selectedCategory:null, limit:30, offset:0};
                localStorage.removeItem($state.current.name);
                $scope.stopLoad = false;
                
                $scope.loadData(true);
                $timeout(function(){
                    $('select').material_select();
                },10);
            };

            /* apply filter */
            $scope.apply = function () {
                portion = 0;
                $scope.filter.offset = portion * $scope.filter.limit;
                $scope.confirmedFilter = angular.copy($scope.filter);
                localStorage.setItem($state.current.name,angular.toJson($scope.confirmedFilter));
                $scope.stopLoad = false;
                $scope.loadData(true);
            };

            /* show gallery for specific item */
            $scope.showGallery = function (id) {
                $state.go("item.detail.gallery", {id: id});
            };

            //изъятие\включение в продажу
            $scope.forSaleToggle = function(item){
                dataResources.notForSale.toggle({itemId:item.id,notForSale:item.notForSale});
            };

            $scope.getTemplate = function(){
                if($scope.width < 601){
                    return templatePath + "items-sm.html"
                }
                if($scope.width > 600){
                    if($scope.width < 961){
                        return templatePath + "items-md.html"
                    }
                    return templatePath + "items-lg.html"
                }
            };

            $timeout(function(){
                $('select').material_select();
            },10);
        }])

        .controller('itemDetailController',['$scope','$state','resolved','dataResources','modal','$timeout', function ($scope, $state, resolved, dataResources,modal,$timeout){
            var templatePath = "pages/fragment/items/card/";

            $scope.selected = resolved[0];
            $scope.categories = [];
            angular.forEach(resolved[1], function (category) {
                $scope.categories.push({id:category.id,name:category.name});
            });
            $scope.companyNames = [];
            angular.forEach(resolved[2], function (company) {
                $scope.companyNames.push(company);
            });
            $scope.companyNames.unshift({id:null,name:"Выберите производителя ..."});

            if(!$scope.selected.inStock){
                $scope.selected.inStock = 0;
            }
            
            $scope.removeCategory = function(idx){
                $scope.selected.categories.splice(idx,1);
            };
            
            $scope.showCategoryModal = function(){
                var selected = [];
                if($scope.selected.categories.length > 0){
                    selected = $scope.selected.categories.map(function(category){
                        return category['id'];
                    })
                }
                var dialog = modal({templateUrl:"pages/modal/categoryModal.html",className:'ngdialog-theme-default custom-width',closeByEscape:true,controller:"categoryClssController",data:selected});
                dialog.closePromise.then(function(output) {
                    if(output.value && output.value != '$escape'){
                        $scope.selected.categories = output.value;
                    }
                });
            };

            //modal button save listener
            $scope.save = function () {
                var categories = $scope.selected.categories.map(function(category){
                    return category['id'];
                });

                var dto = {
                    item: $scope.selected,
                    categories: categories
                };

                if($scope.selected.id){
                    dataResources.item.put(dto).$promise.then(function(data){
                        Materialize.toast('Item UPDATE success',3000);
                    }, function(error){
                        Materialize.toast('Item UPDATE failed',3000);
                    })
                } else {
                    dataResources.item.post(dto).$promise.then(function(data){
                        Materialize.toast('Item CREATE success',3000);
                    }, function(error){
                        Materialize.toast('Item CREATE failed',3000);
                    })
                }
            };

            $scope.showGallery = function () {
                $state.go("item.detail.gallery", {id: $scope.selected.id});
            };

            $scope.getTemplateUrl = function(){
                if($scope.width < 601){
                    return templatePath + "item-card-sm.html";
                } else {
                    return templatePath + "item-card-lg.html";
                }
            };

            $scope.afterInclude = function(){
                $('select').material_select();
            };

        }])

})();
