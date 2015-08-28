(function(){
    angular.module('registration',[]);
})();

(function(){
    'use strict';

    angular.module('registration')
        .factory('registrationResource',['$resource',function($resource){
            return {
                _register: $resource('/auth/register',{},{post:{method:'POST'}}),
                _throughLogin: $resource('/auth/register',{},{post:{method:'POST'}})
            }
        }])

        .controller('registrationController',['$scope','$state','registrationResource', function($scope,$state,registrationResource){

            $scope.card={
                fio:"",
                phone:"",
                mail:"",
                pwd:"",
                repeat:""
            };
            $scope.register = function(){
                registrationResource._register.post($scope.card);
            }
        }])

        .controller('registrationResultController',['$scope','$state','registrationResource','$stateParams', function($scope,$state,registrationResource,$stateParams) {
            console.log("in registrationResultController");
            //TODO: through login, pretty info page
            $scope._registrationStatus = $stateParams.confirmed;
            if($stateParams.confirmed){
                $scope.message = "You can now login and continue your shopping!"
            } else {
                $scope.message = "Your timeout for confirm expired. Please repeat registration process!"
            }
        }])
})();
