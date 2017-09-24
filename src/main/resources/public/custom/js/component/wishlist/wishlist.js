(function(){
    angular.module('wishlist',[]);
})();

(function() {
    'use strict';

    angular.module('wishlist')
        .controller('wishlistController', ['$scope', '$rootScope','$state', '$mdToast', 'dataResources', 'resolved',
            function ($scope, $rootScope, $state, $mdToast, dataResources, resolved) {

                var toast = $mdToast.simple().position('top right').hideDelay(3000);
                var busy = false;
                var portion = 0;
                var mvm = $scope.$parent.mvm;
                var vm = this;

                vm.loadData = loadData;
                vm.clear = clear;
                vm.apply = apply;
                // vm.openFilter = openFilter;
                vm.showClientWishlist = showClientWishlist;
                vm.getTemplateUrl = getTemplateUrl;
                vm.addToList = addToList;
                

                vm.wishlists = [];
                vm.filter = {limit:30, offset:0};
                vm.showHints = true;
                vm.forms = {wishList:{}};
                var confirmedFilter = angular.copy(vm.filter);

                vm.scrolling = {stopLoad:false, allDataLoaded:false, infiniteDistance: 2};
                vm.filterInUse = false;
                vm.stashed = {id: null, itemId: null, subjectId: null, email: null};

                /* Получение данных */
                function loadData(isClean){
                    if(!vm.scrolling.stopLoad && !busy){
                        busy = true;

                        dataResources.wishlist.core.all(confirmedFilter).$promise.then(function(data){

                            if(data.length < confirmedFilter.limit){
                                vm.scrolling.stopLoad = true;
                            }

                            if(isClean){
                                vm.wishlists = [];
                            }

                            vm.wishlists = vm.wishlists.concat(data);

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
                
                function addToList(){
                    if(vm.forms.wishList.$dirty){
                        if(vm.forms.wishList.$valid){
                            console.log(resolved);
                            console.log($rootScope.currentUser)
                            vm.stashed.itemId = resolved.id;
                            console.log(vm.stashed);
                            dataResources.wishlist.core.post(vm.stashed).$promise.then(function(data){
                                $scope.closeThisDialog(data);
                            }, function(error){
                                $mdToast.show(toast.textContent('Неудалось добавить товар в список желаемого').theme('error'));
                            });
                            vm.showHints = true;
                        } else {
                            vm.showHints = false;
                        }
                    }
                }

                function showClientWishlist(email){
                    mvm.goto('wishlist.detail',{email: email});
                }

                // получение шаблона страницы
                function getTemplateUrl(){
                    var result = "pages/fragment/wishlist/";
                    if(mvm.width < 601){
                        return result + "wishlists-sm.html"
                    }
                    if(mvm.width > 600){
                        if(mvm.width < 961){
                            return result + "wishlists-md.html"
                        }
                        return result + "wishlists-lg.html"
                    }
                    return result;
                }
            }
        ])

        .controller('wishlistCardController', ['$scope', '$rootScope','$state', '$mdToast', 'dataResources', 'list',
            function ($scope, $rootScope, $state, $mdToast, dataResources, list) {
                var mvm = $scope.$parent.mvm;
                var vm = this;

                vm.init = init;
                vm.removeItem = removeItem;
                vm.getTemplateUrl = getTemplateUrl;

                vm.forms = {wishlistCard:{}};
                vm.wishlist = list;

                function init(subjectEmail){
                    dataResources.wishlist.core.get({email:subjectEmail}).$promise.then(function(data){
                        vm.wishlist.items = data;
                    }, function(error){
                    });
                }

                function removeItem(itemId) {
                    dataResources.wishlist.core.delete({email:vm.wishlist.email, id: itemId}).$promise.then(function(data){
                    }, function(error){
                    });
                }

                // получение шаблона страницы
                function getTemplateUrl(){
                    var result = "pages/fragment/wishlist/card/";
                    if(mvm.width < 601){
                        return result + "wishlist-card-sm.html"
                    }
                    if(mvm.width > 600){
                        if(mvm.width < 961){
                            return result + "wishlist-card-md.html"
                        }
                        return result + "wishlist-card-lg.html"
                    }
                    return result;
                }

                init();
            }
        ])
})();