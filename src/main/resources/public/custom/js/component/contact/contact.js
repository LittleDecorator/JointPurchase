(function(){
    angular.module('contact',[]);
})();

(function() {
    'use strict';

    angular.module('contact')

        .controller('contactController',['$scope','dataResources','$timeout', function($scope,dataResources, $timeout){

            var templatePath = "pages/fragment/contact/";
            $scope.request = {phone:null,message:null};

            $scope.showHints = true;

            $scope.send = function(){
                console.log($scope);
                console.log($scope.contactForm.$error);
                if($scope.contactForm.phone.$valid){
                    console.log('BLA')
                } else {
                    console.log('FLA')
                }
                //    $scope.showHints = false;
                //} else {
                //    dataResources.contactCallback.post($scope.request,
                //        function(response){
                //            console.log("success");
                //        },
                //        function(response){
                //            console.log("failed");
                //        });
                //    $scope.request = {phone:null,message:null};
                //    $scope.showHints = true;
                //}
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
                }
                if($scope.width > 600){
                    if($scope.width < 1025){
                        return templatePath + "contact-md.html"
                    }
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