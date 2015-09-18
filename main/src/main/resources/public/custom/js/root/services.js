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
                console.log(instance);
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

        .service('store',['$window',function ($window) {
            return {
                get: function (key) {
                    if ($window.localStorage [key]) {
                        console.log("in get");
                        //var cart = angular.fromJson($window.localStorage [key]);
                        var cart = $window.localStorage [key];
                        console.log(cart);
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
                dataResources.customer.get({id:id},function(data){
                    deferred.resolve(data);
                });
                return deferred.promise;
            };

            this.getOrder = function(id){
                var deferred = $q.defer();
                dataResources.order.get({id:id},function (data) {
                    deferred.resolve(data);
                });
                return deferred.promise;
            };

            this.getCompany = function(id){
                var deferred = $q.defer();
                dataResources.company.get({id:id},function(data){
                    deferred.resolve(data);
                });
                return deferred.promise;
            };

            this.getProduct = function(itemId){
                var deferred = $q.defer();
                dataResources.itemDetail.get({id: itemId},function(data){
                    deferred.resolve(data);
                });
                return deferred.promise;
            }
        }])

        .service('eventService',['$rootScope',function($rootScope){
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
                }
            }
        }])
})();