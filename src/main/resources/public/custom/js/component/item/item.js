(function(){
        angular.module('item',[]);
})();

(function(){
    'use strict';

    angular.module('item')
        
        /* Контроллер товара */
        .controller('itemController',['$scope','$state','dataResources','$timeout','companies', 'modal', function ($scope, $state, dataResources,$timeout, companies, modal) {

            $scope.items = [];
            $scope.companyNames = companies;
                        
            $scope.filter = {name:null, article:null, company:null, category:null, limit:30, offset:0};

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

                        $scope.items = $scope.items.concat(data);

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
                console.log("in clear item");
                portion = 0;
                $scope.filter = {name:null, article:null, companyId:null, categoryId:null, limit:30, offset:0};
                $scope.confirmedFilter = angular.copy($scope.filter);
                localStorage.removeItem($state.current.name);
                $scope.stopLoad = false;
                $scope.loadData(true);
            };

            /* apply filter */
            $scope.apply = function () {
                console.log($scope);
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

            // модальное окно фильтрации
            $scope.openFilter = function (wClass) {
                var dialog = modal({
                    templateUrl: "pages/modal/items-filter.html",
                    className: 'ngdialog-theme-default ' + wClass,
                    closeByEscape: true,
                    closeByDocument: true,
                    scope: $scope
                });
                dialog.closePromise.then(function (output) {
                    if (output.value && output.value != '$escape') {
                    }
                });
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

        /* Карточка товара */
        .controller('itemDetailController',['$rootScope','$scope','$stateParams','$state','dataResources','modal','$timeout','item','companies','$mdToast','$filter','statuses', function ($rootScope,$scope, $stateParams, $state, dataResources,modal,$timeout,item,companies,$mdToast,$filter,statuses){
            $scope.item = item ? item : {};

            $scope.companyNames = companies;
            $scope.statuses = statuses;
            console.log($scope.statuses);
            $scope.showHints = true;

            // var elem = helpers.findInArrayById(statuses, $scope.item.status.id);

            //парсим стоимость в денежный формат
            if($scope.item.price){
                $scope.item.price = $filter('number')($scope.item.price);
            }

            // если категорий нет, то инициализация пустым массивом
            if(!$scope.item.categories){
                $scope.item.categories = [];
            }

            // инициализация кол-ва товара в наличие
            if(!$scope.item.inStock){
                $scope.item.inStock = 0;
            }

            // инициализация кол-ва товара в наличие
            if(!$scope.item.inOrder){
                $scope.item.inOrder = 0;
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

                //получение данных при закрытие модального окна категорий
                dialog.closePromise.then(function(output) {
                    if(output.value && output.value != '$escape'){
                        $scope.item.categories = output.value;
                        console.log($scope.itemCard);
                        $scope.itemCard.categories.$error = {};
                        $scope.itemCard.categories.$setValidity("required", true);
                        $('md-chips-wrap').removeClass('md-chips-invalid');
                    }
                });
            };

            /* Сохранение товара */
            $scope.save = function () {
                console.log("save");
                var toast = $mdToast.simple().position('top right').hideDelay(3000);

                // изменение состояния breadCrumbs
                function refreshState(data){
                    $scope.itemCard.$setPristine();
                    /* refresh state because name can be changed */
                    $state.go($state.current,{id:data.result},{notify:false}).then(function(){
                        $scope.item.id = data.result;
                        $stateParams.id = data.result;
                        $rootScope.$broadcast('$refreshBreadcrumbs',$state);
                    });
                }

                if($scope.itemCard.$dirty){
                    console.log("dirty");
                    console.log($scope.itemCard);
                    if($scope.itemCard.$valid){
                        console.log("valid");
                        if($scope.item.id){
                            console.log("try put");
                            // если товар был на редактирование
                            dataResources.item.put($scope.item).$promise.then(function(data){
                                console.log(data);
                                $mdToast.show(toast.textContent('Товар ['+ $scope.item.name +'] успешно изменён').theme('success'));
                                //нужно сбросить состояние, т.к может измениться имя товара
                                refreshState({result:$scope.item.id});
                            }, function(error){
                                $mdToast.show(toast.textContent('Неудалось сохранить изменения').theme('error'));
                            })
                        } else {
                            console.log("try post");
                            dataResources.item.post($scope.item).$promise.then(function(data){
                                console.log(data);
                                $mdToast.show(toast.textContent('Товар ['+ $scope.item.name +'] успешно создан').theme('success'));
                                // изменилось состояние.
                                refreshState(data);
                            }, function(error){
                                $mdToast.show(toast.textContent('Неудалось создать новый товар').theme('error'));
                            })
                        }
                        $scope.showHints = true;
                    } else {
                        console.log($scope.item);
                        console.log("not valid");
                        $scope.showHints = false;
                    }
                } else {
                    console.log("not dirty");
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

            // помечаем scope как чистый
            $scope.afterInclude = function(){
                $timeout(function(){
                    $scope.itemCard.$setPristine(true);
                },50);
            }

        }])

})();
