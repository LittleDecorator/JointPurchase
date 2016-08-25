(function(){
    angular.module('confirm',[]);
})();

(function() {
    'use strict';

    //TODO: Почитать примеры для степпера и доки
    angular.module('confirm')

        .controller('confirmController',['$scope','$state','authService','dataResources','deliveries','$q','$timeout',function($scope,$state,authService,dataResources,deliveries,$q, $timeout){
            console.log("enter confirm controller");
            $scope.showHints = true;

            if(localStorage.getItem($state.current.name)){
                $scope.data = angular.fromJson(localStorage.getItem($state.current.name));
                $scope.data.stepData[1].data.delivery = helpers.findInArrayById($scope.data.stepData[1].data.deliveries,$scope.data.stepData[1].data.delivery.id);
                console.log($scope.data);
            } else {
                $scope.data = {};
                $scope.data.selectedStep = 0;
                $scope.data.stepProgress = 1;
                $scope.data.maxStep = 3;
                $scope.data.showBusyText = false;

                $scope.data.stepData = [
                    { step: 1, completed: false, optional: false,
                        data: {
                            recipientFname:null,
                            recipientLname:null,
                            recipientMname:null,
                            recipientPhone:null,
                            recipientEmail:null,
                            recipientAddress:null,
                            comment:null
                        }
                    },
                    { step: 2, completed: false, optional: false,
                        data: {
                            deliveries : deliveries,
                            delivery: deliveries[0]
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

                $scope.$parent.cart.content.forEach(function (item) {
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

                var order = $.extend({},$scope.data.stepData[0].data);
                order.payment = orderPaymnet;
                order.delivery = $scope.data.stepData[1].data.delivery.id;
                dataResources.orderPrivate.post({items:cleanOrderItems,order:order}).$promise.then(function(data){
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
                if($scope.data.maxStep  - 1 == $scope.data.stepProgress){
                    $scope.data.showBusyText = true;
                    $scope.createOrder(deferred).then(function(data){
                        $scope.$parent.cart = { cou:0, content:[] };
                        $scope.data.stepData[2].data = data;
                        $scope.data.showBusyText = false;
                        stepData.completed = true;
                        $scope.enableNextStep();
                    }, function(error){
                        //$scope.$parent.cart = {cou:0,content:[]};
                        //$state.go("home");
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

            $scope.confirmFirstStep = function(){
                $scope.showHints = false;
                if($scope.step1.$valid){
                    $scope.showHints = true;
                    $scope.submitCurrentStep($scope.data.stepData[0]);
                }
            }

        }])
})();
