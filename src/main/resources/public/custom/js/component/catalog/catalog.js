(function(){
    angular.module('catalog',[]);
})();

(function() {
    'use strict';

    angular.module('catalog')

        /* Основной контроллер работы со списком товара. */
        .controller('catalogController', ['$scope', '$state','dataResources','$timeout','eventService','$stateParams','$rootScope','node', 
            function ($scope, $state, dataResources, $timeout, eventService, $stateParams, $rootScope, node) {

                var busy = false;
                var portion = 0;
                var vm = this;

                vm.loadData = loadData;
                vm.itemView = itemView;
                vm.filterBySubcategory = filterBySubcategory;
                vm.init = init;

                // используется только под администратором
                vm.searchFilter = {category:null, company:null, criteria:null, offset:0, limit:30};
                vm.items = [];
                vm.categories = [];
                vm.showSideFilter = false;
                vm.stopLoad=false;
                vm.allDataLoaded = false;
                vm.infiniteDistance = 2;

                function init(){
                    // выбранный узел бокового меню
                    if(node){
                        if($stateParams.type == 'category') {
                            // если выбранный узел относится к Категориям
                            vm.searchFilter.category = $stateParams.id;
                            // for (var i = 0; i < 15; i++) {
                            //     vm.categories.push("Категория" + i);
                            // }
                        } else {
                            // если выбранный узел относится к Производителям
                            vm.searchFilter.company = $stateParams.id;
                        }
                    }
                }

                /**
                * получение данных с сервера
                * @param isClean - нужно ли очищать существующий набор данных
                */
                function loadData(isClean){
                    // если загрузка разрешена и не заняты
                    if(!vm.stopLoad && !busy){
                        busy = true;
                        dataResources.catalog.list.all(vm.searchFilter).$promise.then(function (data) {
                            // если размер полученных данных меньше запрошенных, то запрещаем дальнейшую подгрузку
                            if(data.length < vm.searchFilter.limit){
                                vm.stopLoad = true;
                            }
                            
                            // очистим данные если требуется
                            if(isClean){
                                vm.items = [];
                            }

                            if($stateParams.type == 'category'){
                                data.forEach(function(e){
                                    e.categories.forEach(function(c){
                                        var categoryId = c.id;
                                        var result = vm.items.filter(function(e){return e.id == categoryId});
                                        if(result.length > 0){
                                            result[0].values.push(e);
                                        } else {
                                            var newElem = {id: c.id, name: c.name, values:[]};
                                            newElem.values.push(e);
                                            vm.items.push(newElem)
                                        }
                                    });
                                });
                            } else {
                                vm.items = vm.items.concat(data);
                            }


                            portion++;
                            vm.searchFilter.offset = portion * vm.searchFilter.limit;
                            //говорим что можно отображать
                            vm.allDataLoaded = true;
                            busy = false;
                        });
                    }
                }

                /**
                 * Новая загрузка после смены подкатегории
                 * @param id
                 */
                function filterBySubcategory(id){
                    vm.searchFilter.category = id;
                    loadData(true);
                }

                /**
                * Переход на карточку товара
                * @param id
                */
                function itemView(id){
                    $state.go("catalog.detail", {itemId: id});
                }

                /**
                * Слушатель события "onFilter"
                */
                $scope.$on('onFilter',function() {
                    var node = eventService.data;
                    var type;
                    // определяет тип выбранного узла
                    if(node.company){
                        vm.searchFilter.company = node.id;
                        vm.searchFilter.category = null;
                        type = 'company';
                    } else {
                        vm.searchFilter.company = null;
                        vm.searchFilter.category = node.id;
                        vm.showSideFilter = true;
                        type = 'category';
                    }
                    // переходим на страницу результата фильтрации
                    $state.go('catalog.type', {id:node.id, type:type}, {notify:true}).then(function(){
                        // неявно обновим Breadcrumbs
                        $rootScope.$broadcast('$refreshBreadcrumbs',$state);
                    });
                    // сбросим фильтр получения данных с сервера
                    portion = 0;
                    vm.searchFilter.offset = 0;
                    vm.stopLoad = false;
                    // запросим данные
                    loadData(true);
                });

                init();
        }])

        /* Контроллер работы с карточкой товара */
        .controller('catalogCardController',['$scope','$state','product',
            function ($scope, $state, product) {

                var mvm = $scope.$parent.mvm;
                var vm = this;

                vm.show = show;
                vm.showGallery = showGallery;
                vm.getTemplateUrl = getTemplateUrl;
                vm.afterInclude = afterInclude;
                vm.item = angular.extend({}, product);
                vm.mainImage = vm.item.url;

                /**
                * Установка изображения активным на просмотр
                * @param id - изображение которое нужно показать как main
                */
                function show(id){
                    var keepGoing = true;
                    var res = null;

                    // поиск выбранного в списке
                    vm.item.images.forEach(function(elem, index){
                        if (keepGoing) {
                            if(elem === id) {
                                res = index;
                                keepGoing = false;
                            }
                        }
                    });
                    vm.mainImage = (mvm.width < 601 ? mvm.PREVIEW_URL : mvm.VIEW_URL) + id;
                }

                /**
                * Переход в галерею товара
                */
                function showGallery() {
                    $state.go("product.detail.gallery", {id: vm.item.id});
                }

                /* Получения шаблона страницы */
                function getTemplateUrl(){
                    var templatePath = "pages/fragment/catalog/card/";
                    if(mvm.width < 601){
                        return templatePath + "catalog-card-sm.html";
                    } else {
                        return templatePath + "catalog-card-lg.html";
                    }
                }

                // callback загрузки шаблона страницы
                function afterInclude(){}

        }]);
})();
