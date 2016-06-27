(function(){
    angular.module('contact',[]);
})();

(function() {
    'use strict';

    angular.module('contact')

        .controller('contactController',['$scope','dataResources','$timeout', function($scope,dataResources, $timeout){

            var templatePath = "pages/fragment/contact/";
            $scope.request = {phone:null,message:null};

            $scope.send = function(){
                console.log("send pressed");
                dataResources.contactCallback.post($scope.request,
                    function(response){
                        console.log("success");
                    },
                    function(response){
                        console.log("failed");
                    });
                $scope.request = {phone:null,message:null};
            };

            $scope.load = function(){
                console.log("in load");
                var map;

                DG.then(function () {
                    map = DG.map('map', {
                        center: [55.794430, 37.392815],
                        zoom: 15
                    });

                    DG.marker([55.794388, 37.392799]).addTo(map).bindPopup('Вы нашли нас!');
                });

            };

            $scope.getTemplateUrl = function(){
                if($scope.width < 601){
                    return templatePath + "contact-sm.html";
                } else {
                    return templatePath + "contact-lg.html";
                }
            };

            $scope.afterInclude = function(){
                //$scope.load();
                $timeout(function() {
                    $scope.load();
                }, 300);
            };
        }])
})();