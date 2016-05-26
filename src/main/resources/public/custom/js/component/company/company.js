(function(){
    angular.module('company',[]);
})();

(function(){
    'use strict';

    angular.module('company')
        .controller('companyController',['$scope','$state','dataResources', function ($scope, $state, dataResources) {

            $scope.companies = dataResources.company.query();
            console.log($scope.companies);

            //create new company
            $scope.addCompany = function () {
                $state.transitionTo("company.detail");
            };

            //show company details
            $scope.edit = function (id) {
                $state.go("company.detail",{id:id});
            };

            $scope.delete = function (id) {
                dataResources.company.delete({id: id});
                var company = helpers.findInArrayById($scope.companies, id);
                var idx = $scope.companies.indexOf(company);
                $scope.companies.splice(idx, 1);
            };

        }])
        .controller('companyDetailController',['$scope','$state','company','dataResources', function ($scope, $state, company, dataResources) {

            $scope.company = company;

            $scope.save = function () {
                dataResources.company.save($scope.company);
            };

        }])
})();