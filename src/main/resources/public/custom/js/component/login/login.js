(function(){
    angular.module('login',[]);
})();

(function() {
    angular.module('login')
        .controller('loginController',['$scope','eventService',function ($scope,eventService) {

        $scope.keyPress = function(keyCode) {
            if (keyCode == 13){
                $scope.submit();
            }
        };

        $scope.submit = function() {
            eventService.onLogin({name:$scope.uname,password:$scope.password});
        };
    }])
})();