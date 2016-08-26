(function(){
    angular.module('confirm',[]);
})();

(function() {
    'use strict';

    //TODO: Как-то отказаться от явного id почтовой доставки при отображении индекса
    //TODO: Найти способ сторить инфу во время заполнения
    angular.module('confirm')

        .controller('confirmController',['$scope','$state','authService','dataResources','deliveries','$q','$timeout','$rootScope',function($scope,$state,authService,dataResources,deliveries,$q, $timeout, $rootScope){
            console.log("enter confirm controller");
            $scope.showHints = true;

            if(localStorage.getItem($state.current.name)){
                $scope.data = angular.fromJson(localStorage.getItem($state.current.name));
                $scope.data.stepData[0].data.delivery = helpers.findInArrayById($scope.data.stepData[0].data.deliveries,$scope.data.stepData[0].data.delivery.id);
                console.log($scope.data);
            } else {
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
            }

            $scope.createOrder = function(deferred){
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

                var order = $.extend({},$scope.data.stepData[1].data);
                order.payment = orderPaymnet;
                order.delivery = $scope.data.stepData[0].data.delivery.id;
                var request = null;
                if(localStorage.getItem('token') == undefined){
                    request = dataResources.order.post({items:cleanOrderItems,order:order});
                } else {
                    request = dataResources.orderPrivate.post({items:cleanOrderItems,order:order});
                }
                //dataResources.orderPrivate.post({items:cleanOrderItems,order:order}).$promise.then(function(data){
                request.$promise.then(function(data){
                    deferred.resolve(data);
                }, function(error){
                    deferred.reject(error);
                });
                return deferred.promise;
            };

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
                console.log($scope.data);
                localStorage.setItem($state.current.name,angular.toJson($scope.data));
            };

            $scope.moveToPreviousStep = function() {
                if ($scope.data.selectedStep > 0) {
                    $scope.data.selectedStep = $scope.data.selectedStep - 1;
                    console.log($scope.data);
                    localStorage.setItem($state.current.name,angular.toJson($scope.data));
                }
            };

            $scope.submitCurrentStep = function(stepData) {
                var deferred = $q.defer();
                console.log($scope.data.maxStep);
                console.log($scope.data.stepProgress);
                console.log($scope.data.selectedStep);
                if(($scope.data.maxStep  - 1 == $scope.data.stepProgress) &&  ($scope.data.stepProgress == $scope.data.selectedStep + 1)){
                    console.log("pass condition");
                    $scope.data.showBusyText = true;
                    $scope.createOrder(deferred).then(function(data){
                        $scope.clearCart();
                        $scope.data.stepData[2].data = data;
                        $scope.data.showBusyText = false;
                        stepData.completed = true;
                        $scope.enableNextStep();
                    }, function(error){
                        console.log(error)
                    });
                } else {
                    $scope.data.showBusyText = false;
                    stepData.completed = true;
                    $scope.enableNextStep();
                }
            };

            $scope.clear = function(){
                localStorage.removeItem($state.current.name);
            };

            $scope.confirmInfoStep = function(){
                $scope.showHints = false;
                if($scope.step2.$valid){
                    $scope.showHints = true;
                    $scope.submitCurrentStep($scope.data.stepData[1]);
                }
            };

            //will be called on change tab
            //TODO: maybe change to promise
            $scope.onTabSelect = function(){
                $timeout(function(){
                    localStorage.setItem($state.current.name,angular.toJson($scope.data));
                },100);
            };

            $scope.switchUserInfo = function(){
                console.log('switchUserInfo');
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

            $scope.cancel = function(){
                localStorage.removeItem($state.current.name);
                $state.go("cart");
            }

        }])
})();
