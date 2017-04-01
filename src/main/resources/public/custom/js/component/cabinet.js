(function(){
    angular.module('cabinet',[]);
})();

(function() {
    'use strict';

    angular.module('cabinet')
        .controller('cabinetController',['$scope','$state','$stateParams','dataResources','$timeout','statusMap','deliveryMap','modal','$mdToast','$rootScope',
            function ($scope, $state, $stateParams, dataResources, $timeout, statusMap, deliveryMap, modal, $mdToast,$rootScope) {

            var templatePath = "pages/fragment/cabinet/";
            // история заказов
            $scope.history = [];
            // персональная информация    
            $scope.person = null;
            // вспомогательные мапы     
            $scope.statuses = statusMap;
            $scope.deliveries = deliveryMap;
            $scope.showHints = true;
            
            // ищем сохраненый фильтр в storage
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

            /* получение шаблона страницы */
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

            /* Положить фильтр в хранилище */
            $scope.remember = function(idx){
                $scope.filter.selectedTab = idx;
                localStorage.setItem($state.current.name,angular.toJson($scope.filter));
            };

            /* Получение персональных данных */
            $scope.getPerson = function(){
                dataResources.customerPrivate.get().$promise.then(function(data){
                    $scope.person = data;
                }, function(error){
                    console.log(error);
                });
            };

            /* Сохранить изменения */
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

            /* Изменение пароля клиента */
            $scope.changePassword = function(){
                var dialog = modal({templateUrl:"pages/modal/passwordModal.html",className:'ngdialog-theme-default small-width',closeByEscape:true,controller:"passwordModalController",data:null});
                dialog.closePromise.then(function(output) {
                    if(output.value && output.value != '$escape'){
                        $mdToast.show($scope.toast.textContent('ваш пароль успешно изменён').theme('success'));
                    }
                });
            };

            /* Удаление учетной записи */
            $scope.deleteAccount = function(){
                //TODO: показать окно подтверждения. Удалить учетку и все что с ней связанно.
            };

            /* Просмотр заказа */
            $scope.viewOrder = function(id){
                $state.go("cabinet.historyDetail",{id:id});
            };

            /* отменяем заказ */
            $scope.cancelOrder = function(id){
                console.log("cabinetController");
                var order = helpers.findInArrayById($scope.history, id);
                if(order.status!='canceled' || order.status!='done'){
                    dataResources.orderCancel.put({id: id}).$promise.then(function(data){
                        if(data.status){
                            order.status = data.status;
                            $mdToast.show($scope.toast.textContent('Ваш заказ успешно отменён').theme('success'));
                        } else {
                            if(data.status != null){
                                $mdToast.show($scope.toast.textContent('Не удалось отменить заказ').theme('error'));
                            }
                        }
                    });
                }
            };

            $timeout(function(){
                $scope.apply();
                $scope.getPerson();
            },50)


        }])

        /* Контроллер иодального окна изменения пароля */
        .controller('passwordModalController',['$scope','dataResources','resolved','cryptoService','$mdToast',function($scope,dataResources,resolved,cryptoService,$mdToast){

            $scope.showHints = true;

            /* изменить пароль */
            $scope.change = function() {
                $scope.showHints = false;
                if($scope.pswChange.$valid){
                    dataResources.authChange.post({oldPassword:$scope.oldPassword, newPassword:cryptoService.encryptString($scope.newPassword)}).$promise.then(function(data){
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

        /* Контроль заказа из истории пользователя */
        .controller('cabinetHistoryDetailController',['$scope','order','items','deliveryMap', 'dataResources', '$mdToast',
            function ($scope, order, items, deliveryMap, dataResources, $mdToast) {

                var templatePath = "pages/fragment/cabinet/history/";

                // получим заказ
                $scope.order = order;
                // получим способы доставки
                $scope.order.delivery = helpers.findInArrayById(deliveryMap, $scope.order.delivery)
                // получим товары из заказа
                $scope.items = items.map(function(element){
                    var item = element.item;
                    item.count = element.count;
                    return item
                });

                /* получение шаблона страницы */
                $scope.getTemplateUrl = function(){
                    if($scope.width < 601){
                        return templatePath + "history-sm.html"
                    }
                    if($scope.width > 600){
                        if($scope.width < 1025){
                            return templatePath + "history-md.html"
                        }
                        return templatePath + "history-lg.html"
                    }
                };

                $scope.cancelOrder = function(id){
                    console.log("cabinetHistoryDetailController");
                    if($scope.order.status!='canceled' || $scope.order.status!='done'){
                        dataResources.orderCancel.put({id: id}).$promise.then(function(data){
                            if(data.status){
                                $scope.order.status = data.status;
                                $mdToast.show($scope.toast.textContent('Ваш заказ успешно отменён').theme('success'));
                            } else {
                                if(data.status!= null){
                                    $mdToast.show($scope.toast.textContent('Не удалось отменить заказ').theme('error'));
                                }
                            }
                        });
                    }
                };

            }])
})();