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
                if(params.className){
                    className = params.className;
                }
                if(!params.resolver){
                    params.resolver = null;
                }

                if(params.closeByEscape != undefined){
                    closeByEscape = params.closeByEscape;

                }

                return ngDialog.open({
                    template: params.templateUrl,
                    className: className,
                    controller: params.controller,
                    showClose: false,
                    closeByEscape: closeByEscape,
                    closeByDocument:false,
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

        .service('itemClssModal', ['ngDialog','$rootScope',function (ngDialog, $rootScope) {

            return function(data) {
                return ngDialog.open({
                    templateUrl:'pages/modal/itemModal.html',
                    className: 'ngdialog-theme-default',
                    controller: 'itemClssController',
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

        .service('authService',['$rootScope',function($rootScope) {
            return {
                isAdmin: function () {
                    if (typeof $rootScope.currentUser != 'undefined') {
                        return $rootScope.currentUser.isAdmin;
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
            console.log("in resolver");
            
            this.getItemDetail = function(id){
                console.log("Get Item Detail");
                var getItemPromise = function() {
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

                var getCategoryPromise = function() {
                    var deferred = $q.defer();
                    dataResources.categoryMap.get(function(res) {
                        var categories = [];
                        angular.forEach(res, function (category) {
                            categories.push({id:category.id,name:category.name});
                        });
                        console.log(categories);
                        deferred.resolve(categories);
                    });
                    return deferred.promise;
                };

                var getCompanyPromise = function() {
                    var deferred = $q.defer();
                    dataResources.companyMap.get(function(res){
                        var companies = [];
                        angular.forEach(res, function (comp) {
                            companies.push(comp);
                        });
                        console.log(companies);
                        deferred.resolve(companies);
                    });
                    return deferred.promise;
                };
                return $q.all([getItemPromise(),getCategoryPromise(),getCompanyPromise()]);
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

            this.getCompanyMap = function(){
                var deferred = $q.defer();
                dataResources.companyMap.get(function(res){
                    deferred.resolve(res);
                });
                return deferred.promise;
            };

            this.getCategoryTreeData = function(){
                var deferred = $q.defer();
                dataResources.categoryTree.get().$promise.then(function(data){
                    console.log(data);
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
                dataResources.itemDetail.get({id: itemId},function(data){
                    console.log(data);
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
                },
                onSearchHide: function(){
                    $rootScope.$broadcast('onSearchHide');
                }
            }
        }])
})();