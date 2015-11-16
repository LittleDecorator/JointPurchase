(function(){
    angular.module('email',[]);
})();

(function() {
    'use strict';

    angular.module('email')
        .controller('emailController',function() {
            $('ul.tabs').tabs();
        })

        .controller('inboxController',['$scope','dataResources',function($scope,dataResources) {
            dataResources.inboxMail.get(
                function(data){
                    console.log("success");
                    angular.forEach(data, function (comp) {
                        comp.date = helpers.dateTimeFormat(comp.date);
                    });
                    $scope.inbox = data;

                },
                function(){
                    console.log("failed");
                }
            )
        }])

        .controller('sendController',['$scope','dataResources',function($scope,dataResources) {
            dataResources.sendMail.get(
                function(data){
                    console.log("success");
                    $scope.send = data;
                },
                function(){
                    console.log("failed");
                }
            )
        }])
})();
