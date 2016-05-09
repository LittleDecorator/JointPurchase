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

        .controller('rememberController',['$scope','authResource','$rootScope', function($scope,authResource,$rootScope){
            console.log("in rememberController");
            $scope.remember = {
                remail:null,
                flag:"remember",
                msg:"На Ваш почтовый ящик была отправленна ссылка для изменения пароля!",
                isSend:false
            };

            $scope.tryAgain = function(){
                $scope.remember.isSend = false;
                $scope.remember.isError = false;
                $scope.remember.remail = null;
            };

            $scope.send = function(){
                authResource._restore.post({data:$scope.remember.remail},
                    function(){
                        $scope.remember.isSend = true;
                        $rootScope.hidenLoginValue = $scope.remember.remail;
                    }, function(){
                        $scope.remember.msg = "Указанный почтовый ящик не зарегестрирован";
                        $scope.remember.isError = true;
                    }
                );

                /*$rootScope.hidenLoginValue = authResource._restore.post($scope.login);
                if($rootScope.hidenLoginValue!=null){
                    $scope.isSend = true;
                } else {
                    $scope.msg = "Указанный почтовый ящик не зарегестрирован";
                }*/
            };
        }])

        .controller('restoreController',['$scope','$state','authResource','jwtHelper','dataResources', function($scope, $state, authResource,jwtHelper,dataResources) {
            console.log("in restoreController");

            $scope.password = null;
            $scope.passwordConf = null;

            $scope.change = function(){
                // authResource._change.post({name:$rootScope.hidenLoginValue,password:$scope.password},function(response){
                //     $scope.msg = "Ваш пароль был успешно изменен.";
                    // $timeout(function() {
                    //     var token = response.token;
                    //     $cookies.put('token',token);
                    //     var decodedToken = jwtHelper.decodeToken(token);
                    //     //try get customer name
                    //     dataResources.customer.get({id:decodedToken.jti},function(data){
                    //         $rootScope.currentUser.name = data.firstName;
                    //     });
                    //     $rootScope.currentUser.roles = decodedToken.roles;
                    //     $state.transitionTo("home");
                    // }, 5000);
                // });
            };

        }])
})();
