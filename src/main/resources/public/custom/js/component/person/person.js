(function(){
        angular.module('person',[]);
})();

(function(){
    'use strict';

    angular.module('person')
        .controller('personController',['$scope','$state','dataResources', function ($scope, $state, dataResources) {

            var templatePath = "pages/fragment/person/";
            
            $scope.customers = dataResources.customer.all();

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
