(function(){
        angular.module('item',[]);
})();

(function(){
    'use strict';

    angular.module('item')
        .controller('itemController',['$scope','$state','dataResources','$timeout','companies', function ($scope, $state, dataResources,$timeout, companies) {

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

                        if(data.length < $scope.confirmedFilter.limit){
                            $scope.stopLoad = true;
                        }

                        if(isClean){
                            $scope.items = [];
                        }

                        console.log(data);
                        $scope.items = data;

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
                $scope.filter = {name:null, article:null, companyId:null, categoryId:null, limit:30, offset:0};
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
                var templatePath = "pages/fragment/items/";
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

        .controller('itemDetailController',['$rootScope','$scope','$state','dataResources','modal','$timeout','item','companies', function ($rootScope,$scope, $state, dataResources,modal,$timeout,item,companies){
            console.log(item);
            $scope.selected = item;
            $scope.companyNames = companies;
            $scope.companyNames.unshift({id:null,name:"Выберите производителя ..."});

            if(!$scope.selected.inStock){
                $scope.selected.inStock = 0;
            }

            /* Удаление категории из товара */
            $scope.removeCategory = function(idx){
                $scope.selected.categories.splice(idx,1);
            };

            /* Открытие модального окна для выбора категории */
            $scope.showCategoryModal = function(){
                var selected = [];
                if($scope.selected.categories && $scope.selected.categories.length > 0){
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

            /* Сохранение товара */
            $scope.save = function () {
                if(!$scope.itemCard.$pristine && !$scope.itemCard.$invalid){
                    if($scope.selected.id){
                        dataResources.item.put($scope.selected).$promise.then(function(data){
                            Materialize.toast('Товар успешно изменён', 3000, 'fine');
                            $scope.itemCard.$setPristine();
                            $state.go($state.current,{id:$scope.selected.id},{notify:false}).then(function(){
                                $rootScope.$broadcast('$refreshBreadcrumbs',$state);
                            });
                        }, function(error){
                            Materialize.toast('Неудалось сохранить изменения', 3000, 'error');
                        })
                    } else {
                        dataResources.item.post($scope.selected).$promise.then(function(data){
                            Materialize.toast('Товар успешно создан', 3000, 'fine');
                            $scope.itemCard.$setPristine();
                            $state.go($state.current,{id:data.result},{notify:false}).then(function(){
                                $scope.selected.id = data.result;
                                $rootScope.$broadcast('$refreshBreadcrumbs',$state);
                            });
                        }, function(error){
                            Materialize.toast('Неудалось создать новый товар', 3000, 'error');
                        })
                    }
                }
            };

            /* Переход в галерею данного товара */
            $scope.showGallery = function () {
                $state.go("item.detail.gallery", {id: $scope.selected.id});
            };

            /* Получения шаблона страницы */
            $scope.getTemplateUrl = function(){
                var templatePath = "pages/fragment/items/card/";
                if($scope.width < 601){
                    return templatePath + "item-card-sm.html";
                } else {
                    return templatePath + "item-card-lg.html";
                }
            };

            /* Callback после загрузки шаблона */
            $scope.afterInclude = function(){
                $('select').material_select();
            };

            /* Post-init обработка */
            $timeout(function(){
                $('select').material_select();
                console.log($scope);
            },20);

        }])

})();
