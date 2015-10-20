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

            var canSaveOrder;

            // Status
             dataResources.orderStatus.get(function(data){
                 angular.forEach(data,function(stat){
                     $scope.stats.push(stat);
                 });

                 if (order) {
                     $scope.currentOrder = order;
                     $scope.currentOrder.items = [];
                     $scope.currentOrder.formatedDateAdd = helpers.dateTimeFormat($scope.currentOrder.dateAdd);

                     $scope.currentOrder.status = helpers.findInArrayByValue($scope.stats, $scope.currentOrder.status);

                     dataResources.orderItems.get({id: order.id}, function (data) {
                         angular.forEach(data, function (orderItem) {
                             var item = orderItem.item;
                             item.cou = orderItem.cou;
                             $scope.currentOrder.items.push(item);
                         });
                     });
                 } else {
                     $scope.currentOrder = {status:null,items:[],payment:0};
                     $scope.currentOrder.status = helpers.findInArrayById($scope.stats, 0);
                     $('#status').prop( "disabled", true );

                 }
            });

            $scope.save = function () {
                var cleanOrderItems = [];
                if(canSaveOrder){
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

                }
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
                    currItem = helpers.findInArrayById(oldItems,item.id)
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
                if(haveNonEmptyItems()){
                    toggleSaveBtnOn();
                }
            };

            //decrement item cou in order
            $scope.decrementCou = function (idx) {
                var item = $scope.currentOrder.items[idx];
                if (item.cou > 0) {
                    item.cou--;
                    recalculatePayment();
                    if(!haveNonEmptyItems()){
                        toggleSaveBtnOff();
                    }
                }
            };

            function recalculatePayment(){
                $scope.currentOrder.payment = 0;
                angular.forEach($scope.currentOrder.items,function(item){
                    $scope.currentOrder.payment = $scope.currentOrder.payment + (item.cou * item.price);
                })
            };

            function haveNonEmptyItems(){
                var result = false;
                $scope.currentOrder.items.some(function(item){
                    if(item.cou>0){
                        return result = true;
                    } else {
                        return result = false;
                    }
                });
                return result;
            }

            function toggleSaveBtnOn(){
                var src = $('#save a');
                if(src.hasClass('disabled')){
                    src.removeClass('disabled');
                    canSaveOrder = true;
                }
            }

            function toggleSaveBtnOff(){
                var src = $('#save a');
                if(!src.hasClass('disabled')){
                    src.addClass('disabled');
                    canSaveOrder = false;
                }
            }

            $scope.$watchCollection('currentOrder.items', function(newNames, oldNames) {
                if(newNames && (newNames.length==0 || (newNames.length>0 && !haveNonEmptyItems()))){
                    toggleSaveBtnOff();
                } else {
                    toggleSaveBtnOn();
                }
            });

        }])
})();