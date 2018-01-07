(function(){
        angular.module('item',[]);
})();

(function(){
    'use strict';

    angular.module('item')
        
        /* Контроллер товара */
        .controller('itemController',['$scope','$state','dataResources','$timeout','companies', 'modal', '$mdUtil', 'ngDialog', 'FileUploader','$mdToast',
            function ($scope, $state, dataResources,$timeout, companies, modal, $mdUtil, ngDialog, FileUploader, $mdToast) {

                var toast = $mdToast.simple().position('top right').hideDelay(3000);
                var busy = false;
                var portion = 0;
                var filterDialog = null;
                var mvm = $scope.$parent.mvm;
                var vm = this;
                var uploader = $scope.uploader = new FileUploader();

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
                vm.scrollTop = scrollTop;
                vm.applyKeyPress = applyKeyPress;
                vm.exportXls = exportXls;
                vm.importXls = importXls;
                vm.afterInclude = afterInclude;

                vm.items = [];
                vm.companyNames = companies;
                vm.filter = {name:null, article:null, company:null, limit:30, offset:0};
                vm.confirmedFilter = angular.copy(vm.filter);
                vm.stopLoad=false;
                vm.detailLock=false;
                vm.allDataLoaded = false;
                vm.infiniteDistance = 1;

                /* получение данных с сервера */
                function loadData(isClean){
                    if(!vm.stopLoad && !vm.detailLock && !busy){
                        busy = true;
                        // mvm.showLoader = true;
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
                          // mvm.showLoader = false;
                        });
                    }
                }

                /* переход в карточку для создания нового товара */
                function addItem () {
                    vm.detailLock=true;
                    $state.go("item.detail");
                }

                /* переход в карточку для редактирования */
                function editItem (id) {
                    vm.detailLock=true;
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
                    vm.filter = {name:null, article:null, company:null, limit:30, offset:0};
                    vm.confirmedFilter = angular.copy(vm.filter);
                    localStorage.removeItem($state.current.name);
                    vm.stopLoad = false;
                    loadData(true);
                }

                function applyKeyPress(event) {
                    if (event.keyCode == 13) {
                        if(filterDialog !=null && ngDialog.isOpen(filterDialog.id)){
                            filterDialog.close();
                            event.preventDefault();
                        }
                        apply();
                    }
                }

                /* применение фильтра */
                function apply() {
                    portion = 0;
                    vm.filter.offset = portion * vm.filter.limit;
                    vm.confirmedFilter = angular.copy(vm.filter);
                    /* берем только id, т.к spring с jpa сможет найти Entity сам. Но как? */
                    if(vm.confirmedFilter.company){
                        vm.confirmedFilter.company = vm.confirmedFilter.company.id;
                    }
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

                /* выгрузка товаров в excel */
                function exportXls(){
                    var params = angular.extend({fileName: 'каталог.xls'},vm.confirmedFilter);
                    dataResources.report.items.get(params).$promise.then(function (data) {
                        saveAs(data.response, params.fileName);
                    }, function(error){
                        $mdToast.show(toast.textContent('Неудалось выгрузить список\n'+ error.message).theme('error'));
                    });
                }
                
                /* загрузка товаров из excel */
                function importXls(){
                    $('#uploadBtn').click();
                }

                /**
                 * callback on file select
                 */
                uploader.onAfterAddingAll = function () {
                    upload();
                };

                /**
                 * загрузка файлов
                 */
                function upload() {
                    var items = uploader.getNotUploadedItems();
                    var formData = new FormData();

                    angular.forEach(items, function (item, idx) {
                        formData.append("file" + idx, item._file);
                    });
                    
                    dataResources.report.items.upload(formData, function(data){
                        angular.forEach(data, function (element) {
                            uploader.clearQueue();
                        });
                        $mdToast.show(toast.textContent('Список товара успешно импортирован').theme('success'));
                    }, function(error){
                        $mdToast.show(toast.textContent('Неудалось импортировать список\n'+ error.message).theme('error'));
                    });
                }

                /* модальное окно фильтрации */
                function openFilter(wClass) {
                    filterDialog = modal({
                        templateUrl: "pages/modal/items-filter.html",
                        className: 'ngdialog-theme-default ' + wClass,
                        closeByEscape: true,
                        closeByDocument: true,
                        scope: $scope
                    });

                    filterDialog.closePromise.then(function (output) {
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
                
                function scrollTop(){
                    var j = document.querySelector(".main"), P = j.querySelector(".item_result");
                    $mdUtil.animateScrollTo(P, 0, 200)
                }

                // помечаем scope как чистый
                function afterInclude(){
                }

                /**
                * Слушатель нажатия кнопки НАЗАД
                */
                $scope.$on('locBack', function () {
                    // mvm.showDetail = false;
                    vm.detailLock = false;
                });

                // выключаем view карточки
                // mvm.showDetail = false;
        }])

        /* Карточка товара */
        .controller('itemDetailController',['$rootScope','$scope','$stateParams','$state','dataResources','modal','$timeout','item','companies','$mdToast','$filter','statuses','transliteratorService',
            function ($rootScope,$scope, $stateParams, $state, dataResources, modal, $timeout, item, companies, $mdToast, $filter, statuses, transliteratorService){

                var mvm = $scope.$parent.mvm;
                mvm.showDetail = true;
                var vm = this;
               console.log(item)
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

                    function addTranslite(item){
                        if(!item.transliteName){
                          item.transliteName = transliteratorService.urlRusLat(item.name)
                        }
                        return item;
                    }

                    if(vm.itemCard.$dirty){
                        if(vm.itemCard.$valid){
                            vm.item = addTranslite(vm.item);
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

                // $scope.$on('locBack', function () {
                //     mvm.showDetail = true;
                // });
        }])

})();
