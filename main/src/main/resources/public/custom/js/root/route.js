'use strict';

var route = {
    getRoutes: function(){
        return [
            {
                name: 'home',
                url: '/',
                views: {
                    'main@': {
                        templateUrl: 'pages/home.html'
                    }
                },
                data: {
                    requireLogin: false,
                }
            },
            {
                name: 'cart',
                url: '/cart',
                views: {
                    'main@': {
                        templateUrl: 'pages/orderPreview.html',
                        controller: 'cartController'
                    }
                },
                data: {
                    requireLogin: false,
                    //base:true
                }
            },
            {
                name: 'cart.checkout',
                url: '/private/cartCheckout',
                views: {
                    'main@': {
                        templateUrl: 'pages/cartCheckout.html',
                        controller: 'cartCheckoutController'
                    }
                },
                data: {
                    requireLogin: false
                    //base:true
                }
            },
            {
                name: 'cart.checkout.done',
                url: '/order/done',
                views: {
                    'main@': {
                        templateUrl: 'pages/OrderingDone.html',
                        controller: 'cartPurchaseDoneController'
                    }
                },
                data: {
                    requireLogin: false
                    //base:true
                },
                resolve: {
                    purchase: function($stateParams, resolveService) {
                        return resolveService.getOrder($stateParams.id);
                    }
                }
            },
            {
                name: 'about',
                url:'/about',
                views: {
                    'main@': {
                        templateUrl : 'pages/about.html',
                        controller: 'aboutController'
                    }
                },
                data:{
                    requireLogin: false,
                    //base:true
                }
            },
            {
                name: 'email',
                url:'/email',
                views: {
                    'main@': {
                        templateUrl : 'pages/email.html',
                        controller: 'emailController'
                    }
                },
                data:{
                    requireLogin: false,
                    //base:true
                }
            },
            {
                name: 'stock',
                url:'/stock',
                views: {
                    'main@': {
                        templateUrl : 'pages/stock.html',
                        controller: 'aboutController'
                    }
                },
                data:{
                    requireLogin: false,
                    //base:true
                }
            },
            {
                name: 'contact',
                url:'/contact',
                views: {
                    'main@': {
                        templateUrl : 'pages/contact.html',
                        //controller: 'contactController'
                        controller: 'wrapperController'
                    }
                },
                data: {
                    requireLogin: false,
                    //base:true
                }
            },
            {
                name:'registration',
                url:'/registration',
                views: {
                    'main@': {
                        templateUrl : 'pages/registration.html',
                        controller: 'registrationController'
                    }
                },
                data: {
                    requireLogin: false,
                    //base:true
                }
            },
            {
                name:'remember',
                url:'/remember',
                views: {
                    'main@': {
                        templateUrl : 'pages/remember.html',
                        controller: 'rememberController'
                    }
                },
                data: {
                    requireLogin: false,
                    //base:true
                }
            },
            {
                name:'restore',
                url:'/restore',
                views: {
                    'main@': {
                        templateUrl : 'pages/remember.html',
                        controller: 'restoreController'
                    }
                },
                data: {
                    requireLogin: false
                    //base:true
                }
            },
            {
                name:'registration.result',
                url:'/result?confirmed',
                views: {
                    'main@': {
                        templateUrl : 'pages/registrationResult.html',
                        controller: 'registrationResultController'
                    }
                },
                data: {
                    requireLogin: false,
                    //base:true
                }
            },
            {
                name:'product.detail',
                url:'/:itemId',
                views: {
                    'main@': {
                        templateUrl : 'pages/item/detail.html',
                        controller: 'detailController'
                    }
                },
                data: {
                    displayName: '{{product.name}}',
                    requireLogin: false,
                    //base:false
                },
                resolve: {
                    product: function($stateParams, resolveService) {
                        return resolveService.getProduct($stateParams.itemId);
                    }
                }
            },
            {
                name: 'product',
                url:'/product',
                views: {
                    'main@': {
                        templateUrl : 'pages/product.html',
                        controller: 'productController'
                    }
                },
                data:{
                    displayName: 'Каталог',
                    requireLogin: false,
                    //base:true
                }
            },
            {
                name: 'order.detail',
                url: '/:id',
                views: {
                    'main@': {
                        templateUrl : 'pages/order-detail.html',
                        controller: 'orderDetailController'
                    }
                },
                data: {
                    requireLogin: true,
                    displayName: '{{order.uid}}',
                    //base:false
                },
                resolve: {
                    order: function($stateParams, resolveService) {
                        return resolveService.getOrder($stateParams.id);
                    }
                }
            },
            {
                name: 'order',
                url: '/order?customerId',
                views: {
                    'main@': {
                        templateUrl : 'pages/orders.html',
                        controller: 'orderController'
                    }
                },
                data: {
                    displayName: 'Заказы',
                    requireLogin: true,
                    //base:true
                },
                resolve: {
                    order: function() {
                        return null;
                    }
                }
            },
            {
                name: 'person.detail',
                url:'/:id',
                views: {
                    'main@': {
                        templateUrl : 'pages/person-detail.html',
                        controller: 'personController'
                    }
                },
                data: {
                    requireLogin: true,
                    displayName:'{{ person.firstName}} {{person.lastName}} {{person.middleName}}',
                    //base:false
                },
                resolve: {
                    person: function($stateParams, resolveService) {
                        return resolveService.getPerson($stateParams.id);
                    }
                }
            },
            {
                name: 'person',
                url:'/person?companyId',
                views: {
                    'main@': {
                        templateUrl : 'pages/persons.html',
                        controller: 'personController'
                    }
                },
                data: {
                    displayName: 'Клиенты',
                    requireLogin: true,
                    //base:true
                },
                resolve: {
                    person: function() {
                        return null;
                    }
                }
            },
            {
                name: 'company.detail',
                url:'/:id',
                views: {
                    'main@': {
                        templateUrl : 'pages/company-detail.html',
                        controller: 'companyController'
                    }
                },
                data: {
                    requireLogin: true,
                    displayName:'{{ company.name }}',
                    //base:false
                },
                resolve: {
                    company: function($stateParams, resolveService) {
                        return resolveService.getCompany($stateParams.id);
                    }
                }
            },
            {
                name: 'company',
                url:'/company',
                views: {
                    'main@': {
                        templateUrl : 'pages/companies.html',
                        controller: 'companyController'
                    }
                },
                data: {
                    displayName: 'Поставщики',
                    requireLogin: true,
                    //base:true
                },
                resolve: {
                    company: function() {
                        return null;
                    }
                }
            },
            {
                name:'item',
                url:'/item?:companyId:orderId',
                views: {
                    'main@': {
                        templateUrl : 'pages/item/items.html',
                        controller: 'itemController'
                    }
                },
                data: {
                    displayName: 'Товар',
                    requireLogin: true,
                    //base:true
                },
                resolve: {
                    item: function() {
                        return null;
                    }
                }
            },
            {
                name:'item.detail',
                url:'/:id',
                views: {
                    'main@': {
                        templateUrl : 'pages/item-detail.html',
                        controller: 'itemDetailController'
                    }
                },
                data: {
                    requireLogin: true,
                    displayName: '{{item.name}}'
                },
                resolve: {
                    item: function($stateParams, resolveService) {
                        return resolveService.getItem($stateParams.id);
                    }
                }
            },
            {
                name:'item.detail.gallery',
                url:'/gallery',
                views: {
                    'main@': {
                        templateUrl : 'pages/item/gallery.html',
                        controller: 'galleryController'
                    }
                },
                data: {
                    requireLogin: true,
                    displayName: 'Изображения',
                    //base:false
                }
            },
            {
                name: 'category',
                url:'/category',
                views: {
                    'main@': {
                        templateUrl : 'pages/category.html',
                        controller: 'categoryController'
                    }
                },
                data: {
                    displayName: 'Категории',
                    requireLogin: true
                    //base:true
                }
            },
            {
                name: 'login',
                url: '/login',
                views: {
                    'main@': {
                        templateUrl: 'pages/login.html',
                        controller:'loginController'
                    }
                },
                data: {
                    requireLogin: false,
                    displayName: false
                }
            }
        ]
    }
};
