(function () {
   angular.module('cart', []);
})();

(function () {
   'use strict';

   angular.module('cart')

       .controller('cartController', ['$scope', '$state','dataResources', function ($scope, $state, dataResources) {

          var templatePath = "pages/fragment/cart/";

          var mvm = $scope.$parent.mvm;
          var vm = this;

          vm.toOrderCreation = toOrderCreation;
          vm.getTemplate = getTemplate;
          vm.refreshCart = refreshCart;

          if (mvm.cart && mvm.cart.cou === 0) {
             mvm.showContent = false;
          } else {
             mvm.showContent = true;
             vm.orderItemsCou = mvm.cart.cou;
          }

          //TODO: here will be check of auth
          function toOrderCreation() {
             refreshCart();
             $state.go("cart.confirm");
          }

          function refreshCart(){
             dataResources.cart.refresh(mvm.cart.content).$promise.then(function(data){
                var stash;
                data.forEach(function (category) {
                   stash = helpers.findInArrayById(mvm.cart.content, category.id);
                   category.cou = stash.cou;
                });
                mvm.cart.content = data;
             }, function (error){

             });
          }

          function getTemplate() {
             if (mvm.width < 481) {
                return templatePath + "cart-sm.html"
             }
             if (mvm.width > 480) {
                if (mvm.width < 841) {
                   return templatePath + "cart-md.html"
                }
                return templatePath + "cart-lg.html"
             }
          }

          refreshCart();

       }])

})();