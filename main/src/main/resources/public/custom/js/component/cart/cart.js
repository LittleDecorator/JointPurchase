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


            if($scope.cart && $scope.cart.cou==0){
                $scope.showContent = false;
                console.log("NO ITEMS in CART")
            } else {
                $scope.showContent = true;
                $scope.orderItemsCou = $scope.cart.cou;
                console.log($scope.orderItemsCou);
            }

            //TODO: here will be check of auth
            $scope.toOrderCreation = function(){
                $state.transitionTo("cart.checkout");
            }
        }])

        .controller('cartCheckoutController',['$scope','cartResources','authService','loginModal',function($scope,cartResources,authService,loginModal){

            $scope.postDelivery = function(){
                console.log("BLAAAAAA!");
            };

            $scope.deliveries = {
                inited:false,
                data:[
                    {type:"self",
                        name:"Самовывоз",
                        desc:"Стоимость доставки заказа в пункт самовывоза - 100 руб. Вы также можете самостоятельно забрать заказ в Пункте Самовывоза, расположенном по адресу г. Москва, Огородный проезд, 20, стр. 38 с понедельника по субботу с 10-30 до 20-00."
                    },
                    {type:"moscow",
                        name:"Курьерская доставка",
                        desc:"Курьерская доставка по Москве — 350руб. Если Вы находитесь в Москве мы доставим Вам заказ, сделанный до 14.00, на следующий день после его оформления. Оплата – наличными курьеру при получении заказа.Стоимость доставки в пределах МКАД 350 руб. Доплата за доставку за МКАД(не далее 15 км от МКАД) + 50 руб. за каждые полные/неполные 5 км. от МКАД. Доставка осуществляется ежедневно c 10:00 до 20:00. В день доставки за полчаса или час курьер свяжется с Вами. При заказе от 7000 руб. доставка осуществляется бесплатно."
                    },
                    {type:"country",
                        name:"Доставка по России",
                        desc:"Примерные тарифы и варианты доставки в Ваш регион Вы можете увидеть в разделе 'Оплата и доставка'. После получения заказа, мы дополнительно свяжемся с Вами для согласования точной стоимости доставки."
                    }]
            };

            $scope.info = {
                recipient:null,
                delivery:null,
                phone:null,
                email:null,
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

        }])
})();