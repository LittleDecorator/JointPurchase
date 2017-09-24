(function(){
    angular.module('loader',[]);
})();

(function(){
    angular.module('loader')
        .controller('loaderController', ['$scope','eventService','loaderModal','$rootScope', function ($scope,eventService,loaderModal,$rootScope) {
            $scope.$on('onComplete',function(){
                $scope.$close();
            });
        }])
})();

