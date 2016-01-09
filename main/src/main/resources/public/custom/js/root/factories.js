(function(){
    angular.module('purchase.factories',[]);
})();

(function(){
    'use strict';

    angular.module('purchase.factories')
        .factory('authInterceptor',['$rootScope','$q','$cookies',function ($rootScope, $q, $cookies) {
            return {
                request: function (config) {
                    config.headers = config.headers || {};
                    if ($cookies.get('token')) {
                        config.headers.Authorization = 'Bearer ' + $cookies.get('token');
                    } else {
                        console.log("no token");
                    }
                    return config;
                },

                // Intercept 401s and redirect you to login
                responseError: function (response) {
                    console.log("auth interceptor - responseError");
                    if (response.status === 401) {
                        // remove any stale tokens
                        $cookies.remove('token');
                        return $q.reject(response);
                    } else {
                        return $q.reject(response);
                    }
                }
            };
        }])

        .factory('dataResources',['$resource',function($resource){
            return {
                company: $resource("/company/:id"),
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

                searchItem:$resource('/item/search/',{},{search :{method:'POST',isArray:true}}),

                item: $resource('/item/:id'),
                //itemClss:$resource('/item/clss',{},{get:{method:'GET',isArray:true}}),
                filterByType:$resource('/item/filter/type',{},{
                    filter:{method:'POST',isArray:true}
                }),
                filterByCompany:$resource('/item/filter/company',{},{
                    filter:{method:'GET',isArray:true}
                }),

                inboxMail:$resource('/mail/inbox',{},{get:{method:'GET',isArray:true}}),
                sendMail:$resource('/mail/send',{},{get:{method:'GET',isArray:true}}),
                //mail: $resource("/mail/:id"),

                categoryItems: $resource('/category/items/:categoryId',{},{ get : {method:'GET',isArray:true}}),

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

                itemFilter:$resource("/item/filter",{},{apply :{method:'POST',isArray:true}}),
                itemDetail: $resource("/item/:id/detail",{},{get:{method:'GET',isArray:false}}),

                authLogin: $resource('/auth/login',{},{
                    post:{method:'POST'}
                }),

                personMap: $resource('/customer/map',{},{get : { method:'GET',isArray: true}}),
                itemMap: $resource('/item/map',{},{get:{method:'GET',isArray:true}}),
                image: $resource('/content/remove',{},{remove:{method:'DELETE'}}),
                order:$resource('/order/:id'),
                orderPrivate:$resource('/order/personal',{},{post:{method:'POST',isArray:false}}),
                orderItems: $resource('/order/:id/items',{},{get: {method:'GET',isArray:true}}),
                orderByCustomerId: $resource('/order/customer/:id',{},{get : { method: 'GET', isArray : true }}),
                orderedItem: $resource('/order/:orderId/item/:itemId',{},{
                    delete: {method:'DELETE'},
                    update: {method:'POST'}
                }),
                contactCallback: $resource('/contact/callback/sms',{},{post:{method:'POST',isArray:false}}),
                customer: $resource('/customer/:id'),
                //customerByCompanyId: $resource('/customer/company/:id',{},{get : { method: 'GET', isArray : true }}),

                previewItems: $resource('/item/preview',{},{
                    get:{method:'GET',isArray:true},
                    filter:{method:'POST',isArray:true}
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
                orderStatus:$resource('clss/order/status',{},{
                    get:{method:'GET',isArray:true}
                }),
                orderDelivery:$resource('clss/order/delivery',{},{
                    get:{method:'GET',isArray:true}
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