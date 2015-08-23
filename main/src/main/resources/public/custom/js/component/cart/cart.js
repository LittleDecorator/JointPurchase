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


            if($scope.cart.cou==0){
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

        .controller('cartCheckoutController',['$scope','cartResources',function($scope,cartResources){

            $scope.postDelivery = function(){
                console.log("BLAAAAAA!");
            };

            $scope.deliveries = {
                inited:false,
                data:[
                    {type:"self",
                        name:"Самовывоз",
                        desc:"Вы также можете самостоятельно забрать заказ в Пункте Самовывоза, расположенном по адресу г. Москва, Огородный проезд, 20, стр. 38 с понедельника по субботу с 10-30 до 20-00. Пункт расположен в&nbsp;10&nbsp;минутах от&nbsp;метро Тимирязевская и&nbsp;в&nbsp;2&nbsp;минутах от&nbsp;станции монорельсовой дороги Улица Милашенкова (\</span><a href=\"http://www.axiomus.ru/i/carry_new.png\">посмотреть схему прохода\<\/a>\<span\>). Это одноэтажное здание с\&nbsp;деревянной дверью, как будто в\&nbsp;сказке про трех медведей.\&nbsp;Для прохода в\&nbsp;пункт не\&nbsp;требуется никаких пропусков, внутри есть примерочная кабина, столы для проверки заказов. Для автомобилистов всегда есть места для парковки, а\&nbsp;удобное расположение пункта позволяет быстро добраться до\&nbsp;ТТК, Алтуфьевского, Дмитровского, Ярославского шоссе. Стоимость доставки заказа в пункт самовывоза 100 руб.\&nbsp;"
                    },
                    {type:"moscow",
                        name:"Курьерская доставка",
                        desc:"Курьерская доставка по Москве — 350\&nbsp;руб\<br\>\<span\>Если Вы находитесь в Москве мы доставим Вам заказ, сделанный до 14.00, на следующий день после его оформления.                   Менеджер нашего магазина свяжется с Вами по телефону и договорится об удобном времени доставки. Оплата – наличными курьеру при получении заказа.Стоимость доставки в пределах МКАД 350 руб.&nbsp;\<\/span\>\<span\>Доплата за доставку за МКАД(не далее 15 км от МКАД) + 50 руб. за каждые полные/неполные 5 км. от МКАД\<\/span\>\<span\>Доставка осуществляется ежедневно c 10:00 до 20:00. В день доставки за полчаса или час курьер свяжется с Вами.&nbsp;Курьер может подождать 15 минут для того, чтобы Вы проверили и примерили заказанные товары.&nbsp;При заказе от 7000 руб. доставка осуществляется бесплатно."
                    },
                    {type:"country",
                        name:"Доставка по России",
                        desc:"Примерные тарифы и варианты доставки в Ваш регион Вы можете увидеть в разделе \<span style=\"text-decoration: underline;\"><a href=\"/oplata\">Оплата и доставка</a></span>&nbsp;, выбрав Ваш город в окошке над картой. &nbsp;После получения заказа, мы дополнительно свяжемся с Вами для согласования точной стоимости доставки.\&nbsp;"
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

                cartResources._order.save({items:cart,order:$scope.info});
            };

        }])
})();