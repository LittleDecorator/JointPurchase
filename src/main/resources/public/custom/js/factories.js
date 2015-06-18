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
        itemImage: $resource('/content/items/:id',{},{
            query :{method:'GET',isArray:true},
            get:{method:'GET',isArray:true}
        }),

        authLogin: $resource('/auth/login',{},{
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
