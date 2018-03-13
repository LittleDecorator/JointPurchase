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
            var xlsxContentType = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
            var imgContentType = 'image/jpg';
            return {
               cart: $resource('/api/item/refresh',{},{
                  refresh:{method:'PUT', isArray:true}
               }),
                catalog: {
                    list : $resource('/api/catalog',{},{
                        all:{method:'POST',isArray:true}
                    }),
                    search : $resource('/api/catalog/search/',{},{
                        get :{method:'GET',isArray:true}
                    }),
                    itemDetail: $resource("/api/catalog/:name/detail",{},{
                        get:{method:'GET',isArray:false}
                    }),
                    best : $resource('/api/catalog/best',{},{
                        all:{method:'GET',isArray:true}
                    })
                },

                company: $resource("/api/company/:id",{},{
                    all: {method:'GET',isArray:true},
                    get: {method:'GET',isArray:false},
                    delete: {method:'DELETE',isArray:false},
                    put: {method:'PUT',isArray:false,transformResponse:function(data, headers){
                        return {result:data}
                    }},
                    post:{method:'POST',isArray:false,transformResponse:function(data, headers){
                        return {result:data}
                    }}
                }),

                companyByName: $resource("/api/company/detail",{},{
                    get: {method:'GET',isArray:false},
                }),
                companyContent: $resource('/api/content/upload/company',{},{
                    upload:{
                        method:'POST',
                        transformRequest: function(data) { return data; },
                        headers : { 'Content-Type' : undefined },
                        isArray : false
                    }
                }),
                itemImage: $resource('/api/content/items/:id',{},{
                    query :{method:'GET',isArray:true},
                    get:{method:'GET',isArray:true}
                }),
                companyImage: $resource('/api/content/company/:id',{},{
                    query : {method:'GET', isArray:false,},
                    get:{method:'GET',isArray:false},
                }),

               saleImage: $resource('/api/content/sale/:id',{},{
                  query : {method:'GET', isArray:false,},
                  get:{method:'GET',isArray:false},
                  delete:{method:'DELETE',isArray:false}
               }),
               saleContent: $resource('/api/content/upload/sale',{},{
                  upload:{
                     method:'POST',
                     transformRequest: function(data) { return data; },
                     transformResponse:function(data, headers){
                        return {result:data}
                     },
                     headers : { 'Content-Type' : undefined },
                     isArray : false
                  }
               }),
                itemContent: $resource('/api/content/upload/item',{},{
                    upload:{
                        method:'POST',
                        transformRequest: function(data) { return data; },
                        headers : { 'Content-Type' : undefined },
                        isArray : true
                    }
                }),
                itemCrop: $resource('/api/content/upload/crop',{},{
                    upload:{
                        method:'POST',
                        transformRequest: function(data) { return data; },
                        headers : { 'Content-Type' : undefined },
                        isArray : true
                    }
                }),

                item: $resource('/api/item/:id',{},{
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
                        },
                        transformResponse:function(data, headers){
                            return {result:data}
                        }
                    }
                }),
                
                filterByCompany:$resource('/api/item/filter/company',{},{
                    filter:{method:'GET',isArray:true}
                }),
                filterByCategory:$resource('/api/item/filter/category',{},{
                    filter:{method:'GET',isArray:true}
                }),

                inboxMail:$resource('/api/mail/inbox',{},{get:{method:'GET',isArray:true}}),
                sendMail:$resource('/api/mail/send',{},{get:{method:'GET',isArray:true}}),

                categoryItems: $resource('/api/category/:name/items',{},{ get : {method:'GET',isArray:true}}),

                companyMap: $resource('/api/company/map',{},{ get : { method: 'GET', isArray : true }}),
                companyCategories: $resource('/api/company/:id/categories',{},{ get : { method: 'GET', isArray : true }}),
                categoryMap: $resource('/api/category/map',{},{get : { method: 'GET', isArray : true }}),
                typeMap:$resource('/api/category/type/map',{},{
                    get:{method:'GET',isArray:true}
                }),
                categoryTree: $resource("/api/category/tree",{},{
                    get:{method:'GET',isArray:true},
                    post:{method:'POST',isArray:false}
                }),
                categoryTypes:$resource("/api/category/types/:id",{},{
                    get:{method:'GET',isArray:true},
                    update:{method:'POST',isArray : false},
                    delete:{method:'DELETE',isArray:false}
                }),
                categoryMenu:$resource("/api/category/side/menu",{},{
                    get:{method:'GET',isArray:true}
                }),
                categoryRootMap:$resource("/api/category/map/roots",{},{
                    get:{method:'GET',isArray:true}
                }),
                categoryChildrenMap:$resource("/api/category/:id/children",{},{
                    get:{method:'GET',isArray:true}
                }),
                categoryChildrenCompanyMap:$resource("/api/category/:id/children/company",{},{
                    get:{method:'GET',isArray:true}
                }),
                categorySubList:$resource("/api/category/:id/sub",{},{
                    get:{method:'GET',isArray:true}
                }),
                category:$resource("/api/category/:name",{},{
                    get:{method:'GET',isArray:false},
                    post:{method:'POST',isArray:false},
                    put:{method:'PUT',isArray:false},
                    delete:{method:'DELETE',isArray:false}
                }),
                

                //itemFilter:$resource("/item/filter",{},{apply :{method:'POST',isArray:true}}),
                itemDetail: $resource("/api/item/:id/detail",{},{get:{method:'GET',isArray:false}}),
                /* AUTH SECTION */
                authRegister: $resource('/api/auth/register',{},{
                    post:{
                        method:'POST',
                        isArray:false,
                        transformResponse: function(data){
                            return {result:data}
                        }
                    }
                }),
                authRegisterConfirmRequest: $resource('/api/auth/register/confirm/request/:type',{type:'@type'},{
                    post:{
                        method:'POST',
                        isArray:false,
                        transformResponse: function(data){
                            return {result:data}
                        }
                    }
                }),
                authRegisterConfirm: $resource('/api/auth/register/confirm',{},{
                    post:{
                        method:'POST',
                        isArray:false,
                        transformResponse: function(data){
                            return {result:angular.fromJson(data)}
                        }
                    }
                }),
                authRestore: $resource('/api/auth/restore',{},{post:{method:'POST',isArray:false}}),

                authLogin: $resource('/api/auth/login',{},{
                    post:{method:'POST'}
                }),
                authChange: $resource('/api/auth/change',{},{
                    post:{
                        method:'POST',
                        isArray:false,
                        transformResponse: function(data){
                            return {result:angular.fromJson(data)}
                        }
                    }
                }),

                personMap: $resource('/api/customer/map',{},{get : { method:'GET',isArray: true}}),
                itemMap: $resource('/api/item/map',{},{get:{method:'GET',isArray:true}}),
                image: $resource('/api/content/remove/:id',{},{remove:{method:'DELETE'}}),
                order:$resource('/api/order/:id',{},{
                    all:{method:'GET',isArray:true},
                    get:{method:'GET',isArray:false},
                    post:{method:'POST',isArray:false, transformRequest:function(data){
                        if(data.order.recipientPhone){
                            data.order.recipientPhone = data.order.recipientPhone.replace(/[^0-9]/g,'');
                        }
                        return angular.toJson(data);
                    }},
                    delete:{method:'DELETE',isArray:false}
                }),
                orderHistory: $resource('/api/order/history',{},{all:{method:'GET',isArray:true}}),
                orderPrivate:$resource('/api/order/personal',{},{post:{method:'POST',isArray:false}}),
                orderItems: $resource('/api/order/:id/items',{},{get: {method:'GET',isArray:true}}),
                orderByCustomerId: $resource('/api/order/customer/:id',{},{get : { method: 'GET', isArray : true }}),
                orderedItem: $resource('/api/order/:orderId/item/:itemId',{},{
                    delete: {method:'DELETE'},
                    update: {method:'POST'}
                }),
                contactCallback: $resource('/api/contact/callback/sms',{},{
                    post:{method:'POST',isArray:false}
                }),
                email: $resource('/api/mail/simple',{},{
                    post:{method:'POST',isArray:false}
                }),
                customer: $resource('/api/customer/:id',{},{
                        all:{method:'GET',isArray:true},
                        get:{method:'GET',isArray:false},
                        put:{method:'PUT',isArray:false},
                        post:{method:'POST',isArray:false},
                        delete:{method:'DELETE',isArray:false}
                    }
                ),
                customerPrivate: $resource('/api/customer/private',{},{
                    get:{method:'GET',isArray:false},
                }),

                previewItems: $resource('/api/item/preview',{},{
                    filter:{method:'POST',isArray:false}
                }),

                galleryShow: $resource('/api/content/set/show',{},{
                    toggle:{method:'PUT',isArray:false}
                }),
                galleryMain: $resource('/api/content/set/main',{},{
                    toggle:{method:'PUT',isArray:false}
                }),
                notForSale:$resource('/api/item/set/sale',{},{
                    toggle:{method:'POST',isArray:false}
                }),
                itemStatusMap:$resource('/api/clss/item/status/map',{},{
                    get:{method:'GET',isArray:true,transformResponse: function(data){
                        var result = [];
                        angular.forEach(angular.fromJson(data), function(value, key){
                            result.push({id:key,value:value})
                        });
                        return result;
                    }}
                }),
                // отмена заказа
                orderCancel:$resource('/api/order/:id/cancel',{id:'@id'}, {
                    put: {method: 'PUT', isArray: false}
                }),
                orderStatusMap:$resource('/api/clss/order/status/map',{},{
                    get:{method:'GET',isArray:true,transformResponse: function(data){
                        // var result = [{id:null,value:"Выберите статус заказа ..."}];
	                    var result = [];
                        angular.forEach(angular.fromJson(data), function(value, key){
                            result.push({id:key,value:value})
                        });
                        return result;
                    }}
                }),
                deliveryMap:$resource('/api/clss/order/delivery',{},{
                    get:{method:'GET',isArray:true,transformResponse: function(data){
                        // var result = [{id:null,value:"Выберите тип доставки ..."}];
	                    var result = [];
                        angular.forEach(angular.fromJson(data), function(value){
                            result.push({id:value.id,value:value.name,hint:value.hint})
                        });
                        return result;
                    }}
                }),
                notification:{
                    newCount: $resource('/api/notification/new/count',{},{
                        get:{method:'GET',isArray:false, transformResponse:function(data, headers){
                            return {result: data}
                        }}
                    }),
                    core: $resource('/api/notification/:notificationId',{},{
                            all:{method:'GET',isArray:true},
                            get:{method:'GET',isArray:false},
                            put:{method:'PUT',isArray:false},
                            post:{method:'POST',isArray:false},
                            delete:{method:'DELETE',isArray:false}
                        }
                    )
                },
                instagram:{
                    image: $resource('/api/content/instagram',{},{
                        all:{method:"GET", isArray:true}
                    }),
                    posts: $resource('/api/instagram/:id',{},{
                      all:{method:"GET", isArray:true},
                      put:{method:'PUT',isArray:true},
                      delete:{method:'DELETE',isArray:false}
                    }),
                    fullPosts: $resource('/api/instagram/visible',{},{
                      all:{method:"GET", isArray:true}
                    }),
                    refresh: $resource('/api/instagram/refresh',{},{
                      all:{method:"GET", isArray:true}
                    }),
                },
                report: {
                    items: $resource('/api/report/items/:fileName',{fileName:'@fileName'},{
                        get:{
                            method: 'GET',
                            isArray:false,
                            headers: { accept: xlsxContentType },
                            responseType: 'arraybuffer',
                            cache: false,
                            transformResponse: function (data) {
                                return {
                                    response: new Blob([data], {type: xlsxContentType})
                                };
                            }
                        },
                        upload:{
                            method:'POST',
                            transformRequest: function(data) { return data; },
                            headers : { 'Content-Type' : undefined },
                            isArray : false
                        }
                    }),
                },
                media: {
                  download: $resource('/media/image/raw/:id',{id:'@id'},{
                    get:{
                      method: 'GET',
                      isArray:false,
                      headers: { 'Content-Type': imgContentType },
                      responseType: 'arraybuffer',
                      cache: false,
                      transformResponse: function (data) {
                        return {
                          response: new Blob([data], {type: imgContentType})
                        };
                      }}
                    }),
                },
                wishlist: {
                    count: $resource('/api/wishlist/count',{},{
                        get:{method:'GET',isArray:false, transformResponse:function(data, headers){
                            return {result: data}
                        }}
                    }),
                    core : $resource('/api/wishlist/:email/:id',{},{
                        all:{method:'GET',isArray:true},
                        get:{method:'GET',isArray:false},
                        put:{method:'PUT',isArray:false},
                        post:{method:'POST',isArray:false},
                        delete:{method:'DELETE',isArray:false}
                    })
                },
                subscriber: $resource('/api/subscriber/:id/:mail',{},{
                    all:{method:'GET',isArray:true},
                    get:{method:'GET',isArray:false},
                    put:{method:'PUT',isArray:false},
                    post:{method:'POST',isArray:false},
                    delete:{method:'DELETE',isArray:false}
                }),
                sale: $resource('/api/sale/:id',{id:'@id'},{
                    all:{method:'GET',isArray:true},
                    get:{method:'GET',isArray:false},
                    put:{method:'PUT',isArray:false},
                    post:{method:'POST',isArray:false},
                    delete:{method:'DELETE',isArray:false},
                    activate:{method:'PATCH',isArray:false}
                }),
               saleByName: $resource("/api/sale/detail",{},{
                  get: {method:'GET',isArray:false, transformResponse:function(data, headers){
                     var result = angular.fromJson(data);
                     result.name = result.title;
                     return result;
                  }},
               }),
                
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
                                    deferred.resolve(result);
                                    closeModal(modalBase, scope);
                                },
                                dismiss: function (reason) {
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

        .factory('menu', function () {

                var sections = [];
                var self;

                return self = {
                    sections: sections,

                    toggleSelectSection: function (section) {
                        self.openedSection = (self.openedSection === section ? null : section);
                    },

                    isSectionSelected: function (section) {
                        return self.openedSection === section;
                    }
                };

            })
})();