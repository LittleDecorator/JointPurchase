(function(){
    angular.module('cart',[]);
})();

(function() {
    'use strict';

    angular.module('cart')

        .controller('cartController',['$scope','$state',function($scope,$state){

            var templatePath = "pages/fragment/cart/";
            var vm = this;
            var mvm = $scope.$parent.mvm;

            vm.toOrderCreation = toOrderCreation;
            vm.getTemplate = getTemplate;

            if(mvm.cart && mvm.cart.cou==0){
                mvm.showContent = false;
            } else {
                mvm.showContent = true;
                vm.orderItemsCou = mvm.cart.cou;
            }
            
            //TODO: here will be check of auth
            function toOrderCreation(){
                $state.transitionTo("cart.confirm");
            }

            function getTemplate(){
                if(mvm.width < 481){
                    return templatePath + "cart-sm.html"
                }
                if(mvm.width > 480){
                    if(mvm.width < 841){
                        return templatePath + "cart-md.html"
                    }
                    return templatePath + "cart-lg.html"
                }
            }

        }])

})();