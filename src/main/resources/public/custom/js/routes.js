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
                }
            },
            
            {
                name: 'catalog',
                url:'/catalog',
                views: {
                    'main@': {
                        templateUrl : 'pages/catalog.html',
                        controller: 'catalogController'
                    }
                },
                data:{
                    displayName: 'Каталог',
                    requireLogin: false
                }
            },
            {
                name:'catalog.detail',
                url:'/:itemId',
                views: {
                    'main@': {
                        templateUrl : 'pages/card/catalogCard.html',
                        controller: 'catalogCardController'
                    }
                },
                data: {
                    displayName: '{{product.name}}',
                    requireLogin: false,
                },
                resolve: {
                    product: function($stateParams, resolveService) {
                        return resolveService.getProduct($stateParams.itemId);
                    }
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
                        templateUrl : 'pages/card/personCard.html',
                        controller: 'personController'
                    }
                },
                data: {
                    requireLogin: true,
                    displayName:'{{ person.firstName}} {{person.lastName}} {{person.middleName}}',
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
                        templateUrl : 'pages/items.html',
                        controller: 'itemController'
                    }
                },
                data: {
                    displayName: 'Товар',
                    requireLogin: true,
                },
                resolve: {
                    companies: function(resolveService) {
                        return resolveService.getCompanyMap();
                    }
                }
            },
            {
                name:'item.detail',
                parent:'item',
                url:'/:id',
                views: {
                    'main@': {
                        templateUrl : 'pages/card/itemCard.html',
                        controller: 'itemDetailController'
                    }
                },
                data: {
                    requireLogin: true,
                    displayName: '{{resolved[0].name}}'
                },
                resolve: {
                    resolved: function($stateParams, resolveService) {
                        return resolveService.getItemDetail($stateParams.id);
                    }
                }
            },
            {
                name:'item.detail.gallery',
                parent:'item.detail',
                url:'/gallery',
                views: {
                    'main@': {
                        templateUrl : 'pages/gallery.html',
                        controller: 'galleryController'
                    }
                },
                data: {
                    requireLogin: true,
                    displayName: 'Изображения',
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
                resolve:{
                    categoryNodes: function($stateParams, resolveService) {
                        return resolveService.getCategoryTreeData();
                    }
                },
                data: {
                    displayName: 'Категории',
                    requireLogin: true

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
