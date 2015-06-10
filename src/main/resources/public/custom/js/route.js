'use strict';

var route = {
    getRoutes: function(){
        return [
            {
                name: 'home',
                url: '/',
                templateUrl: 'pages/home.html',
                data: {
                    requireLogin: false
                }
            },
            {
                name: 'product',
                url:'/product',
                templateUrl : 'pages/product.html',
                controller: 'productController',
                data:{
                    displayName: 'Продукция',
                    requireLogin: false
                }
            },
            {
                name: 'about',
                url:'/about',
                templateUrl : 'pages/about.html',
                controller: 'aboutController',
                data:{
                    displayName: 'О нас',
                    requireLogin: false
                }
            },
            {
                name: 'orderDetail',
                url: '/orderDetail?:id',
                templateUrl : 'pages/order-detail.html',
                controller: 'orderController',
                data: {
                    requireLogin: true
                }
            },
            {
                name: 'order',
                url: '/order?customerId',
                templateUrl : 'pages/orders.html',
                controller: 'orderController',
                data: {
                    displayName: 'Заказы',
                    requireLogin: true
                }
            },
            {
                name: 'personDetail',
                url:'/personDetail?:id',
                templateUrl : 'pages/person-detail.html',
                controller: 'personController',
                data: {
                    requireLogin: true
                }
            },
            {
                name: 'person',
                url:'/person?companyId',
                templateUrl : 'pages/persons.html',
                controller: 'personController',
                data: {
                    displayName: 'Клиенты',
                    requireLogin: true
                }
            },
            {
                name: 'companyDetail',
                url:'/companyDetail?:id',
                templateUrl : 'pages/company-detail.html',
                controller: 'companyController',
                data: {
                    requireLogin: true
                }
            },
            {
                name: 'company',
                url:'/company',
                templateUrl : 'pages/companies.html',
                controller: 'companyController',
                data: {
                    displayName: 'Поставщики',
                    requireLogin: true
                }
            },
            {
                name: 'contact',
                url:'/contact',
                templateUrl : 'pages/contact.html',
                controller: 'contactController',
                data: {
                    displayName: 'Контакты',
                    requireLogin: false
                }
            },
            {
                name:'itemDetail',
                url:'/itemDetail?:id',
                templateUrl : 'pages/item-detail.html',
                controller: 'itemController',
                data: {
                    requireLogin: true
                }
            },
            {
                name:'item',
                url:'/item?:companyId:orderId',
                templateUrl : 'pages/item/items.html',
                controller: 'itemController',
                data: {
                    displayName: 'Товар',
                    requireLogin: true
                }
            },
            {
                name:'gallery',
                url:'/gallery?:itemId',
                templateUrl : 'pages/item/gallery.html',
                controller: 'galleryController',
                data: {
                    requireLogin: true
                }
            },
            {
                name:'login',
                url:'/login',
                templateUrl : 'pages/login.html',
                controller: 'authController',
                data: {
                    displayName: 'Войти',
                    requireLogin: true
                }
            },
            {
                name:'detail',
                url:'/detail?:itemId',
                templateUrl : 'pages/item/detail.html',
                controller: 'detailController',
                data: {
                    requireLogin: false
                }
            },
            {
                name:'registration',
                url:'/registration',
                templateUrl : 'pages/registration.html',
                controller: 'registrationController',
                data: {
                    requireLogin: false
                }
            }
        ]
    }
};
