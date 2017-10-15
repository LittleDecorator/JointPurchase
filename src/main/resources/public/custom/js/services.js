(function(){
    angular.module('purchase.services',[]);
})();

(function(){
    'use strict';

    angular.module('purchase.services')

        .service('modal', ['$rootScope','ngDialog','resolveService', function ($rootScope,ngDialog,resolveService) {
            return function(params) {
                var className = 'ngdialog-theme-default';
                var closeByEscape = true;
                var closeByDocument = false;
                var showClose = false;
                if(params.className){
                    className = params.className;
                }
                if(!params.resolver){
                    params.resolver = null;
                }
                if(params.showClose){
                   showClose = params.showClose;
                }
                if(params.closeByEscape != undefined){
                    closeByEscape = params.closeByEscape;
                }

                if(params.closeByDocument != undefined){
                    closeByDocument = params.closeByDocument;
                }

                return ngDialog.open({
                    template: params.templateUrl,
                    className: className,
                    controller: params.controller,
                    scope:params.scope,
                    showClose: showClose,
                    closeByEscape: closeByEscape,
                    // closeByNavigation: true,
                    closeByDocument: closeByDocument,
                    resolve: {
                        resolved: function getData() {
                            return params.data;
                        },
                        additional: function(){
                            return params.resolver;
                        }
                    }
                });
            };

        }])
        
        .service('fileUploadModal',['ngDialog','$rootScope', function (ngDialog,$rootScope){
            return function(data, scope) {
                return ngDialog.open({
                    templateUrl:'pages/modal/fileUploadModal.html',
                    className: 'ngdialog-theme-default',
                    controller: 'uploadController',
                    showClose: false,
                    closeByEscape: true,
                    closeByDocument:false,
                    scope:scope,
                    resolve: {
                        resolved: function getData() {
                            return data
                        }
                    }
                });
            };
        }])

        .service('categoryClssModal', ['ngDialog','$rootScope', function (ngDialog,$rootScope) {

            return function(data) {
                return ngDialog.open({
                    templateUrl:'pages/modal/categoryModal.html',
                    className: 'ngdialog-theme-default',
                    controller: 'categoryClssController',
                    showClose: true,
                    closeByEscape: true,
                    closeByDocument:false,
                    resolve: {
                        resolved: function getData() {
                            return data
                        }
                    }
                });
            };

        }])

        .service('itemClssModal', ['ngDialog',function (ngDialog) {

            return function(data, wClass) {
                return ngDialog.open({
                    templateUrl:'pages/modal/itemModal.html',
                    className: 'ngdialog-theme-default ' + wClass,
                    controller: 'itemClssController as vm',
                    showClose: false,
                    closeByEscape: true,
                    closeByDocument:false,
                    resolve: {
                        resolved: function getData() {
                            return data
                        }
                    }
                });
            };
        }])

        .service('preorderModal', ['ngDialog','$rootScope', function (ngDialog,$rootScope) {

            return function(data) {
                return ngDialog.open({
                    templateUrl:'pages/modal/preorderModal.html',
                    className: 'ngdialog-theme-default',
                    controller: 'preorderController',
                    showClose: true,
                    closeByEscape: true,
                    closeByDocument:false,
                    resolve: {
                        resolved: function getData() {
                            return data
                        }
                    }
                });
            };

        }])

        .service('store',['$window',function ($window) {
            return {
                get: function (key) {
                    if ($window.localStorage [key]) {
                        //var cart = angular.fromJson($window.localStorage [key]);
                        var value = $window.localStorage [key];
                        //console.log(cart);
                        return JSON.parse(value);
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

        .service('authService',['$rootScope',function($rootScope) {
            return {
                isAdmin: function () {
                    if (typeof $rootScope.currentUser != 'undefined') {
                        return $rootScope.currentUser.role == "admin" || $rootScope.currentUser.role == "root";
                    } else {
                        return false;
                    }
                },

                isAuth: function () {
                    if (localStorage.getItem('token')) {
                    }
                    if (localStorage.getItem('token') == undefined) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }
        }])

        .service('resolveService',['$q','dataResources',function($q,dataResources){

            this.getItemDetail = function(id){
                var deferred = $q.defer();
                if(id){
                    dataResources.item.get({id:id},function(item){
                        deferred.resolve(item);
                    });
                } else {
                    deferred.resolve({});
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

            this.getPersonal = function(){
                var deferred = $q.defer();
                dataResources.customerPrivate.get(function(data){
                    deferred.resolve(data);
                });
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
                    deferred.resolve({});
                }
                return deferred.promise;
            };

            this.getCompanyMap = function(){
                var deferred = $q.defer();
                dataResources.companyMap.get(function(res){
                    res.unshift({id:null,name:"Выберите компанию..."});
                    deferred.resolve(res);
                });
                return deferred.promise;
            };

            this.getPersonMap = function(){
                var deferred = $q.defer();
                dataResources.personMap.get(function(res){
                    deferred.resolve(res);
                });
                return deferred.promise;
            };

            this.getItemStatusMap = function(){
                var deferred = $q.defer();
                dataResources.itemStatusMap.get(function(res){
                    deferred.resolve(res);
                });
                return deferred.promise;
            };

            this.getOrderStatusMap = function(){
                var deferred = $q.defer();
                dataResources.orderStatusMap.get(function(res){
                    deferred.resolve(res);
                });
                return deferred.promise;
            };

            this.getDeliveryMap = function(){
                var deferred = $q.defer();
                dataResources.deliveryMap.get(function(res){
                    deferred.resolve(res);
                });
                return deferred.promise;
            };

            this.getOrderItems = function(id){
                if(id){
                    var deferred = $q.defer();
                    dataResources.orderItems.get({id:id},function(res){
                        deferred.resolve(res);
                    });
                    return deferred.promise;
                } else {
                    return null;
                }
            };

            this.getCategoryTreeData = function(){
                var deferred = $q.defer();
                dataResources.categoryTree.get().$promise.then(function(data){
                    deferred.resolve(data);
                });
                return deferred.promise;
            };

            this.getCategoryMap = function(){
                var deferred = $q.defer();
                dataResources.categoryRootMap.get(function(res){
                    deferred.resolve(res);
                });
                return deferred.promise;
            };

            this.getCategory = function(id){
                if(id){
                    var deferred = $q.defer();
                    dataResources.category.get({id:id},function(res){
                        deferred.resolve(res);
                    });
                    return deferred.promise;
                } else {
                    return null;
                }
            };
            
            this.getCategoryItems = function(id){
                if(id){
                    var deferred = $q.defer();
                    dataResources.categoryItems.get({id:id},function(res){
                        deferred.resolve(res);
                    });
                    return deferred.promise;
                } else {
                    return null;
                }

            };

            this.getProduct = function(itemId){
                var deferred = $q.defer();
                dataResources.catalog.itemDetail.get({id: itemId},function(data){
                    deferred.resolve(data);
                });
                return deferred.promise;
            };

            this.getNewNotifications = function(){
                var deferred = $q.defer();
                dataResources.notification.newCount.get(function (data) {
                    deferred.resolve(data);
                });
                return deferred.promise;
            };

            this.getWishlistCount = function(){
                var deferred = $q.defer();
                dataResources.wishlist.count.get(function (data) {
                    deferred.resolve(data);
                });
                return deferred.promise;
            };

            this.getWishlist = function(email){
                var deferred = $q.defer();
                dataResources.wishlist.core.get({email: email},function (data) {
                    deferred.resolve(data);
                });
                return deferred.promise;
            };

            this.getNotification = function(id){
                var deferred = $q.defer();
                dataResources.notification.core.get({notificationId: id},function(data){
                    deferred.resolve(data);
                });
                return deferred.promise;
            };
        }])

        .service('eventService',['$rootScope','$state',function($rootScope,$state){
            return {
                data:{},
                onLogin: function(data){
                    this.data = data;
                    $rootScope.$broadcast('onLogin');
                },
                onComplete: function(){
                    $rootScope.$broadcast('onComplete');
                    //перейдем на страницу откуда быва вызвана авторизация
                    $state.transitionTo(helpers.findRouteByUrl($rootScope.oldLocation.substring(1)));
                },
                onFilter: function(data){
                    this.data = data;
                    $rootScope.$broadcast('onFilter');
                },
                onCategoryClssSelected: function(data){
                    this.data = data;
                    $rootScope.$broadcast('onCategoryClssSelected');
                },
                onItemClssSelected: function(data){
                    this.data = data;
                    $rootScope.$broadcast('onItemClssSelected');
                },
                onSearchHide: function(){
                    $rootScope.$broadcast('onSearchHide');
                },
                onSideHide: function(data){
                    this.data = data;
                    $rootScope.$broadcast('onSideHide');
                },
            }
        }])

        .service('cryptoService',['$rootScope',function(){

            var rkEncryptionKey = CryptoJS.enc.Base64.parse('ZQiPJvvwFlfa9IxXj+F+eJCST+XvFr6nWYS0rloQZdQ=');
            var rkEncryptionIv = CryptoJS.enc.Base64.parse('ksgfrrfixQ4xLk/qV5CmRg==');

            this.encryptString = function(stringToEncrypt){
                var encrypted = CryptoJS.AES.encrypt(stringToEncrypt, rkEncryptionKey, {mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: rkEncryptionIv});
                return encrypted.ciphertext.toString(CryptoJS.enc.Base64);
            }

        }])
})();