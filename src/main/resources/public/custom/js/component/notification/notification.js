(function(){
    angular.module('notification',[]);
})();

(function(){
    'use strict';

    angular.module('notification')
        .controller('notificationController',['$rootScope','$scope','$state','dataResources','modal',
            function ($rootScope, $scope, $state, dataResources, modal){

                var templatePath = "pages/fragment/notification/";
                var busy = false;
                var portion = 0;
                var mvm = $scope.$parent.mvm;
                var vm = this;

                vm.loadData = loadData;
                vm.clear = clear;
                vm.apply = apply;
                vm.openFilter = openFilter;
                vm.getTemplateUrl = getTemplateUrl;
                vm.showNotification = showNotification;
                vm.checkNotification = checkNotification;

                vm.notifications = [];
                vm.filter = {limit:30, offset:0};

                var confirmedFilter = angular.copy(vm.filter);

                vm.scrolling = {stopLoad:false, allDataLoaded:false, infiniteDistance: 2};
                vm.filterInUse = false;

                /* Получение данных */
                function loadData(isClean){
                    if(!vm.scrolling.stopLoad && !busy){
                        busy = true;

                        dataResources.notification.core.all(confirmedFilter).$promise.then(function(data){

                            if(data.length < confirmedFilter.limit){
                                vm.scrolling.stopLoad = true;
                            }

                            if(isClean){
                                vm.notifications = [];
                            }

                            vm.notifications = vm.notifications.concat(data);

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
                    vm.filter = {limit:30, offset:0};
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
                        if (output.value && output.value !== '$escape') {}
                    });
                }

                // получение шаблона страницы
                function getTemplateUrl(){
                    if(mvm.width < 601){
                        return templatePath + "notifications-sm.html"
                    }
                    if(mvm.width > 600){
                        if(mvm.width < 961){
                            return templatePath + "notifications-md.html"
                        }
                        return templatePath + "notifications-lg.html"
                    }
                }

                function showNotification(id){
                    $state.go("notification.detail", {id:id});
                }

                function checkNotification(notification){
                    if(!notification.viewed){
                        dataResources.notification.core.post({notificationId:notification.id},{}).$promise.then(function(data){
                            showNotification(notification.id)
                        })
                    } else {
                        showNotification(notification.id);
                    }
                }

            }])
        .controller('notificationCardController',['$scope','$state','dataResources','modal','notification', '$filter',
            function ($scope, $state, dataResources, modal, notification, $filter){

                var templatePath = "pages/fragment/notification/card/";

                var mvm = $scope.$parent.mvm;
                var vm = this;

                vm.getTemplateUrl = getTemplateUrl;
                vm.afterInclude = afterInclude;
                vm.init = init;
                vm.showTarget = showTarget;
                
                function init(){
                    vm.notification = notification ? notification : {};
                    
                    vm.notification.dateAdd = $filter('date')(vm.notification.dateAdd,'dd.MM.yyyy HH:mm:ss');
                    vm.notification.viewDate = $filter('date')(vm.notification.viewDate,'dd.MM.yyyy HH:mm:ss');
                }
                
                function getTemplateUrl(){
                    if(mvm.width < 601){
                        return templatePath + "notification-card-sm.html";
                    } if(mvm.width > 600){
                        if(mvm.width < 961){
                            return templatePath + "notification-card-md.html"
                        }
                        return templatePath + "notification-card-lg.html"
                    }
                }

                function showTarget(){
                    var name = vm.notification.targetResource.toLowerCase() + ".detail";
                    mvm.goto(name, {id:vm.notification.targetId});
                }

                // callback загрузки шаблона страницы
                function afterInclude(){}
                
                init();
                
            }])
})();