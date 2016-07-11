(function(){
    angular.module('order',[]);
})();

(function() {
    'use strict';

    angular.module('order')
        .controller('orderController',['$scope','$state','$stateParams','dataResources', function ($scope, $state, $stateParams, dataResources) {
            //TODO: Написать сервис подсчета товаров при заказах
            //TODO: Определиться со статусами заказа (можно ли редактировать и когда, или же это будет работать автоматом)

            var templatePath = "pages/fragment/order/";

            /* array of orders*/
            $scope.orders = [];
            $scope.selectedPerson = {};

            if ($stateParams.customerId) {
                //$scope.orders = dataResources.orderByCustomerId.get({id: $stateParams.customerId}, function (data) {
                //    angular.forEach(data, function (order) {
                //        var person = helpers.findInArrayById(persons, order.personId);
                //        order.personName = person.name;
                //    });
                //});
            } else {
                $scope.orders = dataResources.order.query(function (data) {});
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

            $scope.getTemplateUrl = function(){
                if($scope.width < 601){
                    if($scope.width < 361) {
                        return templatePath + "order-sm.html"
                    }
                    return templatePath + "order-sm-l.html"
                }
                if($scope.width > 600){
                    if($scope.width < 1025){
                        return templatePath + "order-md.html"
                    }
                    return templatePath + "order-lg.html"
                }
            }

        }])

        .controller('orderDetailController',['$scope','$state','$stateParams','dataResources','$timeout','$mdToast','modal','order','items','deliveryMap','statusMap',function ($scope, $state, $stateParams, dataResources,$timeout,$mdToast,modal,order,items,deliveryMap,statusMap){

            var templatePath = "pages/fragment/order/card/";

            $scope.statuses = statusMap;
            $scope.deliveries = deliveryMap;

            $scope.showHints = true;

            $scope.currentOrder = order;
            $scope.currentOrder.items = [];

            if (order) {
                angular.forEach(items, function(orderItem){
                    var item = orderItem.item;
                    item.cou = orderItem.cou;
                    $scope.currentOrder.items.push(item);
                });
            } else {
                var time = new Date().getTime();
                $scope.currentOrder = {status:null,items:[],payment:0,uid:time,dateAdd:time,delivery:null};
            }

            $scope.save = function () {

                var toast = $mdToast.simple().position('top right').hideDelay(3000);

                function haveEmptyItems(){
                    var result = false;
                    $scope.currentOrder.items.some(function(item){
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
                        if($scope.currentOrder.items.length!=0 && !haveEmptyItems()){

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

                                if($scope.currentOrder.id){
                                    $mdToast.show(toast.textContent('Заказ #['+ $scope.currentOrder.uid +'] успешно изменён').theme('success'));
                                } else {
                                    $mdToast.show(toast.textContent('Заказ #['+ $scope.currentOrder.uid +'] успешно создан').theme('success'));
                                    refreshState({result:$scope.currentOrder.id});
                                }
                            }, function(error){
                                if($scope.currentOrder.id){
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

            //add item to order
            $scope.addItemsInOrder = function () {
                var dialog = modal({templateUrl:"pages/modal/itemModal.html",className:'ngdialog-theme-default custom-width',closeByEscape:true,controller:"itemClssController",data:$scope.currentOrder.items});
                dialog.closePromise.then(function(output) {
                    if(output.value && output.value != '$escape'){
                        $scope.currentOrder.items = output.value;
                        angular.forEach($scope.currentOrder.items, function (item) {
                            item.cou = 1;
                        });
                        recalculatePayment();
                        $scope.orderCard.$setDirty(true);
                    }
                });
            };

            //remove item from order
            $scope.remItemsFromOrder = function (idx) {
                $scope.currentOrder.items.splice(idx,1);
                recalculatePayment();
            };

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

            $scope.afterInclude = function(){
                $timeout(function(){
                    $scope.orderCard.$setPristine(true);
                    console.log($scope);
                },50);

            }

        }])

})();