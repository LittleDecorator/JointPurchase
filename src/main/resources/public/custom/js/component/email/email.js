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
                    angular.forEach(data, function (comp) {
                        comp.date = helpers.dateTimeFormat(comp.date);
                    });
                    $scope.inbox = data;

                },
                function(){
                }
            )
        }])

        .controller('sendController',['$scope','dataResources',function($scope,dataResources) {
            dataResources.sendMail.get(
                function(data){
                    $scope.send = data;
                },
                function(){
                }
            )
        }])
})();
