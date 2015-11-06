(function(){
    angular.module('remember',[]);
})();

(function(){
    'use strict';

    angular.module('remember')
        .factory('authResource',['$resource',function($resource){
            return {
                _restore: $resource('/auth/restore',{},{post:{method:'POST'}}),
                _change: $resource('/auth/change',{},{post:{method:'POST'}}),
                _login: $resource('/auth/login',{},{post:{method:'POST'}})
            }
        }])

        .controller('rememberController',['$scope','$state','authResource', function($scope,$state,authResource){

            $scope.flag="remember";
            $scope.login = null;
            $scope.msg = "На Ваш почтовый ящик была отправленна ссылка для изменения пароля!";
            $scope.isSend = false;

            $scope.remember = function(){
                $rootScope.hidenLoginValue = authResource._restore.post($scope.login);
                if($rootScope.hidenLoginValue!=null){
                    $scope.isSend = true;
                }
            };
        }])

        .controller('restoreController',['$scope','$state','authResource','jwtHelper','dataResources', function($scope, $state, authResource,jwtHelper,dataResources) {
            console.log("in restoreController");

            $scope.password = null;
            $scope.passwordConf = null;

            $scope.change = function(){
                authResource._change.post({name:$rootScope.hidenLoginValue,password:$scope.password},function(response){
                    $scope.msg = "Ваш пароль был успешно изменен.";
                    $timeout(function() {
                        var token = response.token;
                        $cookies.put('token',token);
                        var decodedToken = jwtHelper.decodeToken(token);
                        //try get customer name
                        dataResources.customer.get({id:decodedToken.jti},function(data){
                            $rootScope.currentUser.name = data.firstName;
                        });
                        $rootScope.currentUser.roles = decodedToken.roles;
                        $state.transitionTo("home");
                    }, 5000);
                });
            };

        }])
})();
