(function(){
    angular.module('registration',[]);
})();

(function(){
    'use strict';

    angular.module('registration')
        /* Контроллер регистрации */
        .controller('registrationController',['$scope','$state','dataResources','$mdToast','cryptoService', function($scope,$state,dataResources, $mdToast, cryptoService){
            $scope.showHints = true;
            // объект регистрационных данных
            $scope.card={ firstName:null, lastName:null, mail:null, password:null, passwordRepeat: null};
            // флаг что заявка на регистрацию отправлена
            $scope.isSend = false;
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
                        }, function(error){
                            $scope.isSend = false;
                            $mdToast.show(toast.textContent('Неудалось произвести регистрацию\n'+ error.data.message).theme('error'));
                        })
                    } else {
                        $scope.showHints = false;
                    }
                }
            };
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
