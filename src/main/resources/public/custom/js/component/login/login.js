(function(){
    angular.module('login',[]);
})();

(function() {
    angular.module('login')
        .controller('loginController',['$scope','eventService','cryptoService',function ($scope, eventService, cryptoService) {

        $scope.keyPress = function(keyCode) {
            if (keyCode == 13){
                $scope.submit();
            }
        };

        $scope.submit = function() {
            eventService.onLogin({name:$scope.uname, password:cryptoService.encryptString($scope.password)});
        };
    }])
})();