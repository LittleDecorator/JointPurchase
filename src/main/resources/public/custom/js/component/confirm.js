(function(){
    angular.module('confirm',[]);
})();

(function() {
    'use strict';

    //TODO: Как-то отказаться от явного id почтовой доставки при отображении индекса
    //TODO: Найти способ сторить инфу во время заполнения
    angular.module('confirm')

        /* Контроллер формирования заказа */
        .controller('confirmController',['$scope','$state','authService','dataResources','deliveries','$q','$timeout','$rootScope',
            function($scope,$state,authService,dataResources,deliveries,$q, $timeout, $rootScope){

                var mvm = $scope.$parent.mvm;
                var vm = this;

                vm.checkCart = checkCart;
                vm.initStepData = initStepData;
                vm.createOrder = createOrder;
                vm.enableNextStep = enableNextStep;
                vm.moveToPreviousStep = moveToPreviousStep;
                vm.submitCurrentStep = submitCurrentStep;
                vm.confirmInfoStep = confirmInfoStep;
                vm.switchUserInfo = switchUserInfo;
                vm.cancel = cancel;
                vm.toCatalog = toCatalog;
                vm.getTemplateUrl = getTemplateUrl;

                vm.showHints = true;
                vm.forms = {};
                vm.data = {selectedStep: 0, stepProgress: 1, maxStep: 4, showBusyText: false, usePrivate: false, userInfoStash: null};
                vm.fragmentUrl = getTemplateUrl();

                /**
                 * Проверка наличия товара в корзине
                 */
                function checkCart(){
                    /* Если при переходе сюда у нас почему-то пустая корзина, то возвращаемся в корзину */
                    if(mvm.cart == null || mvm.cart.content.length == 0 ){
                        $state.go("cart");
                    }
                }

                /**
                 * Создание структуры данных для Stepper'а
                 */
                function initStepData() {
                    vm.data.stepData = [
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
                        },
                        { step: 4, completed: false, optional: false,
                            data: {

                            }
                        }
                    ];
                }

                /**
                 * Создание заказа
                 * @param deferred
                 * @returns {*|null|promise.promise|jQuery.promise|promise|d.promise}
                 */
                function createOrder(deferred){
                    // список товаров для формирования заказа
                    var cleanOrderItems = [];
                    var orderPayment = 0;

                    /* пройдемся по списку товаров */
                    mvm.cart.content.forEach(function (item) {
                        if(item.cou > 0){
                            orderPayment = orderPayment + (item.cou * item.price);
                            cleanOrderItems.push({
                                item: {id: item.id},
                                count: item.cou
                            })
                        }
                    });

                    // возьмем введенные данные о заказе
                    var order = $.extend({}, vm.data.stepData[1].data);
                    order.payment = orderPayment;

                    // возьмем выбранный способ доставки
                    order.delivery = vm.data.stepData[0].data.delivery.id;

                    var request = null;
                    // определим авторизован ли пользователь
                    if(localStorage.getItem('token') == undefined){
                        request = dataResources.order.post({items:cleanOrderItems, order:order});
                    } else {
                        request = dataResources.orderPrivate.post({items:cleanOrderItems, order:order});
                    }

                    //promises для запроса на создание заказа
                    request.$promise.then(function(data){
                        deferred.resolve(data);
                    }, function(error){
                        deferred.reject(error);
                    });
                    return deferred.promise;
                }

                /**
                 * Переход к следующему шагу
                 */
                function enableNextStep(){
                    //do not exceed into max step
                    if (vm.data.selectedStep >= vm.data.maxStep) {
                        return;
                    }
                    //do not increment stepProgress when submitting from previously completed step
                    if (vm.data.selectedStep === vm.data.stepProgress - 1) {
                        vm.data.stepProgress = vm.data.stepProgress + 1;
                    }
                    vm.data.selectedStep = vm.data.selectedStep + 1;

                    //дошли до последнего шага
                    if(vm.data.maxStep === vm.data.stepProgress){
                        mvm.clearCart();
                    }
                }

                /**
                 * Возврат на предыдущий шаг
                 */
                function moveToPreviousStep() {
                    if (vm.data.selectedStep > 0) {
                        vm.data.selectedStep = vm.data.selectedStep - 1;
                    // localStorage.setItem($state.current.name,angular.toJson($scope.data));
                    }
                }

                /**
                 * Подтверждение текущего шага
                 * @param stepData
                 */
                function submitCurrentStep(stepData) {
                    var deferred = $q.defer();
                    if((vm.data.maxStep  - 1 == vm.data.stepProgress) &&  (vm.data.stepProgress == vm.data.selectedStep + 1)){
                        //если достигли последнего шага
                        vm.data.showBusyText = true;
                        createOrder(deferred).then(function(data){
                            vm.data.stepData[2].data = data;
                            vm.data.showBusyText = false;
                            stepData.completed = true;
                            enableNextStep();
                        }, function(error){
                            console.log(error);
                            //TODO: нужно наполнить даными о невозможности создать заказ и перейти на последний шаг
                        });
                    } else {
                        vm.data.showBusyText = false;
                        stepData.completed = true;
                        enableNextStep();
                    }
                }

                /**
                 * Валидация данных о получателе и подтверждение текущего шага
                 */
                function confirmInfoStep(){
                    vm.showHints = false;
                    if(vm.forms.step2.$valid){
                        vm.showHints = true;
                        submitCurrentStep(vm.data.stepData[1]);
                    }
                }

                /**
                 * Использование персональных данных авторизованных клиентов
                 */
                function switchUserInfo(){
                    if(vm.data.usePrivate){
                        vm.data.userInfoStash = angular.copy(vm.data.stepData[1].data);

                        vm.data.stepData[1].data.recipientFname = $rootScope.currentUser.firstName;
                        vm.data.stepData[1].data.recipientLname = $rootScope.currentUser.lastName;
                        vm.data.stepData[1].data.recipientMname = $rootScope.currentUser.middleName;
                        vm.data.stepData[1].data.recipientPhone = $rootScope.currentUser.phoneNumber;
                        vm.data.stepData[1].data.recipientEmail = $rootScope.currentUser.email;
                        vm.data.stepData[1].data.recipientAddress = $rootScope.currentUser.address;
                        vm.data.stepData[1].data.postAddress = $rootScope.currentUser.postAddress;
                    } else {
                        vm.data.stepData[1].data = vm.data.userInfoStash;
                    }
                }

                /**
                 * Отмена оформления
                 */
                function cancel(){
                    localStorage.removeItem($state.current.name);
                    $state.go("cart");
                }

                /**
                 * Переход в каталог после оформления заказа
                 */
                function toCatalog(){
                    $state.go("catalog");
                }

                /**
                 * Получения шаблона страницы
                 * @returns {string}
                 */
                function getTemplateUrl(){
                    var templatePath = "pages/fragment/confirm/";
                    if(mvm.width < 601){
                        return templatePath + "confirm-sm.html";
                    } else {
                        return templatePath + "confirm-lg.html";
                    }
                }

                /**
                 * Вызовется после include
                 */
                function afterInclude(){}

                /* инициализация и проверка */
                checkCart();
                initStepData();

        }])
})();
