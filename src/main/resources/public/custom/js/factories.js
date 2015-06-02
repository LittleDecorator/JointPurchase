purchase.factory('tokenHandler', function() {
    return {
        token: 'Bearer '
    }
});

purchase.factory('factory',['$resource',function($resource){
    return {
        customer: $resource('/customer/:id'),
        order: $resource('/order/:id'),
        company: $resource("/company/:id"),
        item: $resource("/item/:id"),

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
