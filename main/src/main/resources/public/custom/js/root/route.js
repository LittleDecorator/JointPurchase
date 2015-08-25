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
                    base:true
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
                    base:true
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
                    requireLogin: false,
                    base:true
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
                    base:true
                }
            },
            {
                name: 'contact',
                url:'/contact',
                views: {
                    'main@': {
                        templateUrl : 'pages/contact.html',
                        controller: 'contactController'
                    }
                },
                data: {
                    requireLogin: false,
                    base:true
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
                    base:true
                }
            },
            {
                name:'registration.result',
                url:'/result',
                views: {
                    'main@': {
                        templateUrl : 'pages/registrationResult.html',
                        controller: 'registrationResultController'
                    }
                },
                data: {
                    requireLogin: false,
                    base:true
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
                    base:false
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
                    displayName: 'Продукция',
                    requireLogin: false,
                    base:true
                }
            },

            {
                name: 'order.detail',
                url: '/:id',
                views: {
                    'main@': {
                        templateUrl : 'pages/order-detail.html',
                        controller: 'orderController'
                    }
                },
                data: {
                    requireLogin: true,
                    displayName: '{{order.name}}',
                    base:false
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
                    base:true
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
                    base:false
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
                    base:true
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
                    base:false
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
                    base:true
                },
                resolve: {
                    company: function() {
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
                        controller: 'itemController'
                    }
                },
                data: {
                    requireLogin: true,
                    displayName:'{{ item.name }}',
                    base:false
                },
                resolve: {
                    item: function($stateParams, resolveService) {
                        return resolveService.getItem($stateParams.id);
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
                    base:true
                },
                resolve: {
                    item: function() {
                        return null;
                    }
                }
            },
            {
                name:'item.gallery',
                url:'/:itemId/gallery',
                views: {
                    'main@': {
                        templateUrl : 'pages/item/gallery.html',
                        controller: 'galleryController'
                    }
                },
                data: {
                    requireLogin: true,
                    base:false
                }
            }

        ]
    }
};
