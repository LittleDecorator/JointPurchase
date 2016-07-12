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
                dataResources.order.all().$promise.then(function (data) {
                    $scope.orders = data
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

            if(!$scope.order) {
                var time = new Date().getTime();
                $scope.order = {status:null,payment:0,uid:time,dateAdd:time,delivery:null};
            }

            $scope.save = function () {

                console.log($scope);

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

            //add item to order
            $scope.addItemsInOrder = function () {
                var dialog = modal({templateUrl:"pages/modal/itemModal.html",className:'ngdialog-theme-default custom-width',closeByEscape:true,controller:"itemClssController",data:$scope.items});
                dialog.closePromise.then(function(output) {

                    // филтрация массива
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

            //remove item from order
            $scope.remItemsFromOrder = function (idx) {
                $scope.items.splice(idx,1);
                recalculatePayment();
            };

            //increment item cou in order
            $scope.incrementCou = function (idx) {
                var item = $scope.items[idx];
                item.count++;
                recalculatePayment();
            };

            //decrement item cou in order
            $scope.decrementCou = function (idx) {
                var item = $scope.items[idx];
                if (item.count > 0) {
                    item.count--;
                    recalculatePayment();
                }
            };

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