(function(){
    angular.module('cabinet',[]);
})();

(function() {
    'use strict';

    angular.module('cabinet')
        .controller('cabinetController',['$scope','$state','$stateParams','dataResources','$timeout','statusMap','deliveryMap','modal','$mdToast','$rootScope',
            function ($scope, $state, $stateParams, dataResources, $timeout, statusMap, deliveryMap, modal, $mdToast,$rootScope) {

                var templatePath = "pages/fragment/cabinet/";
                
                var busy = false;
                var portion = 0;
                var confirmedFilter = null;
                
                var mvm = $scope.$parent.mvm;
                var vm = this;
                
                vm.init = init;
                vm.loadData = loadData;
                vm.clear = clear;
                vm.apply = apply;
                vm.getTemplateUrl = getTemplateUrl;
                vm.remember = remember;
                vm.getPerson = getPerson;
                vm.save = save;
                vm.openFilter = openFilter;
                vm.changePassword = changePassword;
                vm.deleteAccount = deleteAccount;
                vm.viewOrder = viewOrder;
                vm.cancelOrder = cancelOrder;

                vm.forms = {};
                // история заказов
                vm.history = [];
                // персональная информация    
                vm.person = null;
                // вспомогательные мапы     
                vm.statuses = statusMap;
                vm.deliveries = deliveryMap;
                vm.showHints = true;
                vm.stopLoad=false;
                vm.allDataLoaded = false;

                /**
                 * Инициализация
                 */
                function init(){
                    // ищем сохраненый фильтр в storage
                    if(localStorage.getItem($state.current.name)){
                        vm.filter = angular.fromJson(localStorage.getItem($state.current.name));
                    } else {
                        vm.filter = {dateFrom:null, dateTo:null, status:null, limit:10, offset:0, selectedTab:0};
                    }
                    apply();
                    getPerson();
                }

                /**
                 * Загрузка данных
                 * @param isClean
                 */
                function loadData (isClean){
                    if(!vm.stopLoad && !busy){
                        busy = true;

                        dataResources.orderHistory.all(confirmedFilter).$promise.then(function(data){
                            if(data.length < confirmedFilter.limit) {
                                vm.stopLoad = true;
                            }  

                            if(isClean){
                                vm.history = [];
                            }

                            vm.history = vm.history.concat(data);

                            portion++;
                            confirmedFilter.offset = portion * confirmedFilter.limit;
                            vm.allDataLoaded = true;
                            busy = false;
                        });
                    }
                }

                /**
                 * Очистка фильтра
                 */
                function clear(){
                    portion = 0;
                    vm.filter = {dateFrom:null, dateTo:null, status:null, limit:10, offset:0, selectedTab:0};
                    confirmedFilter = angular.copy(vm.filter);
                    localStorage.removeItem($state.current.name);
                    vm.stopLoad = false;
                    loadData(true);
                }

                /**
                 * Применение фильтра
                 */
                function apply(){
                    portion = 0;
                    vm.filter.offset = portion * vm.filter.limit;
                    confirmedFilter = angular.copy(vm.filter);
                    localStorage.setItem($state.current.name, angular.toJson(vm.filter));

                    if(confirmedFilter.dateTo!=null) confirmedFilter.dateTo = moment(confirmedFilter.dateTo).endOf('day').toDate();
                    if(confirmedFilter.dateFrom!=null) confirmedFilter.dateFrom = moment(confirmedFilter.dateFrom).startOf('day').toDate();
                    if(confirmedFilter.status!=null) confirmedFilter.status = confirmedFilter.status.id;

                    vm.stopLoad = false;
                    loadData(true);
                }
                
                /**
                 * получение шаблона страницы 
                 * @returns {string}
                 */
                function getTemplateUrl(){
                    if(mvm.width < 601){
                        return templatePath + "cabinet-sm.html"
                    }
                    if(mvm.width > 600){
                        if(mvm.width < 1025){
                            return templatePath + "cabinet-md.html"
                        }
                        return templatePath + "cabinet-lg.html"
                    }
                }
                
                /**
                 * Положить фильтр в хранилище 
                 * @param idx
                 */
                function remember(idx){
                    vm.filter.selectedTab = idx;
                    localStorage.setItem($state.current.name, angular.toJson(vm.filter));
                }
                
                /**
                 * Получение персональных данных 
                 */
                function getPerson(){
                    dataResources.customerPrivate.get().$promise.then(function(data){
                        vm.person = data;
                    }, function(error){
                        console.log(error);
                    });
                }

                /**
                 * Сохранить изменения 
                 */
                function save(){
                    vm.showHints = false;
                    if(vm.personInfo.$valid){
                        dataResources.customer.put(vm.person).$promise.then(function(data){
                            $mdToast.show($rootScope.toast.textContent('Ваши данные успешно изменены').theme('success'));
                            vm.showHints = true;
                        }, function(error){
                            $mdToast.show($rootScope.toast.textContent('Неудалось сохранить изменения').theme('error'));
                        });
                    } else {
                        console.log('Some error present')
                    }
                }

                /**
                 * модальное окно фильтрации
                 * @param wClass
                 */
                function openFilter(wClass) {
                    var dialog = modal({
                        templateUrl: "pages/modal/cabinet-filter.html",
                        className: 'ngdialog-theme-default ' + wClass,
                        closeByEscape: true,
                        closeByDocument: true,
                        scope: $scope
                    });
                    
                    dialog.closePromise.then(function (output) {
                        if (output.value && output.value != '$escape') {}
                    });
                }
                
                /**
                 * Изменение пароля клиента 
                 */
                function changePassword(wClass){
                    var dialog = modal({
                        templateUrl:mvm.width< 601?"pages/fragment/modal/passwordChange.html":"pages/modal/passwordModal.html",
                        className:'ngdialog-theme-default ' + wClass,
                        closeByEscape:true,
                        controller:"passwordModalController as passVm",
                        data:null
                    });
                    
                    dialog.closePromise.then(function(output) {
                        if(output.value && output.value != '$escape'){
                            $mdToast.show(mvm.toast.textContent('ваш пароль успешно изменён').theme('success'));
                        }
                    });
                }
                
                /**
                 * Удаление учетной записи 
                 */
                function deleteAccount(){
                    //TODO: показать окно подтверждения. Удалить учетку и все что с ней связанно.
                }
                
                /**
                 * Просмотр заказа 
                 * @param id
                 */
                function viewOrder(id){
                    $state.go("cabinet.historyDetail",{id:id});
                }
                
                /**
                 * отменяем заказ 
                 * @param id
                 */
                function cancelOrder(id){
                    var order = helpers.findInArrayById(vm.history, id);
                    if(order.status!='canceled' || order.status!='done'){
                        dataResources.orderCancel.put({id: id}).$promise.then(function(data){
                            if(data.status){
                                order.status = data.status;
                                $mdToast.show(mvm.toast.textContent('Ваш заказ успешно отменён').theme('success'));
                            } else {
                                if(data.status != null){
                                    $mdToast.show(mvm.toast.textContent('Не удалось отменить заказ').theme('error'));
                                }
                            }
                        });
                    }
                }

                init();

        }])

        /* Контроллер иодального окна изменения пароля */
        .controller('passwordModalController', ['$scope','dataResources','resolved','cryptoService','$mdToast',
            function($scope, dataResources, resolved, cryptoService, $mdToast){
                
                var mvm = $scope.$parent.mvm;
                var vm = this;

                vm.change = change;

                vm.showHints = true;
                vm.oldPassword=null;
                vm.newPassword = null;
                vm.forms = {};

                /**
                 * изменить пароль 
                 */
                function change () {
                    vm.showHints = false;
                    if(vm.forms.pswChange.$valid){
                        dataResources.authChange.post({oldPassword:vm.oldPassword, newPassword:cryptoService.encryptString(vm.newPassword)}).$promise.then(function(data){
                        if(data.result){
                            vm.closeThisDialog(data);
                        } else {
                            $mdToast.show(mvm.toast.textContent('Не удалось изменить пароль').theme('error'));
                        }
                        }, function(error){
                            $mdToast.show(mvm.toast.textContent('Не удалось изменить пароль').theme('error'));
                        });
                    }
                }

        }])

        /* Контроль заказа из истории пользователя */
        .controller('cabinetHistoryDetailController',['$scope','order','items','deliveryMap', 'dataResources', '$mdToast',
            function ($scope, order, items, deliveryMap, dataResources, $mdToast) {

                var templatePath = "pages/fragment/cabinet/history/";
                var mvm = $scope.$parent.mvm;
                var vm = this;

                vm.getTemplateUrl = getTemplateUrl;
                vm.cancelOrder = cancelOrder;
                // получим заказ
                vm.order = order;
                // получим способы доставки
                vm.order.delivery = helpers.findInArrayById(deliveryMap, vm.order.delivery)
                // получим товары из заказа
                vm.items = items.map(function(element){
                    var item = element.item;
                    item.count = element.count;
                    return item
                });

                /**
                 * получение шаблона страницы
                 * @returns {string}
                 */
                function getTemplateUrl(){
                    if(mvm.width < 601){
                        return templatePath + "history-sm.html"
                    }
                    if(mvm.width > 600){
                        if(mvm.width < 1025){
                            return templatePath + "history-md.html"
                        }
                        return templatePath + "history-lg.html"
                    }
                }

                /**
                 * Отмена заказа
                 * @param id
                 */
                function cancelOrder(id){
                    if(vm.order.status!='canceled' || vm.order.status!='done'){
                        dataResources.orderCancel.put({id: id}).$promise.then(function(data){
                            if(data.status){
                                vm.order.status = data.status;
                                $mdToast.show(mvm.toast.textContent('Ваш заказ успешно отменён').theme('success'));
                            } else {
                                if(data.status!= null){
                                    $mdToast.show(mvm.toast.textContent('Не удалось отменить заказ').theme('error'));
                                }
                            }
                        });
                    }
                }

            }])
})();