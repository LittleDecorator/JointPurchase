(function(){
        angular.module('person',[]);
})();

(function(){
    'use strict';

    angular.module('person')
        .controller('personController',['$scope','$state','dataResources','modal', function ($scope, $state, dataResources, modal) {

            var templatePath = "pages/fragment/person/";
            
            $scope.customers = [];

            $scope.filter = {fio:null, phone:null, email:null, limit:30, offset:0};
            var confirmedFilter = angular.copy($scope.filter);

            var busy = false;
            var portion = 0;

            $scope.scrolling = {stopLoad:false, allDataLoaded:false, infiniteDistance: 2};
            $scope.filterInUse = false;

            /* Получение данных */
            $scope.loadData = function(isClean){
                if(!$scope.scrolling.stopLoad && !busy){
                    busy = true;

                    dataResources.customer.all(confirmedFilter).$promise.then(function(data){

                        if(data.length < confirmedFilter.limit){
                            $scope.scrolling.stopLoad = true;
                        }

                        if(isClean){
                            $scope.customers = [];
                        }

                        $scope.customers = $scope.customers.concat(data);

                        portion++;
                        confirmedFilter.offset = portion * confirmedFilter.limit;
                        $scope.scrolling.allDataLoaded = true;
                        busy = false;
                    });
                }
            };

            // очистка фильтра
            $scope.clear = function () {
                portion = 0;
                $scope.filterInUse = false;
                $scope.filter = {fio:null, phone:null, email:null, limit:30, offset:0};
                confirmedFilter = angular.copy($scope.filter);
                // удалим старый фильтр
                localStorage.removeItem($state.current.name);
                $scope.scrolling.stopLoad = false;
                $scope.loadData(true);
            };

            // подтверждение фильтра
            $scope.apply = function () {
                portion = 0;
                $scope.filterInUse = true;
                $scope.filter.offset = portion * $scope.filter.limit;
                confirmedFilter = angular.copy($scope.filter);
                // запомним фильтр
                localStorage.setItem($state.current.name, angular.toJson(confirmedFilter));
                $scope.scrolling.stopLoad = false;
                $scope.loadData(true);
            };

            // редактирование клиента
            $scope.editPerson = function (id) {
                $state.transitionTo("person.detail", {id:id});
            };

            // удаление клиента
            $scope.deletePerson = function (id) {
                dataResources.customer.delete({id: id});
                var currPerson = helpers.findInArrayById($scope.customers, id);
                var idx = $scope.customers.indexOf(currPerson);
                $scope.customers.splice(idx, 1);
            };

            // Добавление клиента
            $scope.addPerson = function () {
                $state.transitionTo("person.detail");
            };

            // перейти на страницу с заказами выбранного клиента
            $scope.showOrders = function (id) {
                $state.transitionTo("order", {customerId: id});
            };

            // модальное окно фильтрации
            $scope.openFilter = function (wClass) {
                var dialog = modal({
                    templateUrl: "pages/modal/persons-filter.html",
                    className: 'ngdialog-theme-default ' + wClass,
                    closeByEscape: true,
                    closeByDocument: true,
                    scope: $scope
                });
                dialog.closePromise.then(function (output) {
                    if (output.value && output.value != '$escape') {
                    }
                });
            };

            // получение шаблона страницы
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

        .controller('personDetailController',['$scope','$state','dataResources','person','companies','$mdToast','$rootScope','$stateParams', function ($scope, $state, dataResources, person, companies,$mdToast, $rootScope, $stateParams) {
            var templatePath = "pages/fragment/person/card/";
            $scope.person = person ? person : {};
            $scope.companies = companies;
            $scope.showHints = true;

            if($scope.person.companyId && $scope.person.companyId != null){
                $scope.person.isEmployer = true;
            } else {
                $scope.person.companyId = null;
            }

            $scope.getTemplateUrl = function(){
                if($scope.width < 601){
                    return templatePath + "person-card-sm.html";
                } else {
                    return templatePath + "person-card-lg.html";
                }
            };

            $scope.save = function () {

                console.log($scope);
                $scope.showHints = false;

                function refreshState(data){
                    console.log(data);
                     $scope.personCard.$setPristine();
                     /* refresh state because name can be changed */
                     $state.go($state.current,{id:data.result},{notify:false}).then(function(){
                         $scope.person.id = data.result;
                         $stateParams.id = data.result;
                         $rootScope.$broadcast('$refreshBreadcrumbs',$state);
                     });
                }

                if($scope.personCard.$dirty){
                    if($scope.personCard.$valid){
                        var fullName = $scope.person.firstName + ' ' + $scope.person.middleName + ' '+ $scope.person.lastName;
                        if($scope.person.id){
                             dataResources.customer.put($scope.person).$promise.then(function(data){
                                 $mdToast.show($rootScope.toast.textContent('Клиент ['+ fullName +'] успешно изменён').theme('success'));
                                 refreshState({result:$scope.person.id});
                             }, function(error){
                                 $mdToast.show($rootScope.toast.textContent('Неудалось сохранить изменения').theme('error'));
                             })
                        } else {
                             dataResources.customer.post($scope.person).$promise.then(function(data){
                                 $mdToast.show($scope.toast.textContent('Клиент ['+ fullName +'] успешно создан').theme('success'));
                                 refreshState({result:data.id});
                             }, function(error){
                                 $mdToast.show($rootScope.toast.textContent('Неудалось создать нового клиента').theme('error'));
                             })
                        }
                        $scope.showHints = true;
                    } else {
                        $scope.showHints = false;
                    }
                }
            };


        }])
})();
