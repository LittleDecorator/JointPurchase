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
                $state.transitionTo("cart.checkout");
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

        .controller('cartCheckoutController',['$scope','$state','authService','dataResources','deliveries','$q','$timeout',function($scope,$state,authService,dataResources,deliveries,$q, $timeout){

            $scope.showHints = true;

            $scope.createOrder = function(deferred){
                var cleanOrderItems = [];
                var orderPaymnet = 0;
                console.log($scope);
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

                var order = $.extend({},$scope.stepData[0].data);
                order.payment = orderPaymnet;
                order.delivery = $scope.stepData[1].data.delivery.id;
                dataResources.orderPrivate.post({items:cleanOrderItems,order:order}).$promise.then(function(data){
                    deferred.resolve(data);
                    //$state.go("cart.checkout.done", {id: data});
                }, function(error){
                    //$state.go("home");
                    deferred.reject(error);
                });
                return deferred.promise;
            };

            $scope.selectedStep = 0;
            $scope.stepProgress = 1;
            $scope.maxStep = 3;
            $scope.showBusyText = false;

            $scope.stepData = [
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

            $scope.enableNextStep = function nextStep() {
                //do not exceed into max step
                if ($scope.selectedStep >= $scope.maxStep) {
                    return;
                }
                //do not increment stepProgress when submitting from previously completed step
                if ($scope.selectedStep === $scope.stepProgress - 1) {
                    $scope.stepProgress = $scope.stepProgress + 1;
                }
                $scope.selectedStep = $scope.selectedStep + 1;
            };

            $scope.moveToPreviousStep = function moveToPreviousStep() {
                if ($scope.selectedStep > 0) {
                    $scope.selectedStep = $scope.selectedStep - 1;
                }
            };

            $scope.submitCurrentStep = function submitCurrentStep(stepData) {
                var deferred = $q.defer();
                if($scope.maxStep  - 1 == $scope.stepProgress){
                    $scope.showBusyText = true;
                    $scope.createOrder(deferred).then(function(data){
                        $scope.$parent.cart = {cou:0,content:[]};
                        console.log(data);
                        $scope.stepData[2].data = data;
                        $scope.showBusyText = false;
                        stepData.completed = true;
                        $scope.enableNextStep();
                    }, function(error){
                        $scope.$parent.cart = {cou:0,content:[]};
                        $state.go("home");
                    });
                } else {
                    $scope.showBusyText = false;
                    stepData.completed = true;
                    $scope.enableNextStep();
                }
            }

        }])

        .controller('cartPurchaseDoneController',['$scope','purchase',function($scope,purchase){
            if(purchase){
                $scope.purchase = purchase;

                var deliveryDate = 'UNKNOWN';

                var begin = 'You\'re order №{0} (created at {1}) is packaging, after it done it will be delivered to {2}.\nEstimated ship date is {3}.\n';

                var ifAuth = 'You can track your order in \< link to private cabinet\>. For this you need auth.';

                $scope.message = String.format(begin + ifAuth,purchase.uid,purchase.dateAdd,purchase.delivery,deliveryDate);
            }
        }])
})();