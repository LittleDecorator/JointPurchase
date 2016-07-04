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
                        templateUrl: 'pages/cart.html',
                        controller: 'cartController'
                    }
                },
                data: {
                    requireLogin: false,
                    displayName: 'Корзина'
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
                    displayName: 'Детали заказа'
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
                        controller: 'contactController'
                        //controller: 'wrapperController'
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
                    requireLogin: false,
                    options: function() {
                        return {reload: true}
                    }
                },
                resolve: {
                    node: function() {
                       return null;
                    }
                }
            },

            {
                name: 'catalog.type',
                url:'/:type?id',
                parent:'catalog',
                views: {
                    'main@': {
                        templateUrl : 'pages/catalog.html',
                        controller: 'catalogController'
                    }
                },
                data:{
                    displayName: '{{node.name}}',
                    requireLogin: false
                },
                resolve: {
                    node: function($stateParams,resolveService) {
                        console.log($stateParams);
                        if($stateParams.type == 'category'){
                            return resolveService.getCategory($stateParams.id);
                        } else {
                            return resolveService.getCompany($stateParams.id);
                        }
                    }
                }
            },

            {
                name:'catalog.detail',
                url:'/card/:itemId',
                parent:'catalog',
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
                parent:'order',
                views: {
                    'main@': {
                        templateUrl : 'pages/card/orderCard.html',
                        controller: 'orderDetailController'
                    }
                },
                data: {
                    requireLogin: true,
                    displayName: '{{order.uid|nvl:"Создание"}}',
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
                parent: 'person',
                views: {
                    'main@': {
                        templateUrl : 'pages/card/personCard.html',
                        controller: 'personDetailController'
                    }
                },
                data: {
                    displayName:'{{ person.firstName}} {{person.lastName}} {{person.middleName}}',
                    requireLogin: true
                },
                resolve: {
                    person: function($stateParams, resolveService) {
                        return resolveService.getPerson($stateParams.id);
                    },
                    companies: function($stateParams, resolveService) {
                        return resolveService.getCompanyMap();
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
                    requireLogin: true
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
                    requireLogin: true
                }
            },
            {
                name: 'company.detail',
                url:'/:id',
                parent:'company',
                views: {
                    'main@': {
                        templateUrl : 'pages/card/companyCard.html',
                        controller: 'companyDetailController'
                    }
                },
                data: {
                    requireLogin: true,
                    displayName: '{{company.name|nvl:"Создание"}}'
                },
                resolve: {
                    company: function($stateParams, resolveService) {
                        return resolveService.getCompany($stateParams.id);
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
                    displayName: '{{item.name|nvl:"Создание"}}'
                },
                resolve: {
                    item: function($stateParams, resolveService) {
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
                    },
                    categories: function($stateParams, resolveService){
                        return resolveService.getCategoryMap();
                    }
                },
                data: {
                    displayName: 'Категории',
                    requireLogin: true

                }
            },
            {
                name: 'category.card',
                url:'/category/:id',
                parent:'category',
                views: {
                    'main@': {
                        templateUrl : 'pages/card/categoryCard.html',
                        controller: 'categoryCardController'
                    }
                },
                resolve:{
                    rootCategories: function($stateParams, resolveService){
                        return resolveService.getCategoryMap();
                    },
                    category: function($stateParams, resolveService){
                        return resolveService.getCategory($stateParams.id);
                    },
                    categoryItems: function(resolveService,$stateParams){
                        return resolveService.getCategoryItems($stateParams.id)
                    }
                },
                data: {
                    displayName: '{{category.name|nvl:"Создание"}}',
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
