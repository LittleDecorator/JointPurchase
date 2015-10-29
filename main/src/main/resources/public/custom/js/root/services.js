(function(){
    angular.module('purchase.services',[]);
})();

(function(){
    'use strict';

    angular.module('purchase.services')
        .service('loginModal',['$modal','$rootScope','factoryModal', function ($modal, $rootScope,factoryModal) {
            return function() {
                var instance =
                    factoryModal.open({
                        templateUrl:'pages/template/loginModal.html',
                        controller: 'loginController',
                        sizeClass: 'modal-sm'
                    });
                //console.log(instance);
                return instance;
            }
        }])

        .service('loaderModal', ['$modal','$rootScope','factoryModal', function ($modal, $rootScope,factoryModal) {

            return function() {
                var instance =
                    factoryModal.open({
                        templateUrl:'pages/template/loaderModal.html',
                        controller: 'loaderController',
                        sizeClass: 'modal-xs loader modal-backdrop'
                    });
                return instance;
            };

        }])

        .service('categoryClssModal', ['$modal','$rootScope','factoryModal', function ($modal, $rootScope,factoryModal) {

            return function(data) {
                var instance =
                    factoryModal.open({
                        templateUrl:'pages/template/categoryModal.html',
                        controller: 'categoryClssController',
                        sizeClass: 'modal loader modal-backdrop',
                        fixedFooter: true,
                        params: data
                    });
                return instance;
            };

        }])

        .service('itemClssModal', ['$modal','$rootScope','factoryModal', function ($modal, $rootScope,factoryModal) {

            return function(data) {
                var instance =
                    factoryModal.open({
                        templateUrl:'pages/template/itemModal.html',
                        controller: 'itemClssController',
                        sizeClass: 'modal loader modal-backdrop',
                        fixedFooter: true,
                        params: data
                    });
                return instance;
            };

        }])

        .service('store',['$window',function ($window) {
            return {
                get: function (key) {
                    if ($window.localStorage [key]) {
                        console.log("in get");
                        //var cart = angular.fromJson($window.localStorage [key]);
                        var cart = $window.localStorage [key];
                        //console.log(cart);
                        return JSON.parse(cart);
                        //return cart
                    }
                    //return false;
                    return [];

                },

                remove: function (key) {
                    if ($window.localStorage [key]) {
                        $window.localStorage.removeItem(key);
                        return true;
                    } else {
                        return false;
                    }
                },

                set: function (key, val) {

                    if (val === undefined) {
                        $window.localStorage.removeItem(key);
                    } else {
                        $window.localStorage [key] = angular.toJson(val);
                    }
                    return $window.localStorage [key];
                }
            }
        }])

        .service('authService',['$rootScope','$cookies',function($rootScope,$cookies) {
            console.log("in auth service");
            console.log($rootScope.currentUser);
            return {
                isAdmin: function () {
                    console.log("check isAdmin");
                    if (typeof $rootScope.currentUser != 'undefined') {
                        return $rootScope.currentUser.isAdmin;
                    } else {
                        return false;
                    }
                },

                isAuth: function () {
                    //console.log("check isAuth");
                    if ($cookies.get('token')) {
                        //console.log('token present');
                    } else {
                        //console.log('no token present');
                    }
                    if (typeof $cookies.get('token') == 'undefined') {
                        return false;
                    } else {
                        //$rootScope.currentUser.name;
                        return true;
                    }
                }
            }
        }])

        .service('resolveService',['$q','dataResources',function($q,dataResources){
            console.log("in resolver");
            this.getItem = function(id){
                console.log("in resolver -> get item");
                var deferred = $q.defer();
                if(id){
                    dataResources.item.get({id:id},function(data){
                        deferred.resolve(data);
                    });
                } else {
                    deferred.resolve(null);
                }
                return deferred.promise;
            };

            this.getPerson = function(id){
                var deferred = $q.defer();
                if(id){
                    dataResources.customer.get({id:id},function(data){
                        deferred.resolve(data);
                    });
                } else {
                    deferred.resolve(null);
                }
                return deferred.promise;
            };

            this.getOrder = function(id){
                var deferred = $q.defer();
                if(id){
                    dataResources.order.get({id:id},function (data) {
                        deferred.resolve(data);
                    });
                } else {
                    deferred.resolve(null);
                }
                return deferred.promise;
            };

            this.getCompany = function(id){
                var deferred = $q.defer();
                if(id){
                    dataResources.company.get({id:id},function(data){
                        deferred.resolve(data);
                    });
                } else {
                    deferred.resolve(null);
                }
                return deferred.promise;
            };

            this.getProduct = function(itemId){
                var deferred = $q.defer();
                dataResources.itemDetail.get({id: itemId},function(data){
                    deferred.resolve(data);
                });
                return deferred.promise;
            };
        }])

        .service('eventService',['$rootScope','$state',function($rootScope,$state){
            return {
                data:{},
                onLogin: function(data){
                    console.log("in login broad");
                    this.data = data;
                    $rootScope.$broadcast('onLogin');
                },
                onComplete: function(){
                    console.log("in complete broad");
                    $rootScope.$broadcast('onComplete');
                    //temporary place
                    //$state.transitionTo($rootScope.oldLocation.substring(1));
                    //$state.transitionTo("cart.checkout");
                    $state.transitionTo(helpers.findRouteByUrl($rootScope.oldLocation.substring(1)));
                },
                onFilter: function(data){
                    console.log("in sideMenu broad");
                    this.data = data;
                    $rootScope.$broadcast('onFilter');
                },
                onCategoryClssSelected: function(data){
                    console.log("in clss selected");
                    console.log(data);
                    this.data = data;
                    $rootScope.$broadcast('onCategoryClssSelected');
                },
                onItemClssSelected: function(data){
                    console.log("in clss selected");
                    console.log(data);
                    this.data = data;
                    $rootScope.$broadcast('onItemClssSelected');
                }
            }
        }])
})();