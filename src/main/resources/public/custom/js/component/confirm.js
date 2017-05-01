(function(){
    angular.module('confirm',[]);
})();

(function() {
    'use strict';

    //TODO: Как-то отказаться от явного id почтовой доставки при отображении индекса
    //TODO: Найти способ сторить инфу во время заполнения
    angular.module('confirm')

        /* Контроллер формирования заказа */
        .controller('confirmController',['$scope','$state','authService','dataResources','deliveries','$q','$timeout','$rootScope',function($scope,$state,authService,dataResources,deliveries,$q, $timeout, $rootScope){

            /* Если при переходе сюда у нас почему-то пустая корзина, то возвращаемся в корзину */
            if($scope.cart == null || $scope.cart.content.length == 0 ){
                $state.go("cart");
            }

            $scope.showHints = true;
            $scope.forms = {};


            /* ТУТ БЫЛА ПОПЫТКА СОХРАНИТЬ УЖЕ ВВЕДЕННЫЕ ДАННЫЕ  */
            // if(localStorage.getItem($state.current.name)){
            //     $scope.data = angular.fromJson(localStorage.getItem($state.current.name));
                // $scope.data.stepData[0].data.delivery = helpers.findInArrayById($scope.data.stepData[0].data.deliveries,$scope.data.stepData[0].data.delivery.id);
            // } else {
                $scope.data = {};
                $scope.data.selectedStep = 0;
                $scope.data.stepProgress = 1;
                $scope.data.maxStep = 3;
                $scope.data.showBusyText = false;
                $scope.data.usePrivate = false;
                $scope.data.userInfoStash = null;

                $scope.data.stepData = [
                    { step: 1, completed: false, optional: false,
                        data: {
                            deliveries : deliveries,
                            delivery: deliveries[0]
                        }
                    },
                    { step: 2, completed: false, optional: false,
                        data: {
                            recipientFname:null,
                            recipientLname:null,
                            recipientMname:null,
                            recipientPhone:null,
                            recipientEmail:null,
                            recipientAddress:null,
                            postAddress:null,
                            comment:null
                        }
                    },
                    { step: 3, completed: false, optional: false,
                        data: {

                        }
                    }
                ];
            // }

            /* Создание заказа */
            $scope.createOrder = function(deferred){
                // список товаров для формирования заказа
                var cleanOrderItems = [];
                var orderPayment = 0;

                /* пройдемся по списку товаров */
                $scope.cart.content.forEach(function (item) {
                    if(item.cou > 0){
                        orderPayment = orderPayment + (item.cou * item.price);
                        cleanOrderItems.push({
                            item: {id: item.id},
                            count: item.cou
                        })
                    }
                });

                // возьмем введенные данные о заказе
                var order = $.extend({}, $scope.data.stepData[1].data);
                order.payment = orderPayment;
                // возьмем выбранный способ доставки
                order.delivery = $scope.data.stepData[0].data.delivery.id;

                var request = null;
                // определим авторизован ли пользователь
                if(localStorage.getItem('token') == undefined){
                    // request = dataResources.order.post({items:cleanOrderItems, order:order});
                } else {
                    // request = dataResources.orderPrivate.post({items:cleanOrderItems, order:order});
                }

                //promises для запроса на создание заказа
                request.$promise.then(function(data){
                    deferred.resolve(data);
                }, function(error){
                    deferred.reject(error);
                });
                return deferred.promise;
            };

            /* Переход к следующему шагу */
            $scope.enableNextStep = function() {
                //do not exceed into max step
                if ($scope.data.selectedStep >= $scope.data.maxStep) {
                    return;
                }
                //do not increment stepProgress when submitting from previously completed step
                if ($scope.data.selectedStep === $scope.data.stepProgress - 1) {
                    $scope.data.stepProgress = $scope.data.stepProgress + 1;
                }
                $scope.data.selectedStep = $scope.data.selectedStep + 1;

                //дошли до последнего шага
                if($scope.data.maxStep === $scope.data.stepProgress){
                    $scope.clearCart();
                }
            };

            /* Возврат на предыдущий шаг */
            $scope.moveToPreviousStep = function() {
                if ($scope.data.selectedStep > 0) {
                    $scope.data.selectedStep = $scope.data.selectedStep - 1;
                    // localStorage.setItem($state.current.name,angular.toJson($scope.data));
                }
            };

            /* Подтверждение текущего шага */
            $scope.submitCurrentStep = function(stepData) {
                console.log($scope)
                var deferred = $q.defer();
                if(($scope.data.maxStep  - 1 == $scope.data.stepProgress) &&  ($scope.data.stepProgress == $scope.data.selectedStep + 1)){
                    //если достигли последнего шага
                    $scope.data.showBusyText = true;
                    $scope.createOrder(deferred).then(function(data){
                        $scope.data.stepData[2].data = data;
                        $scope.data.showBusyText = false;
                        stepData.completed = true;
                        $scope.enableNextStep();
                    }, function(error){
                        console.log(error);
                        //TODO: нужно наполнить даными о невозможности создать заказ и перейти на последний шаг
                    });
                } else {
                    $scope.data.showBusyText = false;
                    stepData.completed = true;
                    $scope.enableNextStep();
                }
            };

            /* Валидация данных о получателе и подтверждение текущего шага */
            $scope.confirmInfoStep = function(){
                $scope.showHints = false;
                console.log($scope);
                if($scope.forms.step2.$valid){
                    $scope.showHints = true;
                    $scope.submitCurrentStep($scope.data.stepData[1]);
                }
            };

            /* Использование персональных данных авторизованных клиентов */
            $scope.switchUserInfo = function(){
                if($scope.data.usePrivate){
                    $scope.data.userInfoStash = angular.copy($scope.data.stepData[1].data);

                    $scope.data.stepData[1].data.recipientFname = $rootScope.currentUser.firstName;
                    $scope.data.stepData[1].data.recipientLname = $rootScope.currentUser.lastName;
                    $scope.data.stepData[1].data.recipientMname = $rootScope.currentUser.middleName;
                    $scope.data.stepData[1].data.recipientPhone = $rootScope.currentUser.phoneNumber;
                    $scope.data.stepData[1].data.recipientEmail = $rootScope.currentUser.email;
                    $scope.data.stepData[1].data.recipientAddress = $rootScope.currentUser.address;
                    $scope.data.stepData[1].data.postAddress = $rootScope.currentUser.postAddress;
                } else {
                    $scope.data.stepData[1].data = $scope.data.userInfoStash;
                }
            };

            /* Отмена оформления */
            $scope.cancel = function(){
                localStorage.removeItem($state.current.name);
                $state.go("cart");
            };

            /* Получения шаблона страницы */
            $scope.getTemplateUrl = function(){
                console.log("enter getTemplate")
                var templatePath = "pages/fragment/confirm/";
                if($scope.width < 601){
                    return templatePath + "confirm-sm.html";
                } else {
                    return templatePath + "confirm-lg.html";
                }
            };

            /* Переход в каталог после оформления заказа */
            $scope.toCatalog = function(){
                $state.go("catalog");
            };

            $scope.fragmentUrl = $scope.getTemplateUrl();

        }])
})();
