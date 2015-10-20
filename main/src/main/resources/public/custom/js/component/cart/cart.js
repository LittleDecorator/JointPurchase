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
            $scope.showContent = false;

            $scope.THUMB_URL = "media/image/thumb/";
            $scope.ORIG_URL = "media/image/";

            if($scope.cart && $scope.cart.cou==0){
                $scope.showContent = false;
                console.log("NO ITEMS in CART")
            } else {
                console.log($scope);
                $scope.showContent = true;
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

            $scope.info = {
                recipientFname:null,
                recipientLname:null,
                recipientMname:null,
                delivery:null,
                recipientPhone:null,
                recipientEmail:null,
                address:null,
                comment:null
            };

            $scope.createOrder = function(cart){
                if(authService.isAuth()){
                    cartResources._order.save({items:cart,order:$scope.info});
                } else {
                    loginModal();
                }
            };

            console.log($scope);

        }])
})();