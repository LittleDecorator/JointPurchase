(function(){
    angular.module('registration',[]);
})();

(function(){
    'use strict';

    angular.module('registration')
        /* Контроллер регистрации */
        .controller('registrationController',['$rootScope','$scope','$state','dataResources','$mdToast','cryptoService',
            function($rootScope, $scope, $state, dataResources, $mdToast, cryptoService){

                $scope.showHints = true;

                $rootScope.currentUser.id = "873e2c78-2407-4093-979f-beefb03445f0";
            // объект регистрационных данных
            $scope.card={ firstName:null, lastName:null, mail:null, password:null, passwordRepeat: null};
            // флаг что заявка на регистрацию отправлена
            $scope.isSend = helpers.isPropertyNotEmpty($rootScope.currentUser,"id");

            var toast = $mdToast.simple().position('top right').hideDelay(3000);

            // отправка запроса на регистрацию
            $scope.send = function(){
                if($scope.registrationForm.$dirty){
                    if($scope.registrationForm.$valid){
                        var data = angular.extend({}, $scope.card);
                        data.password = cryptoService.encryptString(data.password);
                        dataResources.authRegister.post(data).$promise.then(function(data){
                            $scope.isSend = true;
                            $scope.showHints = true;
                            // запомним свежесозданый id регистрирующегося
                            $rootScope.currentUser.id = data.result;
                        }, function(error){
                            console.log(error);
                            $scope.isSend = false;
                            // $mdToast.show(toast.textContent('Неудалось произвести регистрацию\n'+ error.data.message).theme('error'));
                            $mdToast.show(toast.textContent('Неудалось произвести регистрацию').theme('error'));
                        })
                    } else {
                        $scope.showHints = false;
                    }
                }
            };

            $scope.noEmail = function(){
                $state.go('registrationConfirm');
            }
        }])

            .controller('registrationConfirmController',['$rootScope','$scope','$state','dataResources','$mdToast',
                function($rootScope, $scope, $state, dataResources, $mdToast){



                    $scope.showHints = true;
                    $scope.isSend = helpers.isPropertyNotEmpty($rootScope.currentUser,"id");
                    // объект регистрационных данных
                    $rootScope.currentUser.id = "873e2c78-2407-4093-979f-beefb03445f0";
                    $scope.data={phone:null, code:null, id:$rootScope.currentUser.id, type:'sms'};
                    // флаг что заявка на регистрацию отправлена
                    console.log($rootScope.currentUser);
                    var toast = $mdToast.simple().position('top right').hideDelay(3000);

                    // отправка запроса на регистрацию
                    $scope.sendPhone = function(){
                        console.log($scope);
                        // if($scope.registrationConfirmForm.$dirty){
                        //     if($scope.registrationConfirmForm.$valid){
                                dataResources.authRegisterConfirmRequest.post($scope.data).$promise.then(function(data){
                                    $scope.isSend = true;
                                    $scope.showHints = true;
                                }, function(error){
                                    $scope.isSend = false;
                                    $mdToast.show(toast.textContent('Неудалось выслать код подттверждения\n'+ error.message).theme('error'));
                                });
                            // } else {
                            //     $scope.showHints = false;
                            // }
                        // }
                    };

                    // отправка кода подтверждения
                    $scope.confirmCode = function(){
                        // if($scope.registrationConfirmForm.$dirty){
                        //     if($scope.registrationConfirmForm.$valid){
                                dataResources.authRegisterConfirm.post($scope.data).$promise.then(function(data){
                                    $scope.isSend = true;
                                    $scope.showHints = true;
                                    $state.transitionTo("registrationResult", {confirmed: true});
                                }, function(error){
                                    $scope.isSend = false;
                                    $mdToast.show(toast.textContent('Введен не верный код').theme('error'));
                                })
                            // } else {
                            //     $scope.showHints = false;
                            // }
                        // }
                    };

                    $scope.requestNew = function(){
                        //TODO: отправка нового кода. Только после таймера.
                    }
                }])

        /* Контроллер результата регистрации */
        .controller('registrationResultController',['$scope','$state','$stateParams', function($scope,$state,$stateParams) {
            //TODO: through login, pretty info page
            // получим признак подтверждена ли регистрация
            $scope.registrationStatus = $stateParams.confirmed;
            $scope.fontColor = $scope.iconName = $scope.registrationStatus ? "done" : "block";
            if($stateParams.confirmed){
                $scope.message = "Ваша учетная запись успешно активированна\n" +
                                 "Приятных покупок!"
            } else {
                $scope.message = "Ссылка используемая вами уже не действительна.\n" +
                                 "Пожалуйста повторите процедуру регистрации!"
            }

            /* Переход в каталог */
            $scope.toCatalog = function(){
                $state.go("catalog");
            };

            /* Переход на регистрацию */
            $scope.toRegistration = function(){
                $state.go("registration");
            };
        }])
})();
