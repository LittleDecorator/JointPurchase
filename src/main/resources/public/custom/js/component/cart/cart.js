(function(){
    angular.module('cart',[]);
})();

(function() {
    'use strict';

    angular.module('cart')

        .controller('cartController',['$scope','$state',function($scope,$state){
            console.log("enter cart controller");

            var templatePath = "pages/fragment/cart/";

            $scope.THUMB_URL = "media/image/thumb/";
            $scope.ORIG_URL = "media/image/";

            console.log($scope);

            if($scope.cart && $scope.cart.cou==0){
                $scope.$parent.showContent = false;
            } else {
                $scope.$parent.showContent = true;
                $scope.orderItemsCou = $scope.cart.cou;
            }

            //TODO: here will be check of auth
            $scope.toOrderCreation = function(){
                $state.transitionTo("cart.checkout");
            };

            $scope.getTemplate = function(){
                if($scope.width < 481){
                    return templatePath + "cart-sm.html"
                }
                if($scope.width > 480){
                    if($scope.width < 841){
                        return templatePath + "cart-md.html"
                    }
                    return templatePath + "cart-lg.html"
                }
            };

        }])

        .controller('cartCheckoutController',['$scope','$state','authService','dataResources',function($scope,$state,authService,dataResources){

            $scope.postDelivery = function(){
                console.log("Post delivery method triggered!");
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
                recipientAddress:null,
                comment:null
            };

            $scope.createOrder = function(){
                console.log($scope);
                if(!$scope.checkout.$invalid){

                    var cleanOrderItems = [];
                    var orderPaymnet = 0;

                    $scope.cart.content.forEach(function (item) {
                        if(item.cou>0){
                            orderPaymnet = orderPaymnet + (item.cou * item.price);
                            cleanOrderItems.push({
                                id:null,
                                orderId: null,
                                itemId: item.id,
                                cou: item.cou
                            })
                        }
                    });
                    $scope.current.payment = orderPaymnet;
                    $scope.current.delivery = $scope.current.delivery.value;
                    dataResources.orderPrivate.post({items:cleanOrderItems,order:$scope.current})
                        .$promise.then(function(data){
                            console.log("Fine");
                            $scope.$parent.cart = {cou:0,content:[]};
                            $state.go("cart.checkout.done", {id: data});
                        }, function(error){
                            console.log("Error");
                            console.log("Some fail happen: "+error);
                            $state.go("home");
                        });

                    console.log($scope);
                }
            };

        }])

        .controller('cartPurchaseDoneController',['$scope','purchase',function($scope,purchase){
            if(purchase){
                $scope.purchase = purchase;

                var deliveryDate = 'UNKNOWN';

                var begin = 'You\'re order №{0} (created at {1}) is packaging, after it done it will be delivered to {2}.\nEstimated ship date is {3}.\n';

                var ifAuth = 'You can track your order in \< link to private cabinet\>. For this you need auth.';

                $scope.message = String.format(begin + ifAuth,purchase.uid,purchase.dateAdd,purchase.delivery,deliveryDate);
            }
        }])
})();