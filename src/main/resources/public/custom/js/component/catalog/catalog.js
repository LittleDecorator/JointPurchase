(function(){
    angular.module('catalog',[]);
})();

(function() {
    'use strict';

    angular.module('catalog')

        /* Основной контроллер работы со списком товара. */
        .controller('catalogController', ['$scope', '$state','dataResources','$timeout','eventService','$stateParams','$rootScope','node', function ($scope, $state, dataResources, $timeout, eventService, $stateParams, $rootScope, node) {

            // используется только под администратором
            $scope.searchFilter = {category:null, company:null, criteria:null, offset:0, limit:30};
            // список товаров
            $scope.items = [];
            // набор фильтров категорий товара из бокового меню. Данные используются для ElasticSearch. ВРЕМЕННО УБЕРЕМ
            // $scope.sideFilters = [];

            // выбранный узел бокового меню
            if(node){
                if($stateParams.type == 'category') {
                    // если выбранный узел относится к Категориям
                    $scope.searchFilter.category = $stateParams.id
                } else {
                    // если выбранный узел относится к Производителям
                    $scope.searchFilter.company = $stateParams.id
                }
            }

            var busy = false;
            var portion = 0;
            $scope.showSideFilter = false;
            $scope.stopLoad=false;
            $scope.allDataLoaded = false;
            $scope.infiniteDistance = 2;


            /**
             * получение данных с сервера
             * @param isClean - нужно ли очищать существующий набор данных
             */
            $scope.loadData = function(isClean){
                // если загрузка разрешена и не заняты
                if(!$scope.stopLoad && !busy){
                    busy = true;
                    dataResources.catalog.list.all($scope.searchFilter).$promise.then(function (data) {
                        // если размер полученных данных меньше запрошенных, то запрещаем дальнейшую подгрузку
                        if(data.length < $scope.searchFilter.limit){
                            $scope.stopLoad = true;
                        }
                        // очистим данные если требуется
                        if(isClean){
                            $scope.items = [];
                        }

                        $scope.items = data;

                        portion++;
                        $scope.searchFilter.offset = portion * $scope.searchFilter.limit;
                        //говорим что можно отображать
                        $scope.allDataLoaded = true;
                        busy = false;
                    });
                }
            };

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
            $scope.itemView = function(id){
                $state.go("catalog.detail", {itemId: id});
            };

            /**
             * Слушатель события "onFilter"
             */
            $scope.$on('onFilter',function() {
                var node = eventService.data;
                var type;
                // определяет тип выбранного узла
                if(node.company){
                    $scope.searchFilter.company = node.id;
                    $scope.searchFilter.category = null;
                    type = 'company';
                } else {
                    $scope.searchFilter.company = null;
                    $scope.searchFilter.category = node.id;
                    $scope.showSideFilter = true;
                    type = 'category';
                }
                // переходим на страницу результата фильтрации
                $state.go('catalog.type', {id:node.id, type:type}, {notify:false}).then(function(){
                    // неявно обновим Breadcrumbs
                    $rootScope.$broadcast('$refreshBreadcrumbs',$state);
                });
                // сбросим фильтр получения данных с сервера
                portion = 0;
                $scope.searchFilter.offset = 0;
                $scope.stopLoad = false;
                // запросим данные
                $scope.loadData(true);
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
        .controller('catalogCardController',['$scope','$state','product',function ($scope, $state, product) {

            $scope.item = angular.extend({}, product);
            $scope.mainImage = $scope.item.url;

            /**
             * Установка изображения активным на просмотр
             * @param id - изображение которое нужно показать как main
             */
            $scope.show = function(id){
                var keepGoing = true;
                var res = null;

                // поиск выбранного в списке
                $scope.item.images.forEach(function(elem, index){
                    if (keepGoing) {
                        if(elem === id) {
                            res = index;
                            keepGoing = false;
                        }
                    }
                });
                $scope.mainImage = $scope.PREVIEW_URL + id;
            };

            /**
             * Переход в галерею товара
             */
            $scope.showGallery = function () {
                $state.go("product.detail.gallery", {id: $scope.item.id});
            };

            /* Получения шаблона страницы */
            $scope.getTemplateUrl = function(){
                var templatePath = "pages/fragment/catalog/card/";
                if($scope.width < 601){
                    return templatePath + "catalog-card-sm.html";
                } else {
                    return templatePath + "catalog-card-lg.html";
                }
            };

        }]);
})();
