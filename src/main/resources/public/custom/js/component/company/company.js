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
        .controller('companyDetailController',['$rootScope','$scope','$state','company','dataResources','$mdToast', function ($rootScope,$scope, $state, company, dataResources,$mdToast) {

            var templatePath = "pages/fragment/company/card/";
            
            $scope.company = company;
            $scope.showHints = true;

            var last = {
                bottom: false,
                top: true,
                left: false,
                right: true
            };
            $scope.toastPosition = angular.extend({},last);
            $scope.getToastPosition = function() {
                var result = Object.keys($scope.toastPosition).filter(function(pos) { return $scope.toastPosition[pos]; }).join(' ');
                console.log(result);
                return result;
            };

            $scope.save = function () {
                var toast = $mdToast.simple().position('top right').hideDelay(3000);
                if($scope.companyCard.$dirty){
                    if($scope.companyCard.$valid){
                        if($scope.company && $scope.company.id){
                            dataResources.company.put($scope.company).$promise.then(function(data){
                                $mdToast.show(toast.textContent('Поставщик '+ $scope.company.name +' успешно изменён').theme('success'));
                            },function(error){
                                $mdToast.show(toast.textContent('Не удалось изменить поставщика '+ $scope.company.name).theme('error'));
                            });
                        } else {
                            dataResources.company.post($scope.company).$promise.then(function(data){
                                $mdToast.show(toast.textContent('Поставщик '+ $scope.company.name +' успешно создан').theme('success'));
                                $scope.companyCard.$setPristine();
                                $state.go($state.current,{id:data.result},{notify:false}).then(function(){
                                    $rootScope.$broadcast('$refreshBreadcrumbs',$state);
                                });
                            },function(error){
                                console.log(error);
                                $mdToast.show(toast.textContent('Не удалось создать поставщика '+ $scope.company.name).theme('error'));
                            });
                        }
                        $scope.showHints = true;
                    } else {
                        $scope.showHints = false;
                    }
                }
            };

            $scope.getTemplateUrl = function(){
                if($scope.width < 601) {
                    return templatePath + "company-card-sm.html";
                }
                if($scope.width < 961){
                    return templatePath + "company-card-md.html";
                } else {
                    return templatePath + "company-card-lg.html";
                }
            };

            $scope.afterInclude = function(){

            };

            $scope.showSimpleToast = function() {
                $mdToast.show(
                    $mdToast.simple()
                        .textContent('Simple Toast!')
                        .position($scope.getToastPosition())
                        .hideDelay(3000)
                );
            };

            console.log($scope);

        }])

        .controller('companyToastController', function($scope, $mdToast) {
            $scope.closeToast = function() {
                $mdToast.hide();
            };
        });
})();