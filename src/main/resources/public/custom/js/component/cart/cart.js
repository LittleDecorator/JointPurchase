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
                $state.transitionTo("cart.confirm");
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

})();