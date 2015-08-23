(function(){
    angular.module('company',[]);
})();

(function(){
    'use strict';

    angular.module('company')
        .controller('companyController',['$scope','$state','company','dataResources', function ($scope, $state, company, dataResources) {
            console.log("Enter company controller");

            $scope.company = {};

            if (company) {
                $scope.company = company;
            } else {
                $scope.companies = dataResources.company.query();
            }

            //create new company
            $scope.addCompany = function () {
                $state.transitionTo("company.detail");
            };

            //show company details
            $scope.edit = function (id) {
                $state.transitionTo("company.detail",{id:id});
            };

            $scope.save = function () {
                dataResources.company.save($scope.company);
            };

            $scope.delete = function (id) {
                dataResources.company.delete({id: id});
                var company = helpers.findInArrayById($scope.companies, id);
                var idx = $scope.companies.indexOf(company);
                $scope.companies.splice(idx, 1);
            };

        }]);
})();