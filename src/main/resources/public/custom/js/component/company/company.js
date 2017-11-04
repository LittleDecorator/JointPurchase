(function(){
    angular.module('company',[]);
})();

(function(){
    'use strict';

    angular.module('company')
        .controller('companyController',['$scope','$state','dataResources','$mdToast','$rootScope', 
            function ($scope, $state, dataResources, $mdToast, $rootScope) {
                
                var templatePath = "pages/fragment/company/";
                var mvm = $scope.$parent.mvm;
                var vm = this;
            
                vm.addCompany = addCompany;
                vm.edit = edit;
                vm.deleteCompany = deleteCompany;
                vm.getTemplateUrl = getTemplateUrl;
            
                vm.companies = dataResources.company.query();
            
                /**
                * Создать новую компанию
                */
                function addCompany() {
                    $state.transitionTo("company.detail");
                }

                /**
                * Перейти в карточку
                * @param id
                */
                function edit(id) {
                    $state.go("company.detail",{id:id});
                }

                /**
                * Удаление компании
                * @param id
                */
                function deleteCompany(id) {
                    var company = helpers.findInArrayById(vm.companies, id);
                    dataResources.company.delete({id: id}).$promise.then(function(data){
                        var idx = vm.companies.indexOf(company);
                        vm.companies.splice(idx, 1);
                        $mdToast.show($rootScope.toast.textContent('Поставщик '+ company.name +' успешно удален').theme('success'));
                    }, function(error){
                        console.log(error);
                        $mdToast.show($rootScope.toast.textContent('Не удалось удалить  поставщика '+ company.name).theme('error'));
                    });
                }

                /**
                * Получение шаблона
                * @returns {string}
                */
                function getTemplateUrl(){
                    if(mvm.width < 601){
                        return templatePath + "company-sm.html"
                    }
                    if(mvm.width > 600){
                        if(mvm.width < 961){
                            return templatePath + "company-md.html"
                        }
                        return templatePath + "company-lg.html"
                    }
                }
            }])
        
        .controller('companyDetailController',['$rootScope','$scope','$state','company','dataResources','$mdToast', 
            function ($rootScope,$scope, $state, company, dataResources,$mdToast) {

                var templatePath = "pages/fragment/company/card/";
                var last = { bottom: false, top: true, left: false, right: true };
                var mvm = $scope.$parent.mvm;
                var vm = this;
                
                vm.getToastPosition = getToastPosition;
                vm.save = save;
                vm.showGallery = showGallery;
                vm.getTemplateUrl = getTemplateUrl;
                vm.afterInclude = afterInclude;
                vm.showSimpleToast = showSimpleToast;
                
                vm.forms = {};
                vm.company = company;
                vm.showHints = true;
                vm.toastPosition = angular.extend({},last);
                
                function getToastPosition() {
                    return Object.keys(vm.toastPosition).filter(function (pos) {
                        return vm.toastPosition[pos];
                    }).join(' ');
                }

                /* переход в галерею */
                function showGallery() {
                    console.log("showGallery")
                    $state.go("company.detail.gallery", {id: vm.company.id});
                }

                function save() {
                    var toast = $mdToast.simple().position('top right').hideDelay(3000);
                    if(vm.forms.companyCard.$dirty){
                        if(vm.forms.companyCard.$valid){
                            if(vm.company && vm.company.id){
                                dataResources.company.put(vm.company).$promise.then(function(data){
                                    $mdToast.show(toast.textContent('Поставщик '+ vm.company.name +' успешно изменён').theme('success'));
                                },function(error){
                                    $mdToast.show(toast.textContent('Не удалось изменить поставщика '+ vm.company.name).theme('error'));
                                });
                            } else {
                                dataResources.company.post(vm.company).$promise.then(function(data){
                                    $mdToast.show(toast.textContent('Поставщик '+ vm.company.name +' успешно создан').theme('success'));
                                    vm.forms.companyCard.$setPristine();
                                    $state.go($state.current,{id:data.result},{notify:false}).then(function(){
                                        $rootScope.$broadcast('$refreshBreadcrumbs',$state);
                                    });
                                },function(error){
                                    console.log(error);
                                    $mdToast.show(toast.textContent('Не удалось создать поставщика '+ vm.company.name).theme('error'));
                                });
                            }
                            vm.showHints = true;
                        } else {
                            vm.showHints = false;
                        }
                    }
                }

                function getTemplateUrl(){
                    if(mvm.width < 601) {
                        return templatePath + "company-card-sm.html";
                    }
                    if(mvm.width < 961){
                        return templatePath + "company-card-md.html";
                    } else {
                        return templatePath + "company-card-lg.html";
                    }
                }

                function afterInclude(){
                }

                function showSimpleToast() {
                    $mdToast.show(
                        $mdToast.simple()
                            .textContent('Simple Toast!')
                            .position(getToastPosition())
                            .hideDelay(3000)
                    );
                }

        }])

        .controller('companyToastController', function($scope, $mdToast) {
            $scope.closeToast = function() {
                $mdToast.hide();
            };
        });
})();