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

            ////dataResources.deliveryMap.get(function(data){
            ////    $scope.deliveries = data;
            ////});
            //

            $scope.createOrder = function(){
                //if(!$scope.checkout.$invalid){

                    //var cleanOrderItems = [];
                    //var orderPaymnet = 0;
                    //
                    //$scope.cart.content.forEach(function (item) {
                    //    if(item.cou>0){
                    //        orderPaymnet = orderPaymnet + (item.cou * item.price);
                    //        cleanOrderItems.push({
                    //            id:null,
                    //            orderId: null,
                    //            itemId: item.id,
                    //            cou: item.cou
                    //        })
                    //    }
                    //});
                    //$scope.current.payment = orderPaymnet;
                    //$scope.current.delivery = $scope.current.delivery.value;
                    //dataResources.orderPrivate.post({items:cleanOrderItems,order:$scope.current})
                    //    .$promise.then(function(data){
                    //        $scope.$parent.cart = {cou:0,content:[]};
                    //        $state.go("cart.checkout.done", {id: data});
                    //    }, function(error){
                    //        $state.go("home");
                    //    });

                //}
            };
            //
            //console.log($scope)

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
                { step: 3, completed: false, optional: false, data: {} }
            ];

            $scope.enableNextStep = function nextStep() {
                //do not exceed into max step
                if ($scope.selectedStep >= $scope.maxStep) {
                    return;
                }
                //do not increment vm.stepProgress when submitting from previously completed step
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

            $scope.submitCurrentStep = function submitCurrentStep(stepData, isSkip) {
                var deferred = $q.defer();
                $scope.showBusyText = true;
                console.log('On before submit');
                if (!stepData.completed && !isSkip) {
                    //simulate $http
                    $timeout(function () {
                        $scope.showBusyText = false;
                        console.log('On submit success');
                        deferred.resolve({ status: 200, statusText: 'success', data: {} });
                        //move to next step when success
                        stepData.completed = true;
                        $scope.enableNextStep();
                    }, 1000)
                } else {
                    $scope.showBusyText = false;
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