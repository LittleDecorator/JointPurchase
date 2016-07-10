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

        .controller('personDetailController',['$scope','$state','dataResources','person','companies','$mdToast', function ($scope, $state, dataResources, person, companies,$mdToast) {
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
                // if ($scope.selectedCompany != null) {
                //     $scope.current.companyId = $scope.selectedCompany.id;
                // }
                // dataResources.customer.save($scope.current);


                var toast = $mdToast.simple().position('top right').hideDelay(3000);

                function refreshState(data){
                    // $scope.itemCard.$setPristine();
                    // /* refresh state because name can be changed */
                    // $state.go($state.current,{id:data.result},{notify:false}).then(function(){
                    //     $scope.selected.id = data.result;
                    //     $rootScope.$broadcast('$refreshBreadcrumbs',$state);
                    // });
                }

                if($scope.personCard.$dirty){
                    if($scope.personCard.$valid){
                        if($scope.selected.id){
                            // dataResources.item.put($scope.selected).$promise.then(function(data){
                            //     $mdToast.show(toast.textContent('Товар ['+ $scope.selected.name +'] успешно изменён').theme('success'));
                            //     refreshState({result:$scope.selected.id});
                            // }, function(error){
                            //     $mdToast.show(toast.textContent('Неудалось сохранить изменения').theme('error'));
                            // })
                        } else {
                            // dataResources.item.post($scope.selected).$promise.then(function(data){
                            //     $mdToast.show(toast.textContent('Товар ['+ $scope.selected.name +'] успешно создан').theme('success'));
                            //     refreshState(data);
                            // }, function(error){
                            //     $mdToast.show(toast.textContent('Неудалось создать новый товар').theme('error'));
                            // })
                        }
                        $scope.showHints = true;
                    } else {
                        $scope.showHints = false;
                    }
                }
            };


        }])
})();
