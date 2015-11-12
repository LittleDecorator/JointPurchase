(function(){
    angular.module('contact',[]);
})();

(function() {
    'use strict';

    angular.module('contact')
        .controller('contactController',['$scope','dataResources', function ($scope,dataResources) {
            console.log("in contactController");
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
            }
        }]);
})();