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
                // используется только под администратором
                vm.searchFilter = {category:null, company:null, criteria:null, offset:0, limit:30};
                // список товаров
                vm.items = [];
                // набор фильтров категорий товара из бокового меню. Данные используются для ElasticSearch. ВРЕМЕННО УБЕРЕМ
                // $scope.sideFilters = [];

                // выбранный узел бокового меню
                if(node){
                    if($stateParams.type == 'category') {
                        // если выбранный узел относится к Категориям
                        vm.searchFilter.category = $stateParams.id
                    } else {
                        // если выбранный узел относится к Производителям
                        vm.searchFilter.company = $stateParams.id
                    }
                }

                vm.showSideFilter = false;
                vm.stopLoad=false;
                vm.allDataLoaded = false;
                vm.infiniteDistance = 2;


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

                            vm.items = vm.items.concat(data);

                            portion++;
                            vm.searchFilter.offset = portion * vm.searchFilter.limit;
                            //говорим что можно отображать
                            vm.allDataLoaded = true;
                            busy = false;
                        });
                    }
                }

            /* ПОКА НЕ ПОНЯТНО ДЛЯ ЧЕГО ЭТО БЫЛО ОСТАВЛЕНО */

            // var getRootNodesScope = function() {
            //     return angular.element(document.getElementById("tree-root")).scope();
            // };
            //
            // $scope.collapseAll = function() {
            //     var scope = getRootNodesScope();
            //     scope.collapseAll();
            // };
            //
            // $scope.expandAll = function() {
            //     var scope = getRootNodesScope();
            //     scope.expandAll();
            // };

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
                    $state.go('catalog.type', {id:node.id, type:type}, {notify:false}).then(function(){
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

            /* ВОЗМОЖНО ПРЕДПОЛАГАЛОСЬ ДЛЯ ИСКЛЮЧЕНИЯ ДЕТЕЙ ВЫБРАННЫХ ЭЛЕМЕНТОВ ФИЛЬТРА КАТЕГОРИЙ */
            // $scope.removeFilterElem = function(idx){
            //     $scope.sideFilters.splice(idx,1);
            //     //TODO: maybe change to server clean
            //
            //     if($scope.sideFilters.length==0){
            //         $scope.showSideFilter = false;
            //     }
            // }


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
                    vm.mainImage = mvm.PREVIEW_URL + id;
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

                console.log(vm.item)
                console.log(mvm)

        }]);
})();
