(function(){
        angular.module('person',[]);
})();

(function(){
    'use strict';

    angular.module('person')
        .controller('personController',['$scope','$state','person','dataResources', function ($scope, $state, person, dataResources) {
            console.log("Enter person controller");
            //TODO: делать неактивным кнопку показа заказов клиента, если их нет
            //TODO: возможно стоит показывать в таблице кол-во заказов

            $scope.current = {};
            $scope.companyNames = dataResources.companyMap.get();
            $scope.selectedCompany = {};
            if(person){
                $scope.current = person;
                if ($scope.current.companyId != null) {
                    $scope.selectedCompany = helpers.findInArrayById($scope.companyNames, $scope.current.companyId);
                    $scope.current.isEmployer = true;
                } else {
                    $scope.current.isEmployer = false;
                }
            } else {
                $scope.customers = dataResources.customer.query();
            }

            $scope.editPerson = function (id) {
                $state.transitionTo("person.detail",{id:id});
            };

            $scope.deletePerson = function (id) {
                dataResources.customer.delete({id: id});
                var currPerson = helpers.findInArrayById($scope.customers, id);
                var idx = $scope.customers.indexOf(currPerson);
                $scope.customers.splice(idx, 1);
            };

            $scope.addPerson = function () {
                $state.transitionTo("person.detail");
            };

            //show customer orders, pass id via route
            $scope.showOrders = function (id) {
                $state.transitionTo("order", {customerId: id});
            };

            $scope.save = function () {
                if ($scope.selectedCompany != null) {
                    $scope.current.companyId = $scope.selectedCompany.id;
                }
                dataResources.customer.save($scope.current);
            };

        }]);
})();
