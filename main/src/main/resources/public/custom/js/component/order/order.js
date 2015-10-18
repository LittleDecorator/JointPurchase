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

            /* map of item names */
            $scope.itemNames = [];

            $scope.stats = [];

            // Status
             dataResources.orderStatus.get(function(data){
                 console.log(data);
                 angular.forEach(data,function(stat){
                     $scope.stats.push(stat);
                 });

                 if (order) {
                     $scope.currentOrder = order;
                         //$scope.selectedPerson = helpers.findInArrayById($scope.personNames, $scope.currentOrder.personId);

                         /*dataResources.orderItems.get({id: order.id}, function (data) {
                          angular.forEach(data, function (orderItem) {
                          var item = helpers.findInArrayById($scope.itemNames, orderItem.itemId);
                          orderItem.name = item.name;
                          $scope.currentOrderItems.push(orderItem);
                          });
                          });*/
                 } else {
                     $scope.currentOrder = {status:null,items:[]};
                     $scope.currentOrder.status = helpers.findInArrayById($scope.stats, 0);
                     $('#status').prop( "disabled", true );

                 }

            });

            $scope.save = function () {
                //iterate over order items array, and create new clean array foe mapping
                var cleanOrderItems = [];
                /*$scope.currentOrderItems.forEach(function (item) {
                    cleanOrderItems.push({
                        id: item.id,
                        orderId: item.orderId,
                        itemId: item.itemId,
                        cou: item.cou
                    })
                });*/

                //prepare response object
                $scope.currentOrder.personId = $scope.selectedPerson.id;
                var respData = {
                    order: $scope.currentOrder,
                    items: cleanOrderItems
                };
                dataResources.order.save(respData);
            };

            //add item to order
            $scope.addItemsInOrder = function () {
                $scope.modal = itemClssModal($scope.currentOrder.items);
            };

            //remove item from order
            $scope.remItemsFromOrder = function (idx) {
                $scope.currentOrder.items.splice(idx,1);
            };

            $scope.$on('onItemClssSelected',function() {
                $scope.currentOrder.items = [];
                angular.forEach(eventService.data, function (item) {
                    item.cou = 0;
                    $scope.currentOrder.items.push(item);
                });
            });

            //increment item cou in order
            $scope.incrementCou = function (idx) {
                $scope.currentOrder.items[idx].cou++;
            };

            //decrement item cou in order
            $scope.decrementCou = function (idx) {
                var item = $scope.currentOrder.items[idx];
                if (item.cou > 0) {
                    item.cou--;
                }
            };

            //$scope.createOrder = function(cart){
            //    dataResources.order.save({items:cart});
            //};

        }])
})();