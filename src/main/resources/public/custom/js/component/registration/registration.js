(function(){
    angular.module('registration',[]);
})();

(function(){
    'use strict';

    angular.module('registration')
        /* Контроллер регистрации */
        .controller('registrationController',['$rootScope','$scope','$state','dataResources','$mdToast','cryptoService','store',
            function($rootScope, $scope, $state, dataResources, $mdToast, cryptoService, store){

                // $rootScope.currentUser.id = "873e2c78-2407-4093-979f-beefb03445f0";
                var toast = $mdToast.simple().position('top right').hideDelay(3000);

                var vm = this;
                var mvm = $scope.$parent.mvm;

                vm.send = send;
                vm.noEmail = noEmail;

	            // флаг что заявка на регистрацию отправлена
	            vm.isSend = helpers.isPropertyNotEmpty(store.get("id"));
                vm.form = { registration:null };
                vm.showHints = true;

                // объект регистрационных данных
                vm.card={
                    firstName:null,
                    lastName:null,
                    mail:null,
                    password:null,
                    passwordRepeat: null
                };

                // отправка запроса на регистрацию
                function send(){
                    if(vm.registrationForm.$dirty){
                        if(vm.registrationForm.$valid){
                            var data = angular.extend({}, vm.card);
                            data.password = cryptoService.encryptString(data.password);

                            dataResources.authRegister.post(data).$promise.then(function(data){
                                vm.isSend = true;
                                vm.showHints = true;
                                // запомним свежесозданый id регистрирующегося
                                store.set("id",data.result);
                            }, function(error){
                                console.log(error);
                                vm.isSend = false;
                                // $mdToast.show(toast.textContent('Неудалось произвести регистрацию\n'+ error.data.message).theme('error'));
                                $mdToast.show(toast.textContent('Неудалось произвести регистрацию').theme('error'));
                            })
                        } else {
                            vm.showHints = false;
                        }
                    }
                }

                // использовать форму подтверждения если письмо не дошло
                function noEmail(){
                    $state.go('registrationConfirm');
                }
            }])

		.controller('registrationConfirmController',['$rootScope','$scope','$state','dataResources','$mdToast', '$interval', 'store',
            function($rootScope, $scope, $state, dataResources, $mdToast, $interval, store){

	            // $rootScope.currentUser.id = "873e2c78-2407-4093-979f-beefb03445f0";
	            var toast = $mdToast.simple().position('top right').hideDelay(3000);
	            var duration = moment.duration('00:00');
	            var oneSecond = moment.duration(1, 'seconds');
	            var stopTime = null;

	            var vm = this;
	            var mvm = $scope.$parent.mvm;

	            vm.sendPhone = sendPhone;
	            vm.confirmCode = confirmCode;
	            vm.requestNew = requestNew;
	            vm.startCountdown = startCountdown;
	            vm.resetCountdown = resetCountdown;

	            vm.form = {registrationConfirmRequest: {}, registrationConfirm: {}};
	            vm.showHints = true;
	            vm.isSend = helpers.isPropertyNotEmpty(store.get("id"));
                vm.isFailed = false;
	            vm.timer = null;
	            // vm.countdownTime = "#{duration.hours()}:#{duration.minutes()}:#{duration.seconds()}";

	            // объект регистрационных данных
	            vm.data={
		            phone:null,
		            code:null,
		            id: store.get("id"),
		            type:'sms'
	            };

                    // отправка запроса на регистрацию
                    function sendPhone(){
                        if(vm.registrationConfirmRequest.$dirty){
                            if(vm.registrationConfirmRequest.$valid){
                                dataResources.authRegisterConfirmRequest.post(vm.data).$promise.then(function(data){
                                    vm.isSend = true;
                                    vm.showHints = true;
                                }, function(error){
                                    vm.isSend = false;
                                    $mdToast.show(toast.textContent('Неудалось выслать код подтверждения\n'+ error.message).theme('error'));
                                });
                            } else {
                                vm.showHints = false;
                            }
                        }
                    }

                    // отправка кода подтверждения
                    function confirmCode(){
                        if(vm.registrationConfirm.$dirty){
                            if(vm.registrationConfirm.$valid){
                                dataResources.authRegisterConfirm.post(vm.data).$promise.then(function(data){
                                    $interval.cancel(stopTime);
                                    vm.isSend = true;
                                    vm.showHints = true;
                                    // если код был принят, идем на страницу результата регистрации
                                    $state.go("registrationResult", { confirmed: true });
                                }, function(error){
                                    vm.isSend = false;
                                    $mdToast.show(toast.textContent('Введен не верный код').theme('error'));
                                })
                            } else {
                                vm.showHints = false;
                            }
                        }
                    }

                    function requestNew(){
                        dataResources.authRegisterConfirmRequest.post(vm.data).$promise.then(function(data){
                            vm.isSend = true;
                            vm.isFailed = false;
                            vm.showHints = true;
                            resetCountdown();
                        }, function(error){
                            vm.isSend = false;
                            $mdToast.show(toast.textContent('Неудалось выслать код подтверждения\n'+ error.message).theme('error'));
                        });
                    }

	                function startCountdown(startTime){
		                duration  = moment.duration(startTime);

		                stopTime = $interval(function(){
			                vm.timer = duration.subtract(oneSecond);
			                if(duration.asSeconds() == 0){
                                $interval.cancel(stopTime);
                                vm.isFailed = true;
			                }
		                }, 1000);
	                }

		            function resetCountdown(){
			            $interval.cancel(stopTime);
			            startCountdown(5*60*1000);
		            }

		            // стартуем таймер если был отправлен код
	            if(vm.isSend){
		            startCountdown(5*60*1000);
	            }

            }])

        /* Контроллер результата регистрации */
        .controller('registrationResultController',['$scope','$state','$stateParams',
            function($scope,$state,$stateParams) {

                //TODO: through login, pretty info page

                var vm = this;
                var mvm = $scope.$parent.mvm;

                vm.toCatalog = toCatalog;
                vm.toRegistration = toRegistration;

                // получим признак подтверждена ли регистрация
                vm.registrationStatus = $stateParams.confirmed;
                vm.fontColor = vm.iconName = vm.registrationStatus ? "done" : "block";

                if($stateParams.confirmed){
                    vm.message = "Ваша учетная запись успешно активированна\n" + "Приятных покупок!"
                } else {
                    vm.message = "Ссылка используемая вами уже не действительна.\n" + "Пожалуйста повторите процедуру регистрации!"
                }

                /* Переход в каталог */
                function toCatalog(){
                    $state.go("catalog");
                }

                /* Переход на регистрацию */
                function toRegistration(){
                    $state.go("registration");
                }
            }])
})();
