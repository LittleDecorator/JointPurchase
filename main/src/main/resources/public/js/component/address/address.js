(function(){
    angular.module('address',[]);
})();

(function() {
    'use strict';

    angular.module('address')

        .controller('addressController',['$scope','dataResources','$timeout',function($scope,dataResources,$timeout){
            console.log("in addressController");

            $scope.$watch('isInited', function handleFooChange( newValue, oldValue ) {
                if(newValue){
                    $timeout(function() {
                        $scope.load();
                    }, 300);
                }
            }, true);

            $scope.load = function(){
                console.log("in load");
                var map;

                DG.then(function () {
                    map = DG.map('map', {
                        center: [55.793763, 37.392595],
                        zoom: 17
                    });

                    DG.marker([55.794376, 37.392815]).addTo(map).bindPopup('Вы кликнули по мне!');
                });

            }

        }])
})();