(function(){
    angular.module('company',[]);
})();

(function(){
    'use strict';

    angular.module('company')
        .controller('companyController',['$scope','$state','dataResources', function ($scope, $state, dataResources) {

            var templatePath = "pages/fragment/company/";
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

            $scope.getTemplate = function(){
                if($scope.width < 601){
                    return templatePath + "company-sm.html"
                }
                if($scope.width > 600){
                    if($scope.width < 961){
                        return templatePath + "company-md.html"
                    }
                    return templatePath + "company-lg.html"
                }
            };

        }])
        .controller('companyDetailController',['$scope','$state','company','dataResources', function ($scope, $state, company, dataResources) {

            var templatePath = "pages/fragment/company/card/";
            
            $scope.company = company;

            $scope.save = function () {
                dataResources.company.save($scope.company);
            };

            $scope.getTemplateUrl = function(){
                if($scope.width < 601){
                    return templatePath + "company-card-sm.html";
                } else {
                    return templatePath + "company-card-lg.html";
                }
            };

            $scope.afterInclude = function(){

            };

        }])
})();