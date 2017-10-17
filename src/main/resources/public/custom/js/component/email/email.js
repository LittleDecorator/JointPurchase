(function(){
    angular.module('email',[]);
})();

(function() {
    'use strict';

    angular.module('email')

        .controller('emailController', ['$rootScope','$scope','dataResources','$timeout', '$mdToast',
            function($rootScope, $scope, dataResources, $timeout, $mdToast) {
                var mvm = $scope.$parent.mvm;
                var vm = this;
        
                vm.send = send;
        
                vm.forms = {};
                vm.request = { to:null, subject:null, body:null };
                vm.showHints = true;
        
                /**
                 * Отправка сообщения клиенту от нас
                 */
                function send(){
                    vm.showHints = false;
                    if(vm.forms.emailForm.recipient.$valid && vm.forms.emailForm.subject.$valid){
                        dataResources.email.post(vm.request, function(data){
                            $mdToast.show($rootScope.toast.textContent('Ваше сообщение успешно отправлено').theme('success'));
                            vm.showHints = true;
                        }, function(error){
                            $mdToast.show($rootScope.toast.textContent('Неудалось отправить сообщение').theme('error'));
                        });
                        vm.request = { to:null, subject:null, body:null };
                    }
                }
        }])

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
