(function(){
    angular.module('login',[]);
})();

(function() {
    angular.module('login')
        .controller('loginController',['$scope','eventService','loaderModal',function ($scope,eventService,loaderModal) {
        console.log("in login controller");

        $scope.keyPress = function(keyCode) {
            console.log("keyPress");
            if (keyCode == 13){
                $scope.submit();
            }
        };

        $scope.submit = function() {
            console.log("in submit");
            $scope.$close();
            $scope.$dismiss();
            loaderModal();

            eventService.onLogin({name:$scope.uname,password:$scope.password});
        };
    }])
})();