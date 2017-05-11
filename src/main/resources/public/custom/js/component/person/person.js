(function(){
        angular.module('person',[]);
})();

(function(){
    'use strict';

    angular.module('person')
        .controller('personController',['$scope','$state','dataResources','modal', 
            function ($scope, $state, dataResources, modal) {

                var templatePath = "pages/fragment/person/";
                var busy = false;
                var portion = 0;
                var mvm = $scope.$parent.mvm;
                var vm = this;

                vm.loadData = loadData;
                vm.addPerson = addPerson;
                vm.editPerson = editPerson;
                vm.deletePerson = deletePerson;
                vm.clear = clear;
                vm.apply = apply;
                vm.showOrders = showOrders;
                vm.openFilter = openFilter;
                vm.applyKeyPress = applyKeyPress;
                vm.getTemplateUrl = getTemplateUrl;
                
                vm.customers = [];
                vm.filter = {fio:null, phone:null, email:null, limit:30, offset:0};
                
                var confirmedFilter = angular.copy(vm.filter);

                vm.scrolling = {stopLoad:false, allDataLoaded:false, infiniteDistance: 2};
                vm.filterInUse = false;

                /* Получение данных */
                function loadData(isClean){
                    if(!vm.scrolling.stopLoad && !busy){
                        busy = true;

                        dataResources.customer.all(confirmedFilter).$promise.then(function(data){

                            if(data.length < confirmedFilter.limit){
                                vm.scrolling.stopLoad = true;
                            }

                            if(isClean){
                                vm.customers = [];
                            }

                            vm.customers = vm.customers.concat(data);

                            portion++;
                            confirmedFilter.offset = portion * confirmedFilter.limit;
                            vm.scrolling.allDataLoaded = true;
                            busy = false;
                        });
                    }
                }

                // очистка фильтра
                function clear() {
                    portion = 0;
                    vm.filterInUse = false;
                    vm.filter = {fio:null, phone:null, email:null, limit:30, offset:0};
                    confirmedFilter = angular.copy(vm.filter);
                    // удалим старый фильтр
                    localStorage.removeItem($state.current.name);
                    vm.scrolling.stopLoad = false;
                    loadData(true);
                }

                // подтверждение фильтра
                function apply() {
                    portion = 0;
                    vm.filterInUse = true;
                    vm.filter.offset = portion * vm.filter.limit;
                    confirmedFilter = angular.copy(vm.filter);
                    // запомним фильтр
                    localStorage.setItem($state.current.name, angular.toJson(confirmedFilter));
                    vm.scrolling.stopLoad = false;
                    loadData(true);
                }

                // редактирование клиента
                function editPerson(id) {
                    $state.transitionTo("person.detail", {id:id});
                }

                // удаление клиента
                function deletePerson(id) {
                    console.log("delete&")
                    dataResources.customer.delete({id: id});
                    var currPerson = helpers.findInArrayById(vm.customers, id);
                    var idx = vm.customers.indexOf(currPerson);
                    vm.customers.splice(idx, 1);
                }

                // Добавление клиента
                function addPerson() {
                    $state.transitionTo("person.detail");
                }

                // перейти на страницу с заказами выбранного клиента
                function showOrders(id) {
                    $state.transitionTo("order", {customerId: id});
                }

                function applyKeyPress(keyCode) {
                    if (keyCode == 13) {
                        apply();
                    }
                }

                // модальное окно фильтрации
                function openFilter(wClass) {
                    var dialog = modal({
                        templateUrl: "pages/modal/persons-filter.html",
                        className: 'ngdialog-theme-default ' + wClass,
                        closeByEscape: true,
                        closeByDocument: true,
                        scope: vm
                    });
                
                    dialog.closePromise.then(function (output) {
                        if (output.value && output.value != '$escape') {}
                    });
                }

                // получение шаблона страницы
                function getTemplateUrl(){
                    if(mvm.width < 601){
                        return templatePath + "person-sm.html"
                    }
                    if(mvm.width > 600){
                        if(mvm.width < 961){
                            return templatePath + "person-md.html"
                        }
                        return templatePath + "person-lg.html"
                    }
                }

        }])

        .controller('personDetailController',['$scope','$state','dataResources','person','companies','$mdToast','$rootScope','$stateParams', 
            function ($scope, $state, dataResources, person, companies,$mdToast, $rootScope, $stateParams) {
                
                var templatePath = "pages/fragment/person/card/";

                var mvm = $scope.$parent.mvm;
                var vm = this;

                vm.save = save;
                vm.getTemplateUrl = getTemplateUrl;
                vm.afterInclude = afterInclude;
                
                vm.person = person ? person : {};
                vm.companies = companies;
                vm.showHints = true;

                if(vm.person.companyId && vm.person.companyId != null){
                    vm.person.isEmployer = true;
                } else {
                    vm.person.companyId = null;
                }

                function save() {
                    vm.showHints = false;

                    function refreshState(data){
                        vm.personCard.$setPristine();
                        /* refresh state because name can be changed */
                        $state.go($state.current,{id:data.result},{notify:false}).then(function(){
                            vm.person.id = data.result;
                            $stateParams.id = data.result;
                            $rootScope.$broadcast('$refreshBreadcrumbs',$state);
                        });
                    }

                    if(vm.personCard.$dirty){
                        if(vm.personCard.$valid){
                            var fullName = vm.person.firstName + ' ' + vm.person.middleName + ' '+ vm.person.lastName;
                            if(vm.person.id){
                                dataResources.customer.put(vm.person).$promise.then(function(data){
                                    $mdToast.show($rootScope.toast.textContent('Клиент ['+ fullName +'] успешно изменён').theme('success'));
                                    refreshState({result:vm.person.id});
                                }, function(error){
                                    $mdToast.show($rootScope.toast.textContent('Неудалось сохранить изменения').theme('error'));
                                })
                            } else {
                                dataResources.customer.post(vm.person).$promise.then(function(data){
                                    $mdToast.show($scope.toast.textContent('Клиент ['+ fullName +'] успешно создан').theme('success'));
                                    refreshState({result:data.id});
                                }, function(error){
                                    $mdToast.show($rootScope.toast.textContent('Неудалось создать нового клиента').theme('error'));
                                })
                            }
                            vm.showHints = true;
                        } else {
                            vm.showHints = false;
                        }
                    }
                }

                function getTemplateUrl(){
                    if(mvm.width < 601){
                        return templatePath + "person-card-sm.html";
                    } else {
                        return templatePath + "person-card-lg.html";
                    }
                }

                // callback загрузки шаблона страницы
                function afterInclude(){}
        }])
})();
