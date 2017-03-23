(function(){
    angular.module('order',[]);
})();

(function() {
    'use strict';

    angular.module('order')

        /* Контроллер списочной формы */
        .controller('orderController',['$scope','$state','$stateParams','dataResources','deliveryMap','statusMap', function ($scope, $state, $stateParams, dataResources, deliveryMap,statusMap ) {
            //TODO: Написать сервис подсчета товаров при заказах
            //TODO: Определиться со статусами заказа (можно ли редактировать и когда, или же это будет работать автоматом)

            var templatePath = "pages/fragment/order/";

            $scope.orders = [];
            $scope.statuses = statusMap;
            $scope.deliveries = deliveryMap;

            $scope.filter = {subjectId:null, delivery:null, status:null, dateFrom:null, dateTo:null, limit:30, offset:0};
            var confirmedFilter = angular.copy($scope.filter);

            /* если в фильтре должен участвовать клиент */
            if($stateParams.customerId != null){
                $scope.filter.subjectId = $stateParams.customerId;
            }

            var busy = false;
            var portion = 0;

            $scope.scrolling = {stopLoad:false, allDataLoaded:false, infiniteDistance: 2};
            $scope.filterInUse = false;

            /* Получение данных */
            $scope.loadData = function(isClean){
                if(!$scope.scrolling.stopLoad && !busy){
                    busy = true;

                    dataResources.order.all(confirmedFilter).$promise.then(function(data){

                        if(data.length < confirmedFilter.limit){
                            $scope.scrolling.stopLoad = true;
                        }

                        if(isClean){
                            $scope.orders = [];
                        }

                        $scope.orders = data;

                        portion++;
                        confirmedFilter.offset = portion * confirmedFilter.limit;
                        $scope.scrolling.allDataLoaded = true;
                        busy = false;
                    });
                }
            };

            // очистка фильтра
            $scope.clear = function () {
                portion = 0;
                $scope.filterInUse = false;
                $scope.filter = {subjectId:null, delivery:null, status:null, dateFrom:null, dateTo:null, limit:30, offset:0};
                confirmedFilter = angular.copy($scope.filter);
                // удалим старый фильтр
                localStorage.removeItem($state.current.name);
                $scope.scrolling.stopLoad = false;
                $scope.loadData(true);
            };

            // подтверждение фильтра
            $scope.apply = function () {
                portion = 0;
                $scope.filterInUse = true;
                $scope.filter.offset = portion * $scope.filter.limit;
                confirmedFilter = angular.copy($scope.filter);
                // запомним фильтр
                localStorage.setItem($state.current.name, angular.toJson(confirmedFilter));
                //
                confirmedFilter.delivery = confirmedFilter.delivery!= null ? confirmedFilter.delivery.value : null;
                confirmedFilter.status = confirmedFilter.status != null ? confirmedFilter.status.id : null;
                $scope.scrolling.stopLoad = false;
                $scope.loadData(true);
            };

            /* Редактирование заказа */
            $scope.editOrder = function (id) {
                $state.transitionTo("order.detail", {id:id});
            };

            /* Удаление заказа */
            $scope.deleteOrder = function (id) {
                dataResources.order.delete({id: id});
                var currOrder = helpers.findInArrayById($scope.orders, id);
                var idx = $scope.orders.indexOf(currOrder);
                $scope.orders.splice(idx, 1);
            };

            /* Создание заказа */
            $scope.addOrder = function () {
                $state.transitionTo("order.detail");
            };

            /* Получение шаблона страницы */
            $scope.getTemplateUrl = function(){
                if($scope.width < 601){
                    return templatePath + "order-sm.html"
                }
                if($scope.width > 600){
                    if($scope.width < 1025){
                        return templatePath + "order-md.html"
                    }
                    return templatePath + "order-lg.html"
                }
            }

        }])

        /* Контроллер Карточки заказа */
        .controller('orderDetailController',['$scope','$state','$stateParams','dataResources','$timeout','$mdToast','modal','order','items','deliveryMap','statusMap',function ($scope, $state, $stateParams, dataResources,$timeout,$mdToast,modal,order,items,deliveryMap,statusMap){

            var templatePath = "pages/fragment/order/card/";

            $scope.statuses = statusMap;
            $scope.deliveries = deliveryMap;
            $scope.showHints = true;
            $scope.order = order;
            $scope.items = items.map(function(element){
                var item = element.item;
                item.count = element.cou;
                return item
            });

            /* Если создаем новый заказ, то проставим время */
            if(!$scope.order) {
                var time = new Date().getTime();
                $scope.order = {status:null,payment:0,uid:time,dateAdd:time,delivery:null};
            }

            /* Сохранения/ Обновление заказа */
            $scope.save = function () {
                var toast = $mdToast.simple().position('top right').hideDelay(3000);

                function haveEmptyItems(){
                    var result = false;
                    $scope.items.some(function(item){
                        if(item.cou==0){
                            return result = true;
                        } else {
                            return result = false;
                        }
                    });
                    return result;
                }

                function refreshState(data){
                    $scope.orderCard.$setPristine();
                    /* refresh state because name can be changed */
                    $state.go($state.current,{id:data.result},{notify:false}).then(function(){
                        $scope.selected.id = data.result;
                        $rootScope.$broadcast('$refreshBreadcrumbs',$state);
                    });
                }

                if($scope.orderCard.$dirty) {
                    if ($scope.orderCard.$valid) {
                        if($scope.items.length!=0 && !haveEmptyItems()){

                            var orderItems = [];

                            $scope.items.forEach(function (item) {
                                if(item.count>0){
                                    orderItems.push({
                                        id:null,
                                        orderId: $scope.order.id,
                                        itemId: item.id,
                                        cou: item.count
                                    })
                                }
                            });

                            dataResources.order.post({order:$scope.order, items:orderItems}).$promise.then(function(data){
                                if($scope.order.id){
                                    $mdToast.show(toast.textContent('Заказ #['+ $scope.order.uid +'] успешно изменён').theme('success'));
                                } else {
                                    $mdToast.show(toast.textContent('Заказ #['+ $scope.order.uid +'] успешно создан').theme('success'));
                                    refreshState({result:$scope.order.id});
                                }
                            }, function(error){
                                if($scope.order.id){
                                    $mdToast.show(toast.textContent('Неудалось сохранить изменения').theme('error'));
                                } else {
                                    $mdToast.show(toast.textContent('Неудалось создать новый заказ').theme('error'));
                                }
                            });
                        } else {
                            $mdToast.show(toast.textContent('В заказе отсутствует товар!').theme('error'));
                        }
                    } else {
                        $scope.showHints = false;
                    }
                }
            };

            /* Добавление товара в заказ */
            $scope.addItemsInOrder = function () {
                //определим модальное окно
                var dialog = modal({
                    templateUrl:"pages/modal/itemModal.html",
                    className:'ngdialog-theme-default custom-width',
                    closeByEscape:true,
                    controller:"itemClssController",
                    data:$scope.items
                });

                // callback закрытия модального
                dialog.closePromise.then(function(output) {

                    // фильтрация массива
                    function itemFilter(array, onlyMiss){
                        return function(item){
                            //ищем елемент по id
                            var elem = helpers.findInArrayById(array,item.id);
                            var result;

                            // ищем элементы присутствующие в наборе
                            if(helpers.isEmptyObject(elem)){
                                result = false;
                            } else {
                                result = true;
                            }
                            // инвертируем результат, если ищем отсутствующие
                            if(onlyMiss) result=!result;
                            return result;
                        }
                    }

                    if(output.value && output.value != '$escape'){
                        // сперва фильтруем исходный массив чтобы убрать все старые товары
                        $scope.items = $scope.items.filter(itemFilter(output.value,false));
                        // теперь фильтруем новые товары, оставляя только те, что добавились
                        output.value = output.value.filter(itemFilter($scope.items,true));
                        // зададим кол-во для новых товаров
                        angular.forEach(output.value, function(elem){
                            elem.count = 1;
                        });
                        // перенесём новые товары
                        $scope.items.push.apply($scope.items,output.value);
                        recalculatePayment();
                        // пометим форму как грязную
                        $scope.orderCard.$setDirty(true);
                    }
                });
            };

            // Удаление товара из заказа
            $scope.remItemsFromOrder = function (idx) {
                $scope.items.splice(idx,1);
                recalculatePayment();
            };

            // увеличение счетчика товаров в заказе
            $scope.incrementCou = function (idx) {
                var item = $scope.items[idx];
                item.count++;
                recalculatePayment();
            };

            // уменьшение счетчика товаров в заказе
            $scope.decrementCou = function (idx) {
                var item = $scope.items[idx];
                if (item.count > 0) {
                    item.count--;
                    recalculatePayment();
                }
            };

            // пересчет суммы заказа
            function recalculatePayment(){
                $scope.order.payment = 0;
                angular.forEach($scope.items,function(item){
                    $scope.order.payment = $scope.order.payment + (item.count * item.price);
                })
            }

            //TODO: need add addresses for self delivery and add new address field
            /*$scope.deliveryChanged = function(){
                var elem = $('#deliveryType .select-wrapper');
                if($scope.filter.selectedCompany == null || $scope.selected.company == null){
                    angular.element(elem).addClass('inactive');
                } else {
                    if(angular.element(elem).hasClass('inactive')){
                        angular.element(elem).removeClass('inactive');
                    }
                }
            };*/

            // получение страницы
            $scope.getTemplateUrl = function(){
                if($scope.width < 601){
                    return templatePath + "order-card-sm.html"
                }
                if($scope.width > 600){
                    if($scope.width < 1025){
                        return templatePath + "order-card-md.html"
                    }
                    return templatePath + "order-card-lg.html"
                }
            };

            // помечаем scope как чистый
            $scope.afterInclude = function(){
                $timeout(function(){
                    $scope.orderCard.$setPristine(true);
                },50);
            }

        }])

})();