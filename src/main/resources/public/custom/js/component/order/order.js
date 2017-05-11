(function(){
    angular.module('order',[]);
})();

(function() {
    'use strict';

    angular.module('order')

        /* Контроллер списочной формы */
        .controller('orderController',['$scope','$state','$stateParams','dataResources','deliveryMap','statusMap','modal', 
            function ($scope, $state, $stateParams, dataResources, deliveryMap, statusMap, modal ) {
            //TODO: Написать сервис подсчета товаров при заказах
            //TODO: Определиться со статусами заказа (можно ли редактировать и когда, или же это будет работать автоматом)

                var templatePath = "pages/fragment/order/";
                var confirmedFilter = {};
                var busy = false;
                var portion = 0;
                var mvm = $scope.$parent.mvm;
                var vm = this;
                
                vm.init = init;
                vm.loadData = loadData;
                vm.clear = clear;
                vm.apply = apply;
                vm.applyKeyPress = applyKeyPress;
                vm.editOrder = editOrder;
                vm.deleteOrder = deleteOrder;
                vm.addOrder = addOrder;
                vm.openFilter = openFilter;
                vm.getTemplateUrl = getTemplateUrl;

                vm.orders = [];
                vm.statuses = statusMap;
                vm.deliveries = deliveryMap;
                vm.filter = { subjectId:null, delivery:null, status:null, dateFrom:null, dateTo:null, limit:30, offset:0 };
                vm.scrolling = { stopLoad:false, allDataLoaded:false, infiniteDistance: 2 };
                vm.filterInUse = false;

                /**
                 * Инициализация
                 */
                function init(){
                    /* если в фильтре должен участвовать клиент */
                    if($stateParams.customerId != null){
                        vm.filter.subjectId = $stateParams.customerId;
                        dataResources.customer.get({id:$stateParams.customerId}).$promise.then(function(data){
                            vm.filter.subject = data.fullName;
                        })
                    }
                    confirmedFilter = angular.copy(vm.filter);
                }

                function applyKeyPress(event) {
                    if (event.keyCode == 13) {
                        apply();
                    }
                }
                
                /**
                 * Получение данных
                 * @param isClean
                 */
                function loadData(isClean){
                    if(!vm.scrolling.stopLoad && !busy){
                        busy = true;

                        dataResources.order.all(confirmedFilter).$promise.then(function(data){

                            if(data.length < confirmedFilter.limit){
                                vm.scrolling.stopLoad = true;
                            }

                            if(isClean){
                                vm.orders = [];
                            }

                            vm.orders = vm.orders.concat(data);

                            portion++;
                            confirmedFilter.offset = portion * confirmedFilter.limit;
                            vm.scrolling.allDataLoaded = true;
                            busy = false;
                        });
                    }
                }
                
                /**
                 * очистка фильтра 
                 */
                function clear(){
                    portion = 0;
                    vm.filterInUse = false;
                    vm.filter = { subjectId:null, delivery:null, status:null, dateFrom:null, dateTo:null, limit:30, offset:0 };
                    /* если в фильтре должен участвовать клиент */
                    if($stateParams.customerId != null){
                        vm.filter.subjectId = $stateParams.customerId;
                    }
                    confirmedFilter = angular.copy(vm.filter);
                    // удалим старый фильтр
                    localStorage.removeItem($state.current.name);
                    vm.scrolling.stopLoad = false;
                    loadData(true);
                }
                
                /**
                 * подтверждение фильтра 
                 */
                function apply(){
                    portion = 0;
                    vm.filterInUse = true;
                    vm.filter.offset = portion * vm.filter.limit;
                    confirmedFilter = angular.copy(vm.filter);
                    // запомним фильтр
                    localStorage.setItem($state.current.name, angular.toJson(confirmedFilter));
                    //
                    confirmedFilter.delivery = confirmedFilter.delivery!= null ? confirmedFilter.delivery.value : null;
                    confirmedFilter.status = confirmedFilter.status != null ? confirmedFilter.status.id : null;
                    vm.scrolling.stopLoad = false;
                    loadData(true);
                }
                
                /**
                 * Редактирование заказа
                 * @param id
                 */
                function editOrder(id) {
                    $state.transitionTo("order.detail", {id:id});
                }
                
                /**
                 * Удаление заказа
                 * @param id
                 */
                function deleteOrder(id){
                    dataResources.order.delete({id: id});
                    var currOrder = helpers.findInArrayById(vm.orders, id);
                    var idx = vm.orders.indexOf(currOrder);
                    vm.orders.splice(idx, 1);
                }
                
                /**
                 * Создание заказа 
                 */
                function addOrder() {
                    $state.transitionTo("order.detail");
                }
                
                /**
                 * модальное окно фильтрации 
                 * @param wClass
                 */
                function openFilter(wClass){
                    var dialog = modal({
                        templateUrl: "pages/modal/orders-filter.html",
                        className: 'ngdialog-theme-default ' + wClass,
                        closeByEscape: true,
                        closeByDocument: true,
                        scope: $scope
                    });
                    
                    dialog.closePromise.then(function (output) {
                        if (output.value && output.value != '$escape') {
                        }
                    });
                }
                
                /**
                 * Получение шаблона страницы
                 * @returns {string}
                 */
                function getTemplateUrl(){
                    if(mvm.width < 601){
                        return templatePath + "order-sm.html"
                    }
                    if(mvm.width > 600){
                        if(mvm.width < 1025){
                            return templatePath + "order-md.html"
                        }
                        return templatePath + "order-lg.html"
                    }
                }

                init();

        }])

        /* Контроллер Карточки заказа */
        .controller('orderDetailController',['$scope','$rootScope','$state','$stateParams','dataResources','$timeout','$mdToast','modal','order','items','deliveryMap','statusMap',
            function ($scope, $rootScope, $state, $stateParams, dataResources, $timeout, $mdToast, modal, order, items, deliveryMap, statusMap){

                var templatePath = "pages/fragment/order/card/";
                var mvm = $scope.$parent.mvm;
                var vm = this;
                
                vm.init = init;
                vm.save = save;
                vm.addItemsInOrder = addItemsInOrder;
                vm.remItemsFromOrder = remItemsFromOrder;
                vm.incrementCou = incrementCou;
                vm.decrementCou = decrementCou;
                vm.recalculatePayment = recalculatePayment;
                vm.getTemplateUrl = getTemplateUrl;
                vm.afterInclude = afterInclude;
                
                vm.forms = {};
                vm.items = [];
                vm.statuses = statusMap;
                vm.deliveries = deliveryMap;
                vm.showHints = true;
                vm.order = order ? order: {};

                /**
                 * Инициализация
                 */
                function init(){
                    // перебираем товар заказа если открыт на редактирование
                    if(items != null ){
                        vm.items = items.map(function(element){
                            var item = element.item;
                            item.count = element.count;
                            return item
                        });
                    }

                    /* Если создаем новый заказ, то проставим время */
                    if(!vm.order) {
                        var time = new Date().getTime();
                        vm.order = { status:null, payment:0, uid:time, dateAdd:time, delivery:null};
                    }
                }


                /**
                 * Сохранения/ Обновление заказа
                 */
                function save() {
                    var toast = $mdToast.simple().position('top right').hideDelay(3000);

                    // проверим что есть товары с кол-вом = 0
                    function haveEmptyItems(){
                        var result = false;
                        vm.items.some(function(item){
                            if(item.cou == 0){
                                return result = true;
                            } else {
                                return result = false;
                            }
                        });
                        return result;
                    }

                    // обновление breadCrumbs текущей страницы
                    function refreshState(data){
                        vm.forms.orderCard.$setPristine();
                        /* refresh state because name can be changed */
                        $state.go($state.current, {id:data.result}, {notify:false}).then(function(){
                            vm.order.id = data.result;
                            $stateParams.id = data.result;
                            $rootScope.$broadcast('$refreshBreadcrumbs',$state);
                        });
                    }

                    // точка входа в сохранение заказа
                    if(vm.forms.orderCard.$dirty) {
                        if (vm.forms.orderCard.$valid) {
                            if(vm.items.length!=0 && !haveEmptyItems()){
                                var orderItems = [];
                                vm.showHints = true;
                                
                                // формируем список товара
                                vm.items.forEach(function (item) {
                                    if(item.count>0){
                                        orderItems.push({
                                            item: {id: item.id},
                                            count: item.count
                                        })
                                    }
                                });

                                // оправляем запрос
                                dataResources.order.post({ order:vm.order, items:orderItems}).$promise.then(function(data){
                                    if(vm.order.id){
                                        $mdToast.show(toast.textContent('Заказ #['+ vm.order.uid +'] успешно изменён').theme('success'));
                                    } else {
                                        $mdToast.show(toast.textContent('Заказ #['+ vm.order.uid +'] успешно создан').theme('success'));
                                        refreshState({result:data.id});
                                    }
                                }, function(error){
                                    if($scope.order.id){
                                        $mdToast.show(toast.textContent('Неудалось сохранить изменения').theme('error'));
                                    } else {
                                        $mdToast.show(toast.textContent('Неудалось создать новый заказ').theme('error'));
                                    }
                                });
                            }
                        } else {
                            vm.showHints = false;
                        }
                    }
                }

                /**
                 * Добавление товара в заказ
                 * @param wClass
                 */
                function addItemsInOrder(wClass) {
                    //определим модальное окно
                    var dialog = modal({
                        templateUrl: mvm.width < 601 ? "pages/fragment/modal/itemModal.html" : "pages/modal/itemModal.html",
                        className: 'ngdialog-theme-default ' + wClass,
                        closeByEscape: true,
                        controller: "itemClssController as vm",
                        data: vm.items
                    });

                    // Слушатель закрытия модального
                    dialog.closePromise.then(function(output) {

                        // фильтрация массива
                        function itemFilter(array, onlyMiss){
                            return function(item){
                                //ищем елемент по id
                                var elem = helpers.findInArrayById(array,item.id);
                                var result;

                                // ищем элементы присутствующие в наборе
                                result = !helpers.isEmptyObject(elem);
                                // инвертируем результат, если ищем отсутствующие
                                if(onlyMiss) result=!result;
                                return result;
                            }
                        }

                        // если при закрытие были переданны данные
                        if(output.value && output.value != '$escape'){
                            // сперва фильтруем исходный массив чтобы убрать все старые товары
                            vm.items = vm.items.filter(itemFilter(output.value, false));
                            // теперь фильтруем новые товары, оставляя только те, что добавились
                            output.value = output.value.filter(itemFilter(vm.items, true));
                            // зададим кол-во для новых товаров
                            angular.forEach(output.value, function(elem){
                                elem.count = 1;
                            });
                            // перенесём новые товары
                            vm.items.push.apply(vm.items, output.value);
                            recalculatePayment();
                            // пометим форму как грязную
                            vm.forms.orderCard.$setDirty(true);
                        }
                    });
                }

                /**
                 * Удаление товара из заказа
                 * @param idx
                 */
                function remItemsFromOrder(idx) {
                    vm.items.splice(idx,1);
                    recalculatePayment();
                }

                /**
                 * увеличение счетчика товаров в заказе
                 * @param idx
                 */
                function incrementCou(idx) {
                    var item = vm.items[idx];
                    item.count++;
                    recalculatePayment();
                }

                /**
                 * уменьшение счетчика товаров в заказе
                 * @param idx
                 */
                function decrementCou(idx) {
                    var item = vm.items[idx];
                    if (item.count > 0) {
                        item.count--;
                        recalculatePayment();
                    }
                }

                /**
                 * пересчет суммы заказа
                 */
                function recalculatePayment(){
                    vm.order.payment = 0;
                    angular.forEach(vm.items,function(item){
                        vm.order.payment = vm.order.payment + (item.count * item.price);
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

                /**
                 * получение страницы
                 * @returns {string}
                 */
                function getTemplateUrl(){
                    if(mvm.width < 601){
                        return templatePath + "order-card-sm.html"
                    }
                    if(mvm.width > 600){
                        if(mvm.width < 1025){
                            return templatePath + "order-card-md.html"
                        }
                        return templatePath + "order-card-lg.html"
                    }
                }

                /**
                 * помечаем scope как чистый
                 */
                function afterInclude(){
                    $timeout(function(){
                        vm.forms.orderCard.$setPristine(true);
                    },50);
                }

                init();
            
            }])

})();