(function(){
    angular.module('order',[]);
})();

(function() {
    'use strict';

    angular.module('order')
        .controller('orderController',['$scope','$state','$stateParams','dataResources',function ($scope, $state, $stateParams, dataResources) {
            //TODO: Написать сервис подсчета товаров при заказах
            //TODO: Определиться со статусами заказа (можно ли редактироватьб и когда, или же это будет работать автоматом)
            console.log("Enter order controller");

            /* array of orders*/
            $scope.orders = [];

            /* map of person names */
            $scope.personNames = dataResources.personMap.get();

            /*items for selection*/
            $scope.itemNames = dataResources.itemMap.get();

            $scope.selectedPerson = {};

            if ($stateParams.customerId) {
                $scope.orders = dataResources.orderByCustomerId.get({id: $stateParams.customerId}, function (data) {
                    angular.forEach(data, function (order) {
                        var person = helpers.findInArrayById($scope.personNames, order.personId);
                        order.personName = person.name;
                    });
                });
            } else {
                $scope.orders = dataResources.order.query(function (data) {
                    angular.forEach(data, function (order) {
                        var person = helpers.findInArrayById($scope.personNames, order.personId);
                        order.personName = person.name;
                    });
                });
            }

            $scope.editOrder = function (id) {
                $state.transitionTo("order.detail",{id:id});
            };

            $scope.deleteOrder = function (id) {
                dataResources.order.delete({id: id});
                var currOrder = helpers.findInArrayById($scope.orders, id);
                var idx = $scope.orders.indexOf(currOrder);
                $scope.orders.splice(idx, 1);
            };

            //create order
            $scope.addOrder = function () {
                $state.transitionTo("order.detail");
            };

        }])
        .controller('orderDetailController',['$scope','$state','order','$stateParams','dataResources','$timeout','itemClssModal','eventService',function ($scope, $state, order,$stateParams, dataResources,$timeout,itemClssModal,eventService){

            $scope.stats = [];
            $scope.deliveries = [];
            var time = new Date().getTime();
            $scope.currentOrder = {status:null,items:[],payment:0,uid:time,dateAdd:time};
            $scope.isNewOrder = true;

            if (order) {
                console.log(order);
                $scope.currentOrder = angular.copy(order);

                $scope.currentOrder.items = [];


                dataResources.orderItems.get({id: order.id}, function (data) {
                    angular.forEach(data, function (orderItem) {
                        var item = orderItem.item;
                        item.cou = orderItem.cou;
                        $scope.currentOrder.items.push(item);
                    });
                });

                $scope.isNewOrder = !$scope.isNewOrder;
            }
            $scope.currentOrder.formatedDateAdd = helpers.dateTimeFormat($scope.currentOrder.dateAdd);


            // Status
            dataResources.orderStatus.get(function(data){
                angular.forEach(data,function(stat){
                    $scope.stats.push(stat);
                });

                if (order) {
                    $scope.currentOrder.status = helpers.findInArrayByValue($scope.stats, order.status);
                } else {
                    $scope.currentOrder.status = helpers.findInArrayById($scope.stats, 0);
                    $('#status').prop( "disabled", true );
                }
            });

            dataResources.orderDelivery.get(function(data){
                angular.forEach(data,function(del){
                    $scope.deliveries.push(del);
                });

                if (order) {
                    $scope.currentOrder.delivery = helpers.findInArrayByValue($scope.deliveries, order.delivery);
                } else {
                    $scope.currentOrder.delivery = helpers.findInArrayById($scope.deliveries, 0);
                }
            });



            $scope.save = function () {
                console.log("Save button pressed");
                var cleanOrderItems = [];

                    $scope.currentOrder.items.forEach(function (item) {
                        if(item.cou>0){
                            cleanOrderItems.push({
                                id:null,
                                orderId: $scope.currentOrder.id,
                                itemId: item.id,
                                cou: item.cou
                            })
                        }
                    });

                    var correctOrder = angular.copy($scope.currentOrder);
                    correctOrder.status = correctOrder.status.value;
                    correctOrder.delivery = correctOrder.delivery.value;

                    var respData = {
                        order: correctOrder,
                        items: cleanOrderItems
                    };
                    //TODO: try handle order after save
                    var items = angular.copy($scope.currentOrder.items);
                    dataResources.order.save(respData,function(data){
                        $scope.currentOrder = data;
                        $scope.currentOrder.items = items;
                    });
            };

            //add item to order
            $scope.addItemsInOrder = function () {
                $scope.modal = itemClssModal($scope.currentOrder.items);
            };

            //remove item from order
            $scope.remItemsFromOrder = function (idx) {
                $scope.currentOrder.items.splice(idx,1);
                recalculatePayment();
            };

            $scope.$on('onItemClssSelected',function() {
                var oldItems = $scope.currentOrder.items;
                $scope.currentOrder.items = [];
                var currItem;
                angular.forEach(eventService.data, function (item) {
                    currItem = helpers.findInArrayById(oldItems,item.id);
                    if(helpers.isEmpty(currItem)){
                        currItem = item;
                        currItem.cou = 1;
                    }
                    $scope.currentOrder.items.push(currItem)
                });
                recalculatePayment();
            });

            //increment item cou in order
            $scope.incrementCou = function (idx) {
                var item = $scope.currentOrder.items[idx];
                item.cou++;
                recalculatePayment();
            };

            //decrement item cou in order
            $scope.decrementCou = function (idx) {
                var item = $scope.currentOrder.items[idx];
                if (item.cou > 0) {
                    item.cou--;
                    recalculatePayment();
                }
            };

            function recalculatePayment(){
                $scope.currentOrder.payment = 0;
                angular.forEach($scope.currentOrder.items,function(item){
                    $scope.currentOrder.payment = $scope.currentOrder.payment + (item.cou * item.price);
                })
            };

            $scope.haveEmptyItems = function(){
                var result = false;
                $scope.currentOrder.items.some(function(item){
                    if(item.cou==0){
                        return result = true;
                    } else {
                        return result = false;
                    }
                });
                return result;
            };

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

            console.log($scope);

        }])

})();