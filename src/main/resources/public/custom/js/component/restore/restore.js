(function(){
    angular.module('restore',[]);
})();

(function(){
    'use strict';

    angular.module('restore')

        /* Контроллер восстановления пароля */
        .controller('restoreController',['$scope','cryptoService','dataResources','$mdToast', function($scope, cryptoService, dataResources, $mdToast){
            $scope.showHints = true;
            $scope.isSend = false;
            var toast = $mdToast.simple().position('top right').hideDelay(3000);

            $scope.data = { name:null, password:null};

            $scope.sendRestore = function(){
                if($scope.restoreForm.$dirty){
                    if($scope.restoreForm.$valid){
                        var req = angular.extend({}, $scope.data);
                        req.password = cryptoService.encryptString(req.password);
                        dataResources.authRestore.post(req).$promise.then(function(data){
                            console.log(data);
                            $scope.isSend = true;
                            $scope.showHints = true;
                        }, function(errors){
                            console.log(errors);
                            $scope.isSend = false;
                            $mdToast.show(toast.textContent('Неудалось произвести восстановление пароля\n '+ errors.data.message).theme('error'));
                        });
                    } else {
                        $scope.showHints = false;
                    }
                }
            };
        }])

        /* Контроллер страницы результата восстановления пароля */
        .controller('restoreResultController',['$scope','$state','authResource','jwtHelper','dataResources', function($scope, $state, authResource,jwtHelper,dataResources) {
            //TODO: through login, pretty info page
            // получим признак подтверждена ли операция восстановления пароля
            $scope.restoreStatus = $stateParams.confirmed;
            if($stateParams.confirmed){
                $scope.message = "Ваша учетная запись успешно изменена\n" +
                                 "Приятных покупок!"
            } else {
                $scope.message = "Ссылка используемая вами уже не действительна.\n" +
                                 "Пожалуйста повторите процедуру восстановления пароля!"
            }

            /* Переход в каталог */
            $scope.toCatalog = function(){
                $state.go("catalog");
            };

            /* ВИДИМО ВАРИАН АВТОРИЗАЦИИ И ПЕРЕАДРИСАЦИИ */
            // $scope.change = function(){
            //     // authResource._change.post({name:$rootScope.hidenLoginValue,password:$scope.password},function(response){
            //     //     $scope.msg = "Ваш пароль был успешно изменен.";
            //         // $timeout(function() {
            //         //     var token = response.token;
            //         //     $cookies.put('token',token);
            //         //     var decodedToken = jwtHelper.decodeToken(token);
            //         //     //try get customer name
            //         //     dataResources.customer.get({id:decodedToken.jti},function(data){
            //         //         $rootScope.currentUser.name = data.firstName;
            //         //     });
            //         //     $rootScope.currentUser.roles = decodedToken.roles;
            //         //     $state.transitionTo("home");
            //         // }, 5000);
            //     // });
            // };

        }])
})();
