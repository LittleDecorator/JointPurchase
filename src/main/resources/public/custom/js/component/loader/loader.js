(function(){
    angular.module('loader',[]);
})();

(function(){
    angular.module('loader')
        .controller('loaderController', ['$scope','eventService','loaderModal','$rootScope', function ($scope,eventService,loaderModal,$rootScope) {
            console.log("in loader controller");
            console.log($scope);

            $scope.$on('onComplete',function(){
                $scope.$close();
            });
        }])
})();

