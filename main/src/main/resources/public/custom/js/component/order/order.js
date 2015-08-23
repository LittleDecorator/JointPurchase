(function(){
    angular.module('order',[]);
})();

(function() {
    'use strict';

    angular.module('order')
        .controller('orderController',['$scope','$state','order','$stateParams','dataResources',function ($scope, $state, order,$stateParams, dataResources) {
            //TODO: Написать сервис подсчета товаров при заказах
            //TODO: Определиться со статусами заказа (можно ли редактироватьб и когда, или же это будет работать автоматом)
            console.log("Enter order controller");

            /* array of orders*/
            $scope.orders = [];
            /* current order */
            $scope.currentOrder = {};
            /* items of current order */
            $scope.currentOrderItems = [];
            /* map of item names */
            $scope.itemNames = [];
            /* inner private object */
            $scope.index = -1;

            /* map of person names */
            $scope.personNames = dataResources.personMap.get();

            /*items for selection*/
            $scope.itemNames = dataResources.itemMap.get();

            /* flag hide/show modal footer */
            $scope.hideModalFooter = false;

            $scope.selectedPerson = {};

            if (order) {
                $scope.currentOrder = order;
                $scope.selectedPerson = helpers.findInArrayById($scope.personNames, $scope.currentOrder.personId);

                dataResources.orderItems.get({id: order.id}, function (data) {
                    angular.forEach(data, function (orderItem) {
                        var item = helpers.findInArrayById($scope.itemNames, orderItem.itemId);
                        orderItem.name = item.name;
                        $scope.currentOrderItems.push(orderItem);
                    });
                });
            } else if ($stateParams.customerId) {
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

            $scope.toggleItems = function () {
                $scope.showAllItem = !$scope.showAllItem;
                $scope.hideModalFooter = true;
            };

            $scope.editOrder = function (id) {
                $state.transitionTo("order.detail",{id:id});
            };

            $scope.deleteOrder = function (id) {
                dataResources.order.delete({id: id});

                //find item in array
                var currOrder = helpers.findInArrayById($scope.orders, id);

                //find item index in array
                var idx = $scope.orders.indexOf(currOrder);
                //remove item from array
                $scope.orders.splice(idx, 1);
            };

            //create order
            $scope.addOrder = function () {
                $state.transitionTo("order.detail");
            };

            $scope.save = function () {
                //iterate over order items array, and create new clean array foe mapping
                var cleanOrderItems = [];
                $scope.currentOrderItems.forEach(function (item) {
                    cleanOrderItems.push({
                        id: item.id,
                        orderId: item.orderId,
                        itemId: item.itemId,
                        cou: item.cou
                    })
                });

                //prepare response object
                $scope.currentOrder.personId = $scope.selectedPerson.id;
                var respData = {
                    order: $scope.currentOrder,
                    items: cleanOrderItems
                };

                dataResources.order.save(respData);
            };

            $scope.createOrder = function(cart){
                dataResources.order.save({items:cart});
            };

            //add item to order
            $scope.addItemsInOrder = function (id) {
                //TODO: Проверять что товар уже в заказе, и либо ничего не делать, либо увеличивать позицию на 1, либо убирать дублируемый товар из списка возможного
                //TODO: Нужна множественная выборка?????
                var selectedItem = helpers.findInArrayById($scope.itemNames, id);
                $scope.currentOrderItems.push({
                    orderId: $scope.currentOrder.id,
                    itemId: id,
                    name: selectedItem.name,
                    cou: 1
                });
                $scope.toggleItems();
            };

            //remove item from order
            $scope.remItemsFromOrder = function (item) {
                var idx = $scope.currentOrderItems.indexOf(item);
                if (idx > -1) {
                    dataResources.orderedItem.delete({orderId: $scope.currentOrder.id, itemId: item.id});
                    $scope.currentOrderItems.splice(idx, 1);
                }
            };

            //increment item cou in order
            $scope.incrementCou = function (id) {
                angular.forEach($scope.currentOrderItems, function (item) {
                    if (item.id == id) {
                        item.cou++;
                        return true;
                    } else {
                        return false;
                    }
                })
            };

            //decrement item cou in order
            $scope.decrementCou = function (id) {
                angular.forEach($scope.currentOrderItems, function (item) {
                    if (item.id == id) {
                        if (item.cou > 0) {
                            item.cou--;
                        }
                        return true;
                    } else {
                        return false;
                    }
                })
            }

        }])
})();