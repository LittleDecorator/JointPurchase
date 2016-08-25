(function(){
    angular.module('cabinet',[]);
})();

(function() {
    'use strict';

    angular.module('cabinet')
        .controller('cabinetController',['$scope','$state','$stateParams','dataResources','$timeout','statusMap','deliveryMap','modal','$mdToast','$rootScope',
            function ($scope, $state, $stateParams, dataResources, $timeout, statusMap, deliveryMap, modal, $mdToast,$rootScope) {

            var templatePath = "pages/fragment/cabinet/";
            $scope.history = [];
            $scope.person = null;
            $scope.statuses = statusMap;
            $scope.deliveries = deliveryMap;
            $scope.showHints = true;

            if(localStorage.getItem($state.current.name)){
                $scope.filter = angular.fromJson(localStorage.getItem($state.current.name));
            } else {
                $scope.filter = {dateFrom:null, dateTo:null, status:null, limit:30, offset:0, selectedTab:0};
            }

            var confirmedFilter = angular.copy($scope.filter);
            var busy = false;
            var portion = 0;
            $scope.stopLoad=false;
            $scope.allDataLoaded = false;

            $scope.loadData = function(isClean){
                if(!$scope.stopLoad && !busy){
                    busy = true;

                    dataResources.orderHistory.all(confirmedFilter).$promise.then(function(data){

                        if(data.length < confirmedFilter.limit){
                            $scope.stopLoad = true;
                        }

                        if(isClean){
                            $scope.history = [];
                        }

                        $scope.history = data;

                        portion++;
                        confirmedFilter.offset = portion * confirmedFilter.limit;
                        $scope.allDataLoaded = true;
                        busy = false;
                    });
                }
            };

            $scope.clear = function () {
                portion = 0;
                $scope.filter = {dateFrom:null, dateTo:null, status:null, limit:30, offset:0, selectedTab:0};
                confirmedFilter = angular.copy($scope.filter);
                localStorage.removeItem($state.current.name);
                $scope.stopLoad = false;
                $scope.loadData(true);
            };

            $scope.apply = function () {
                portion = 0;
                $scope.filter.offset = portion * $scope.filter.limit;
                confirmedFilter = angular.copy($scope.filter);
                localStorage.setItem($state.current.name,angular.toJson($scope.filter));

                if(confirmedFilter.dateTo!=null) confirmedFilter.dateTo = moment(confirmedFilter.dateTo).endOf('day').toDate();
                if(confirmedFilter.dateFrom!=null) confirmedFilter.dateFrom = moment(confirmedFilter.dateFrom).startOf('day').toDate();
                if(confirmedFilter.status!=null) confirmedFilter.status = confirmedFilter.status.id;

                $scope.stopLoad = false;
                $scope.loadData(true);
            };

            $scope.getTemplateUrl = function(){
                if($scope.width < 601){
                    return templatePath + "cabinet-sm.html"
                }
                if($scope.width > 600){
                    if($scope.width < 1025){
                        return templatePath + "cabinet-md.html"
                    }
                    return templatePath + "cabinet-lg.html"
                }
            };

            $scope.remember = function(idx){
                $scope.filter.selectedTab = idx;
                localStorage.setItem($state.current.name,angular.toJson($scope.filter));
            };

            $scope.getPerson = function(){
                dataResources.customerPrivate.get().$promise.then(function(data){
                    $scope.person = data;
                }, function(error){
                    console.log(error);
                });
            };

            $scope.save = function(){
                $scope.showHints = false;
                console.log($scope);
                if($scope.personInfo.$valid){
                    console.log('All data valid');
                    dataResources.customer.put($scope.person).$promise.then(function(data){
                        $mdToast.show($rootScope.toast.textContent('Ваши данные успешно изменены').theme('success'));
                        $scope.showHints = true;
                    }, function(error){
                        $mdToast.show($rootScope.toast.textContent('Неудалось сохранить изменения').theme('error'));
                    });
                } else {
                    console.log('Some error present')
                }
            };

            $scope.changePassword = function(){
                var dialog = modal({templateUrl:"pages/modal/passwordModal.html",className:'ngdialog-theme-default small-width',closeByEscape:true,controller:"passwordModalController",data:null});
                dialog.closePromise.then(function(output) {
                    if(output.value && output.value != '$escape'){
                        $mdToast.show($scope.toast.textContent('ваш пароль успешно изменён').theme('success'));
                    }
                });
            };

            $timeout(function(){
                $scope.apply();
                $scope.getPerson();
            },50)


        }])

        .controller('passwordModalController',['$scope','dataResources','resolved','cryptoService','$mdToast',function($scope,dataResources,resolved,cryptoService,$mdToast){

            $scope.showHints = true;

            $scope.change = function() {
                $scope.showHints = false;
                if($scope.pswChange.$valid){
                    console.log("Validation passed");
                    dataResources.authChange.post({oldPassword:$scope.oldPassword, newPassword:cryptoService.encryptString($scope.newPassword)}).$promise.then(function(data){
                        console.log(data.result);
                        if(data.result){
                            $scope.closeThisDialog(data);
                        } else {
                            $mdToast.show($scope.toast.textContent('Не удалось изменить пароль').theme('error'));
                        }
                    }, function(error){
                        $mdToast.show($scope.toast.textContent('Не удалось изменить пароль').theme('error'));
                    });
                }
            };

        }])

})();