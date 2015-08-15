purchase.factory('authInterceptor',function ($rootScope, $q, $cookies) {
    return {
        // Add authorization token to headers
        request: function (config) {
            //console.log("auth interceptor - request");
            config.headers = config.headers || {};
            if ($cookies.get('token')) {
                config.headers.Authorization = 'Bearer ' + $cookies.get('token');
            } else {
                console.log("no token");
            }
            return config;
        },

        // Intercept 401s and redirect you to login
        responseError: function(response) {
            console.log("auth interceptor - responseError");
            if(response.status === 401) {
                // remove any stale tokens
                $cookies.remove('token');
                //$location.path('/home');
                //$state.transitionTo("home");
                return $q.reject(response);
            } else {
                return $q.reject(response);
            }
        }
    };
 });

purchase.factory('factory',['$resource',function($resource){
    return {
        customer: $resource('/customer/:id'),
        order: $resource('/order/:id'),
        company: $resource("/company/:id"),
        item: $resource("/item/:id"),

        categoryTree: $resource("/category/tree",{},{
            get:{method:'GET',isArray:false}
        }),

        itemDetail: $resource("/item/:id/detail",{},{
            get:{method:'GET',isArray:false}
        }),

        itemFilter:$resource("/item/filter",{},{apply :{method:'POST',isArray:true}}),

        orderByCustomerId: $resource('/order/customer/:id',{},{get : { method: 'GET', isArray : true }}),
        customerByCompanyId: $resource('/customer/company/:id',{},{get : { method: 'GET', isArray : true }}),

        companyMap: $resource('/company/map',{},{ get : { method: 'GET', isArray : true }}),
        categoryMap: $resource('/category/map',{},{get : { method: 'GET', isArray : true }}),
        personMap: $resource('/customer/map',{},{get : { method:'GET',isArray: true}}),
        itemMap: $resource('/item/map',{},{get:{method:'GET',isArray:true}}),

        orderItems: $resource('/order/:id/items',{},{get: {method:'GET',isArray:true}}),
        orderedItem: $resource('/order/:orderId/item/:itemId',{},{
            delete: {method:'DELETE'},
            update: {method:'POST'}
        }),

        previewItems: $resource('/item/preview',{},{
            get:{method:'GET',isArray:true},
            filter:{method:'POST',isArray:true}
        }),

        itemContent: $resource('/content/upload/item',{},{
                        upload:{
                            method:'POST',
                            transformRequest: function(data) { return data; },
                            headers : { 'Content-Type' : undefined },
                            isArray : true
                        }
        }),
        itemImage: $resource('/content/items/:id',{},{
            query :{method:'GET',isArray:true},
            get:{method:'GET',isArray:true}
        }),

        authLogin: $resource('/auth/login',{},{
            post:{method:'POST'}
        }),

        reg: $resource('/auth/register',{},{
            post:{method:'POST'}
        }),

        authRoleCheck: $resource('/api/role/:role',{},{
            get:{method:'GET',
                isArray:false
                //headers: { 'Authorization': tokenHandler.token.token }
            }
        })
    };

}]);

purchase.factory('factoryModal', modal);

modal.$inject = ['$http', '$compile', '$rootScope', '$document', '$q', '$controller', '$timeout'];

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
}
