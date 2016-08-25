(function(){
    angular.module('purchase.factories',[]);
})();

(function(){
    'use strict';

    angular.module('purchase.factories')
        .factory('authInterceptor',['$rootScope','$q','$injector',function ($rootScope, $q,$injector) {
            return {
                request: function (config) {
                    if (localStorage.getItem('token')) {
                        config.headers.Authorization = 'Bearer ' + localStorage.getItem('token');
                        config.headers.Expires = localStorage.getItem('Expires');
                    }
                    return config;
                },

                response: function(response) {
                    var time = response.headers('Expires');
                    if(time && time!== 'null'){
                        localStorage.setItem('Expires',time);
                    }
                    return response;
                },

                responseError: function (response) {
                    if(response.status === 401) {
                        localStorage.removeItem('token');
                        localStorage.removeItem('menus');
                        localStorage.removeItem('subMenus');
                        $rootScope.currentUser = {};
                        $rootScope.menus = [];
                        $rootScope.subMenus = [];
                        $injector.get('$state').transitionTo('login');
                        return $q.reject(response);
                    }
                    else {
                        return $q.reject(response);
                    }
                }

            };
        }])

        .factory('dataResources',['$resource','$filter',function($resource,$filter){
            return {

                catalog: {
                    list : $resource('/catalog',{},{
                        all:{method:'POST',isArray:true}
                    }),
                    search : $resource('/catalog/search/',{},{
                        get :{method:'GET',isArray:true}
                    }),
                    itemDetail: $resource("/catalog/:id/detail",{},{
                        get:{method:'GET',isArray:false}
                    })
                },

                company: $resource("/company/:id",{},{
                    all: {method:'GET',isArray:true},
                    get: {method:'GET',isArray:false},
                    delete: {method:'DELETE',isArray:false},
                    put: {method:'PUT',isArray:false},
                    post:{method:'POST',isArray:false,transformResponse:function(data, headers){
                        return {result:data}
                    }}
                }),
                itemImage: $resource('/content/items/:id',{},{
                    query :{method:'GET',isArray:true},
                    get:{method:'GET',isArray:true}
                }),
                itemContent: $resource('/content/upload/item',{},{
                    upload:{
                        method:'POST',
                        transformRequest: function(data) { return data; },
                        headers : { 'Content-Type' : undefined },
                        isArray : true
                    }
                }),

                item: $resource('/item/:id',{},{
                    all:{method:'GET',isArray:true},
                    get:{method:'GET',isArray:false},
                    post:{method:'POST',isArray:false,
                        transformRequest:function(data, headers) {
                            var result = angular.copy(data);
                            result.price= result.price.replace(/[^0-9]/g,'');
                            return angular.toJson(result);
                        },
                        transformResponse:function(data, headers){
                            return {result:data}
                        }
                    },
                    put:{method:'PUT',isArray:false,
                        transformRequest:function(data, headers) {
                            var result = angular.copy(data);
                            result.price= result.price.replace(/[^0-9]/g,'');
                            return angular.toJson(result);
                        }
                    }
                }),
                
                filterByCompany:$resource('/item/filter/company',{},{
                    filter:{method:'GET',isArray:true}
                }),
                filterByCategory:$resource('/item/filter/category',{},{
                    filter:{method:'GET',isArray:true}
                }),

                inboxMail:$resource('/mail/inbox',{},{get:{method:'GET',isArray:true}}),
                sendMail:$resource('/mail/send',{},{get:{method:'GET',isArray:true}}),

                categoryItems: $resource('/category/:id/items',{},{ get : {method:'GET',isArray:true}}),

                companyMap: $resource('/company/map',{},{ get : { method: 'GET', isArray : true }}),
                categoryMap: $resource('/category/map',{},{get : { method: 'GET', isArray : true }}),
                typeMap:$resource('category/type/map',{},{
                    get:{method:'GET',isArray:true}
                }),
                categoryTree: $resource("/category/tree",{},{
                    get:{method:'GET',isArray:true},
                    post:{method:'POST',isArray:false}
                }),
                categoryTypes:$resource("/category/types/:id",{},{
                    get:{method:'GET',isArray:true},
                    update:{method:'POST',isArray : false},
                    delete:{method:'DELETE',isArray:false}
                }),
                categoryMenu:$resource("/category/side/menu",{},{
                    get:{method:'GET',isArray:true}
                }),
                categoryRootMap:$resource("/category/map/roots",{},{
                    get:{method:'GET',isArray:true}
                }),
                categorySubList:$resource("/category/:id/sub",{},{
                    get:{method:'GET',isArray:true}
                }),
                category:$resource("/category/:id",{},{
                    get:{method:'GET',isArray:false},
                    post:{method:'POST',isArray:false},
                    put:{method:'PUT',isArray:false},
                    delete:{method:'DELETE',isArray:false}
                }),
                

                //itemFilter:$resource("/item/filter",{},{apply :{method:'POST',isArray:true}}),
                itemDetail: $resource("/item/:id/detail",{},{get:{method:'GET',isArray:false}}),

                authLogin: $resource('/auth/login',{},{
                    post:{method:'POST'}
                }),
                authChange: $resource('/auth/change',{},{
                    post:{
                        method:'POST',
                        isArray:false,
                        transformResponse: function(data){
                            return {result:angular.fromJson(data)}
                        }
                    }
                }),

                personMap: $resource('/customer/map',{},{get : { method:'GET',isArray: true}}),
                itemMap: $resource('/item/map',{},{get:{method:'GET',isArray:true}}),
                image: $resource('/content/remove',{},{remove:{method:'DELETE'}}),
                order:$resource('/order/:id',{},{
                    all:{method:'GET',isArray:true},
                    get:{method:'GET',isArray:false},
                    post:{method:'POST',isArray:false, transformRequest:function(data){
                        data.order.recipientPhone = data.order.recipientPhone.replace(/[^0-9]/g,'');
                        console.log(data.order);
                        return angular.toJson(data);
                    }},
                    delete:{method:'DELETE',isArray:false}
                }),
                orderHistory: $resource('/order/history',{},{all:{method:'GET',isArray:true}}),
                orderPrivate:$resource('/order/personal',{},{post:{method:'POST',isArray:false}}),
                orderItems: $resource('/order/:id/items',{},{get: {method:'GET',isArray:true}}),
                orderByCustomerId: $resource('/order/customer/:id',{},{get : { method: 'GET', isArray : true }}),
                orderedItem: $resource('/order/:orderId/item/:itemId',{},{
                    delete: {method:'DELETE'},
                    update: {method:'POST'}
                }),
                contactCallback: $resource('/contact/callback/sms',{},{post:{method:'POST',isArray:false}}),
                customer: $resource('/customer/:id',{},{
                        all:{method:'GET',isArray:true},
                        get:{method:'GET',isArray:false},
                        put:{method:'PUT',isArray:false},
                        post:{method:'POST',isArray:false},
                        delete:{method:'DELETE',isArray:false}
                    }
                ),
                customerPrivate: $resource('/customer/private'),

                previewItems: $resource('/item/preview',{},{
                    filter:{method:'POST',isArray:false}
                }),

                galleryShow: $resource('content/set/show',{},{
                    toggle:{method:'PUT',isArray:false}
                }),
                galleryMain: $resource('content/set/main',{},{
                    toggle:{method:'PUT',isArray:false}
                }),
                notForSale:$resource('item/set/sale',{},{
                    toggle:{method:'POST',isArray:false}
                }),
                itemStatusMap:$resource('clss/item/status/map',{},{
                    get:{method:'GET',isArray:true,transformResponse: function(data){
                        var result = [];
                        console.log(data);
                        angular.forEach(angular.fromJson(data), function(value, key){
                            console.log(key);
                            console.log(value);
                            result.push({id:key,value:value})
                        });
                        console.log(result);
                        return result;
                    }}
                }),
                orderStatusMap:$resource('clss/order/status/map',{},{
                    get:{method:'GET',isArray:true,transformResponse: function(data){
                        var result = [{id:null,value:"Выберите статус заказа ..."}];
                        angular.forEach(angular.fromJson(data), function(value, key){
                            result.push({id:key,value:value})
                        });
                        return result;
                    }}
                }),
                deliveryMap:$resource('clss/order/delivery',{},{
                    get:{method:'GET',isArray:true,transformResponse: function(data){
                        //var result = [{id:null,value:"Укажите тип доставки ..."}];
                        var result = [];
                        angular.forEach(angular.fromJson(data), function(value){
                            result.push({id:value.id,value:value.name,hint:value.hint})
                        });
                        return result;
                    }}
                })

            }
        }])

        .factory('factoryModal',['$http', '$compile', '$rootScope', '$document', '$q', '$controller', '$timeout',
            function modal($http, $compile, $rootScope, $document, $q, $controller, $timeout) {
                var service = {
                    open: open
                };

                function open(options) {
                    /// <summary>Opens a modal</summary>
                    /// <param name="options" type="Object">
                    /// ? title {string} The title of the modal.<br />
                    /// ? scope {$scope} The scope to derive from. If not passed, the $rootScope is used<br />
                    /// ? params {object} Objects to pass to the controller as $modalInstance.params<br />
                    /// ? template {string} The HTML of the view. Overriden by @templateUrl<br />
                    /// ? templateUrl {string} The URL of the view. Overrides @template<br />
                    /// ? fixedFooter {boolean} TRUE if the modal should have a fixed footer<br />
                    /// ? controller {string||array||function} A controller definition<br />
                    /// ? controllerAs {string} the controller alias for the controllerAs sintax. Requires @controller
                    /// </param>
                    /// <param name="options.title" type="String">The title of the window</param>
                    /// <returns type="$.when" />

                    var deferred = $q.defer();

                    getTemplate(options).then(function (modalBaseTemplate) {
                        var modalBase = angular.element(modalBaseTemplate);

                        var scope = $rootScope.$new(false, options.scope),
                            modalInstance = {
                                params: options.params || {},
                                close: function (result) {
                                    console.log("in close modal");
                                    deferred.resolve(result);
                                    closeModal(modalBase, scope);
                                },
                                dismiss: function (reason) {
                                    console.log("in dismiss modal");
                                    deferred.reject(reason);
                                    closeModal(modalBase, scope);
                                }
                            };

                        scope.$close = modalInstance.close;
                        scope.$dismiss = modalInstance.dismiss;

                        $compile(modalBase)(scope);

                        var openModalOptions = {
                            //ready: function () { alert('Ready'); }, // Callback for Modal open
                            complete: function () { modalInstance.dismiss(); } // Callback for Modal close
                        };

                        runController(options, modalInstance, scope);

                        modalBase.appendTo('body').openModal(openModalOptions);

                    }, function (error) {
                        deferred.reject({ templateError: error });
                    });

                    return deferred.promise;
                }

                function runController(options, modalInstance, scope) {
                    /// <param name="option" type="Object"></param>

                    if (!options.controller) return;

                    var controller = $controller(options.controller, {
                        $scope: scope,
                        $modalInstance: modalInstance
                    });

                    if (angular.isString(options.controllerAs)) {
                        scope[options.controllerAs] = controller;
                    }
                }

                function getTemplate(options) {
                    var deferred = $q.defer();

                    if (options.templateUrl) {
                        $http.get(options.templateUrl).success(function (data) {
                            deferred.resolve(data);
                        }).catch(function (error) {
                            deferred.reject(error);
                        });
                    } else {
                        deferred.resolve(options.template || '');
                    }


                    return deferred.promise.then(function (template) {

                        var cssClass = options.fixedFooter ? 'modal modal-fixed-footer' : 'modal';
                        var html = [];
                        html.push('<div class="' + cssClass + ' ' + options.sizeClass +'">');
                        if (options.title) {
                            html.push('<div class="modal-header">');
                            html.push(options.title);
                            html.push('<a class="grey-text text-darken-2 right" ng-click="$dismiss()">');
                            html.push('<i class="mdi-navigation-close" />');
                            html.push('</a>');
                            html.push('</div>');
                        }
                        html.push(template);
                        html.push('</div>');

                        return html.join('');
                    });
                }

                function closeModal(modalBase, scope) {
                    /// <param name="modalBase" type="jQuery"></param>
                    /// <param name="scope" type="$rootScope.$new"></param>

                    modalBase.closeModal();

                    $timeout(function () {
                        scope.$destroy();
                        modalBase.remove();
                    }, 5000, true);
                }

                return service;
            }])
})();