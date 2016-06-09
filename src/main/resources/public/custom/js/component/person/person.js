(function(){
        angular.module('person',[]);
})();

(function(){
    'use strict';

    angular.module('person')
        .controller('personController',['$scope','$state','dataResources', function ($scope, $state, dataResources) {

            var templatePath = "pages/fragment/person/";
            
            $scope.customers = dataResources.customer.query();

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

            $scope.getTemplateUrl = function(){
                if($scope.width < 601){
                    return templatePath + "person-sm.html"
                }
                if($scope.width > 600){
                    if($scope.width < 961){
                        return templatePath + "person-md.html"
                    }
                    return templatePath + "person-lg.html"
                }
            };

        }])

        .controller('personDetailController',['$scope','$state','dataResources','person','companies', function ($scope, $state, dataResources, person, companies) {
            var templatePath = "pages/fragment/person/card/";
            $scope.person = {};
            $scope.companies = companies;
            $scope.companies.unshift({id:null,name:'Выберите компанию'});

            if(person){
                $scope.person = person;
                $scope.person.isEmployer = ($scope.person.companyId != null);
            }

            $scope.getTemplateUrl = function(){
                if($scope.width < 601){
                    return templatePath + "person-card-sm.html";
                } else {
                    return templatePath + "person-card-lg.html";
                }
            };

            $scope.afterInclude = function(){
                $('select').material_select();
            };

        }])
})();
