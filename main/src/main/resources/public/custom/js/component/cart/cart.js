(function(){
    angular.module('cart',[]);
})();

(function() {
    'use strict';

    angular.module('cart')
        .factory('cartResources',['$resource',function($resource){
            return {
                _order: $resource('/order/:id')
            }
        }])

        .controller('cartController',['$scope','$state','cartResources',function($scope,$state,cartResources){
            console.log("enter cart controller");

            $scope.THUMB_URL = "media/image/thumb/";
            $scope.ORIG_URL = "media/image/";

            console.log($scope);

            if($scope.cart && $scope.cart.cou==0){
                $scope.$parent.showContent = false;
                console.log("NO ITEMS in CART")
            } else {
                console.log($scope);
                $scope.$parent.showContent = true;
                $scope.orderItemsCou = $scope.cart.cou;
                console.log($scope.orderItemsCou);
            }

            //TODO: here will be check of auth
            $scope.toOrderCreation = function(){
                $state.transitionTo("cart.checkout");
            }

        }])

        .controller('cartCheckoutController',['$scope','cartResources','authService','loginModal','dataResources',function($scope,cartResources,authService,loginModal,dataResources){

            $scope.postDelivery = function(){
                console.log("BLAAAAAA!");
            };

            $scope.deliveries = [];

            dataResources.orderDelivery.get(function(data){
                angular.forEach(data,function(del){
                    $scope.deliveries.push(del);
                });

            });

            $scope.current = {
                recipientFname:null,
                recipientLname:null,
                recipientMname:null,
                delivery:null,
                recipientPhone:null,
                recipientEmail:null,
                address:null,
                comment:null
            };

            /*$scope.createOrder = function(cart){
                if(authService.isAuth()){
                    cartResources._order.save({items:cart,order:$scope.info});
                } else {
                    loginModal();
                }
            };*/

            $scope.save = function () {
                console.log("Save button pressed");
                var cleanOrderItems = [];

                $scope.current.items.forEach(function (item) {
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

            console.log($scope);

        }])
})();