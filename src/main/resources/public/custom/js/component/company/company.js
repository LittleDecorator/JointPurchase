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
                var company = helpers.findInArrayById($scope.companies, id);
                dataResources.company.delete({id: id}).$promise.then(function(data){
                    var idx = $scope.companies.indexOf(company);
                    $scope.companies.splice(idx, 1);
                    Materialize.toast('Поставщик '+ company.name +' успешно удален',3000,'fine');
                }, function(error){
                    console.log(error);
                    Materialize.toast('Не удалось удалить  поставщика '+ company.name ,3000,'error');
                });
            };

            $scope.getTemplateUrl = function(){
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
        .controller('companyDetailController',['$rootScope','$scope','$state','company','dataResources', function ($rootScope,$scope, $state, company, dataResources) {

            var templatePath = "pages/fragment/company/card/";
            
            $scope.company = company;

            $scope.save = function () {
                console.log($scope);
                console.log($scope.company);
                if($scope.company && $scope.company.id){
                    dataResources.company.put($scope.company).$promise.then(function(data){
                        Materialize.toast('Поставщик '+ $scope.company.name +' успешно изменён',3000,'fine');
                    },function(error){
                        Materialize.toast('Не удалось изменить поставщика '+ $scope.company.name ,3000,'error');
                    });
                } else {
                    dataResources.company.post($scope.company).$promise.then(function(data){
                        Materialize.toast('Поставщик '+ $scope.company.name +' успешно создан',3000,'fine');
                        $scope.companyCard.$setPristine();
                        $state.go($state.current,{id:data.result},{notify:false}).then(function(){
                            $rootScope.$broadcast('$refreshBreadcrumbs',$state);
                        });
                    },function(error){
                        console.log(error);
                        Materialize.toast('Не удалось создать поставщика '+ $scope.company.name ,3000,'error');
                    });
                }
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