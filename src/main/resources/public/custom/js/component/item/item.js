(function(){
        angular.module('item',[]);
})();

(function(){
    'use strict';

    angular.module('item')
        
        /* Контроллер товара */
        .controller('itemController',['$scope','$state','dataResources','$timeout','companies', 'modal',
            function ($scope, $state, dataResources,$timeout, companies, modal) {

                var busy = false;
                var portion = 0;
                var mvm = $scope.$parent.mvm;
                var vm = this;

                vm.loadData = loadData;
                vm.addItem = addItem;
                vm.editItem = editItem;
                vm.deleteItem = deleteItem;
                vm.clear = clear;
                vm.apply = apply;
                vm.showGallery = showGallery;
                vm.forSaleToggle = forSaleToggle;
                vm.openFilter = openFilter;
                vm.getTemplate = getTemplate;

                vm.items = [];
                vm.companyNames = companies;
                vm.filter = {name:null, article:null, company:null, category:null, limit:30, offset:0};
                vm.confirmedFilter = angular.copy(vm.filter);
                vm.stopLoad=false;
                vm.allDataLoaded = false;
                vm.infiniteDistance = 1;
                
                /* получение данных с сервера */
                function loadData(isClean){
                    if(!vm.stopLoad && !busy){
                        busy = true;

                        dataResources.item.all(vm.confirmedFilter).$promise.then(function(data){

                            if(data.length < vm.confirmedFilter.limit){
                                vm.stopLoad = true;
                            }

                            if(isClean){
                                vm.items = [];
                            }

                            vm.items = vm.items.concat(data);

                            portion++;
                            vm.confirmedFilter.offset = portion * vm.confirmedFilter.limit;
                            vm.allDataLoaded = true;
                            busy = false;
                        });
                    }
                }

                /* переход в карточку для создания нового товара */
                function addItem () {
                    $state.transitionTo("item.detail");
                }

                /* переход в карточку для редактирования */
                function editItem (id) {
                    $state.go("item.detail", {id: id});
                }

                /* удаление товара */
                function deleteItem(id,idx) {
                    //TODO: обрабатывать ситуацию, когда на странице был удален последняя запись
                    //delete item from db
                    dataResources.item.delete({id: id});
                    //find item in array
                    vm.items.splice(idx, 1);
                }

                /* сброс фильтра */
                function clear() {
                    portion = 0;
                    vm.filter = {name:null, article:null, companyId:null, categoryId:null, limit:30, offset:0};
                    vm.confirmedFilter = angular.copy(vm.filter);
                    localStorage.removeItem($state.current.name);
                    vm.stopLoad = false;
                    loadData(true);
                }

                /* применение фильтра */
                function apply() {
                    portion = 0;
                    vm.filter.offset = portion * vm.filter.limit;
                    vm.confirmedFilter = angular.copy(vm.filter);
                    localStorage.setItem($state.current.name,angular.toJson(vm.confirmedFilter));
                    vm.stopLoad = false;
                    loadData(true);
                }

                /* переход в галерею */
                function showGallery(id) {
                    $state.go("item.detail.gallery", {id: id});
                }

                /* изъятие\включение в продажу */
                function forSaleToggle(item){
                    dataResources.notForSale.toggle({itemId:item.id,notForSale:item.notForSale});
                }

                /* модальное окно фильтрации */
                function openFilter(wClass) {
                    var dialog = modal({
                        templateUrl: "pages/modal/items-filter.html",
                        className: 'ngdialog-theme-default ' + wClass,
                        closeByEscape: true,
                        closeByDocument: true,
                        scope: $scope
                    });
                    
                    dialog.closePromise.then(function (output) {
                        if (output.value && output.value != '$escape') {}
                    });
                }

                /* получение адреса шаблона */
                function getTemplate(){
                    var templatePath = "pages/fragment/items/";
                    if(mvm.width < 601){
                        return templatePath + "items-sm.html"
                    }
                    if(mvm.width > 600){
                        if(mvm.width < 961){
                            return templatePath + "items-md.html"
                        }
                        return templatePath + "items-lg.html"
                    }
                }

        }])

        /* Карточка товара */
        .controller('itemDetailController',['$rootScope','$scope','$stateParams','$state','dataResources','modal','$timeout','item','companies','$mdToast','$filter','statuses',
            function ($rootScope,$scope, $stateParams, $state, dataResources,modal,$timeout,item,companies,$mdToast,$filter,statuses){

                var mvm = $scope.$parent.mvm;
                var vm = this;

                vm.validate = validate;
                vm.showCategoryModal = showCategoryModal;
                vm.save = save;
                vm.showGallery = showGallery;
                vm.getTemplateUrl = getTemplateUrl;
                vm.afterInclude = afterInclude;

                vm.item = item ? item : {};
                vm.companyNames = companies;
                vm.statuses = statuses;
                vm.showHints = true;

                //парсим стоимость в денежный формат
                if(vm.item.price){
                    vm.item.price = $filter('number')(vm.item.price);
                }

                // если категорий нет, то инициализация пустым массивом
                if(!vm.item.categories){
                    vm.item.categories = [];
                }

                // инициализация кол-ва товара в наличие
                if(!vm.item.inStock){
                    vm.item.inStock = 0;
                }

                // инициализация кол-ва товара в наличие
                if(!vm.item.inOrder){
                    vm.item.inOrder = 0;
                }

                /* Валидация категории из товара */
                function validate(){
                    if(vm.item.categories.length == 0){
                        vm.showHints = false;
                        vm.itemCard.categories.$error.required = true;
                        vm.itemCard.categories.$setValidity("min-items", false);
                    } else {
                        vm.showHints = true;
                        vm.itemCard.categories.$error.required = true;
                        vm.itemCard.categories.$setValidity("min-items", true);
                    }
                }

                /* Открытие модального окна для выбора категории */
                function showCategoryModal(wClass){
                    var selected = [];
                    if(vm.item.categories && vm.item.categories.length > 0){
                        selected = vm.item.categories.map(function(category){
                            return category['id'];
                        })
                    }

                    var dialog = modal({
                        templateUrl: mvm.width < 601 ? "pages/fragment/modal/categoryModal.html" : "pages/modal/categoryModal.html",
                        className:'ngdialog-theme-default ' + wClass,
                        controller:"categoryClssController as vm",
                        closeByEscape: true,
                        closeByDocument: false,
                        data:selected,
                        scope: $scope
                    });

                    //получение данных при закрытие модального окна категорий
                    dialog.closePromise.then(function(output) {
                        if(output.value && output.value != '$escape'){
                            vm.item.categories = output.value;
                            validate();
                        }
                    });
                }

                /* Сохранение товара */
                function save() {
                    var toast = $mdToast.simple().position('top right').hideDelay(3000);

                    // изменение состояния breadCrumbs
                    function refreshState(data){
                        vm.itemCard.$setPristine();
                        /* refresh state because name can be changed */
                        $state.go($state.current,{id:data.result},{notify:false}).then(function(){
                            vm.item.id = data.result;
                            $stateParams.id = data.result;
                            $rootScope.$broadcast('$refreshBreadcrumbs',$state);
                        });
                    }


                    if(vm.itemCard.$dirty){
                        if(vm.itemCard.$valid){
                            if(vm.item.id){
                                // если товар был на редактирование
                                dataResources.item.put(vm.item).$promise.then(function(data){
                                    $mdToast.show(toast.textContent('Товар ['+ vm.item.name +'] успешно изменён').theme('success'));
                                    //нужно сбросить состояние, т.к может измениться имя товара
                                    refreshState({result:vm.item.id});
                                }, function(error){
                                    $mdToast.show(toast.textContent('Неудалось сохранить изменения').theme('error'));
                                })
                            } else {
                                dataResources.item.post(vm.item).$promise.then(function(data){
                                    $mdToast.show(toast.textContent('Товар ['+ vm.item.name +'] успешно создан').theme('success'));
                                    // изменилось состояние.
                                    refreshState(data);
                                }, function(error){
                                    $mdToast.show(toast.textContent('Неудалось создать новый товар').theme('error'));
                                })
                            }
                            vm.showHints = true;
                        } else {
                            vm.showHints = false;
                        }
                    } else {
                        console.log("not dirty");
                    }
                }

                /* Переход в галерею данного товара */
                function showGallery() {
                    $state.go("item.detail.gallery", {id: vm.item.id});
                }

                /* Получения шаблона страницы */
                function getTemplateUrl(){
                    var templatePath = "pages/fragment/items/card/";
                    if(mvm.width < 601){
                        return templatePath + "item-card-sm.html";
                    } else {
                        return templatePath + "item-card-lg.html";
                    }
                }

                // помечаем scope как чистый
                function afterInclude(){
                    $timeout(function(){
                        vm.itemCard.$setPristine(true);
                    },50);
                }

        }])

})();
