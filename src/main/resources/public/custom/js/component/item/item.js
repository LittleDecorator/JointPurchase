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
                $scope.confirmedFilter = angular.copy($scope.filter);
                localStorage.removeItem($state.current.name);
                $scope.stopLoad = false;
                $scope.loadData(true);
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
        }])

        .controller('itemDetailController',['$rootScope','$scope','$state','dataResources','modal','$timeout','item','companies','$mdToast','$filter','statuses', function ($rootScope,$scope, $state, dataResources,modal,$timeout,item,companies,$mdToast,$filter,statuses){
            $scope.item = item;
            $scope.companyNames = companies;
            $scope.statuses = statuses;
            $scope.showHints = true;

            var elem = helpers.findInArrayById(statuses,$scope.item.status.id);
            console.log(elem);

            if($scope.item.price){
                $scope.item.price = $filter('number')($scope.item.price);
            }

            if(!$scope.item.categories){
                $scope.item.categories = [];
            }

            if(!$scope.item.inStock){
                $scope.item.inStock = 0;
            }

            /* Удаление категории из товара */
            $scope.removeCategory = function(idx){
                $scope.item.categories.splice(idx,1);
                if($scope.item.categories.length == 0){
                    $scope.itemCard.categories.$error.required = true;
                    $scope.itemCard.categories.$setValidity("required", false);
                    $('md-chips-wrap').addClass('md-chips-invalid');
                }
            };

            /* Открытие модального окна для выбора категории */
            $scope.showCategoryModal = function(){
                var selected = [];
                if($scope.item.categories && $scope.item.categories.length > 0){
                    selected = $scope.item.categories.map(function(category){
                        return category['id'];
                    })
                }
                var dialog = modal({templateUrl:"pages/modal/categoryModal.html",className:'ngdialog-theme-default custom-width',closeByEscape:true,controller:"categoryClssController",data:selected});
                dialog.closePromise.then(function(output) {
                    if(output.value && output.value != '$escape'){
                        $scope.item.categories = output.value;
                        $scope.itemCard.categories.$error = {};
                        $scope.itemCard.categories.$setValidity("required", true);
                        $('md-chips-wrap').removeClass('md-chips-invalid');
                    }
                });
            };

            /* Сохранение товара */
            $scope.save = function () {
                var toast = $mdToast.simple().position('top right').hideDelay(3000);

                function refreshState(data){
                    $scope.itemCard.$setPristine();
                    /* refresh state because name can be changed */
                    $state.go($state.current,{id:data.result},{notify:false}).then(function(){
                        $scope.item.id = data.result;
                        $rootScope.$broadcast('$refreshBreadcrumbs',$state);
                    });
                }

                if($scope.itemCard.$dirty){
                    if($scope.itemCard.$valid){
                        if($scope.item.id){
                            dataResources.item.put($scope.item).$promise.then(function(data){
                                $mdToast.show(toast.textContent('Товар ['+ $scope.item.name +'] успешно изменён').theme('success'));
                                refreshState({result:$scope.item.id});
                            }, function(error){
                                $mdToast.show(toast.textContent('Неудалось сохранить изменения').theme('error'));
                            })
                        } else {
                            dataResources.item.post($scope.item).$promise.then(function(data){
                                $mdToast.show(toast.textContent('Товар ['+ $scope.item.name +'] успешно создан').theme('success'));
                                refreshState(data);
                            }, function(error){
                                $mdToast.show(toast.textContent('Неудалось создать новый товар').theme('error'));
                            })
                        }
                        $scope.showHints = true;
                    } else {
                        $scope.showHints = false;
                    }
                }
            };

            /* Переход в галерею данного товара */
            $scope.showGallery = function () {
                $state.go("item.detail.gallery", {id: $scope.item.id});
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

        }])

})();
