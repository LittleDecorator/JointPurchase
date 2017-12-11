(function(){
    angular.module('sale',[]);
})();

(function() {
    'use strict';

    angular.module('sale')
        .controller('saleController', ['$scope', '$rootScope','$state', '$mdToast', 'dataResources', 'resolved',
            function ($scope, $rootScope, $state, $mdToast, dataResources, resolved) {

                var busy = false;
                var portion = 0;
                var mvm = $scope.$parent.mvm;
                var vm = this;

                vm.loadData = loadData;
                vm.clear = clear;
                vm.apply = apply;
                vm.getTemplateUrl = getTemplateUrl;
                vm.toCard = toCard;

                vm.sales = [];
                vm.filter = {limit:30, offset:0};
                vm.showHints = true;
                vm.forms = {saleForm:{}};
                var confirmedFilter = angular.copy(vm.filter);

                vm.scrolling = {stopLoad:false, allDataLoaded:false, infiniteDistance: 2};

                /* Получение данных */
                function loadData(isClean){
                    if(!vm.scrolling.stopLoad && !busy){
                        busy = true;

                        dataResources.sale.all(confirmedFilter).$promise.then(function(data){

                            if(data.length < confirmedFilter.limit){
                                vm.scrolling.stopLoad = true;
                            }

                            if(isClean){
                                vm.sales = [];
                            }

                            vm.sales = vm.sales.concat(data);

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
                    vm.filter.offset = portion * vm.filter.limit;
                    confirmedFilter = angular.copy(vm.filter);
                    // запомним фильтр
                    localStorage.setItem($state.current.name, angular.toJson(confirmedFilter));
                    vm.scrolling.stopLoad = false;
                    loadData(true);
                }

                function toCard(id){
                    mvm.goto('sale.detail',{id: id});
                }

                // получение шаблона страницы
                function getTemplateUrl(){
                    var result = "pages/fragment/sale/";
                    if(mvm.width < 601){
                        return result + "sale-sm.html"
                    }
                    if(mvm.width > 600){
                        if(mvm.width < 961){
                            return result + "sale-md.html"
                        }
                        return result + "sale-lg.html"
                    }
                    return result;
                }
            }
        ])

        .controller('saleCardController', ['$scope', '$rootScope','$state', '$mdToast', 'dataResources', 'sale',
            function ($scope, $rootScope, $state, $mdToast, dataResources, sale) {
                var mvm = $scope.$parent.mvm;
                var vm = this;

                vm.init = init;
                vm.removeItem = removeItem;
                vm.addItem = addItem;
                vm.getTemplateUrl = getTemplateUrl;

                vm.forms = {saleCard:{}};
                vm.sale = sale;

                function init(){
                    // dataResources.sale.get({email:subjectEmail}).$promise.then(function(data){
                    //     vm.wishlist.items = data;
                    // }, function(error){
                    // });
                }

                function removeItem(itemId) {
                    // dataResources.wishlist.core.delete({email:vm.wishlist.email, id: itemId}).$promise.then(function(data){
                    // }, function(error){
                    // });
                }

                function addItem(itemId) {
                    // dataResources.wishlist.core.delete({email:vm.wishlist.email, id: itemId}).$promise.then(function(data){
                    // }, function(error){
                    // });
                }

                // получение шаблона страницы
                function getTemplateUrl(){
                    var result = "pages/fragment/sale/card/";
                    if(mvm.width < 601){
                        return result + "sale-card-sm.html"
                    }
                    if(mvm.width > 600){
                        if(mvm.width < 961){
                            return result + "sale-card-md.html"
                        }
                        return result + "sale-card-lg.html"
                    }
                    return result;
                }

                init();
            }
        ])
})();