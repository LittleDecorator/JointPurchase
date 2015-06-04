'use strict';

var route = {
    home: {
        name:'home',
        url: '/',
        templateUrl : 'pages/home.html',
        controller: 'mainController',
        data: {
            requireLogin: false
        }
    },
    product : {
        name: 'product',
        url:'/product',
        templateUrl : 'pages/product.html',
        controller: 'productController',
        data:{
            displayName: 'Продукция',
            requireLogin: false
        }
    },
    about : {
        name: 'about',
        url:'/about',
        templateUrl : 'pages/about.html',
        controller: 'aboutController',
        data:{
            displayName: 'О нас',
            requireLogin: false
        }
    },
    orders : {
        name: 'orders',
        url: '/orders?customerId',
        templateUrl : 'pages/orders.html',
        controller: 'orderController',
        data: {
            requireLogin: true
        }
    },
    person : {
        name: 'person',
        url:'/person?companyId',
        templateUrl : 'pages/persons.html',
        controller: 'personController',
        data: {
            requireLogin: true
        }
    },
    company : {
        name: 'company',
        url:'/company',
        templateUrl : 'pages/companies.html',
        controller: 'companyController',
        data: {
            requireLogin: true
        }
    },
    contact : {
        name: 'contact',
        url:'/contact',
        templateUrl : 'pages/contact.html',
        controller: 'contactController',
        data: {
            requireLogin: false
        }
    },
    item:{
        name:'item',
        url:'/item?:companyId:orderId',
        templateUrl : 'pages/item/items.html',
        controller: 'itemController',
        data: {
            requireLogin: true
        }
    },
    gallery:{
        name:'gallery',
        url:'/gallery?:itemId',
        templateUrl : 'pages/item/gallery.html',
        controller: 'galleryController',
        data: {
            requireLogin: true
        }
    },
    login:{
        name:'login',
        url:'/login',
        templateUrl : 'pages/login.html',
        controller: 'authController',
        data: {
            requireLogin: true
        }
    },
    detail:{
        name:'detail',
        url:'/detail?:itemId',
        templateUrl : 'pages/item/detail.html',
        controller: 'detailController',
        data: {
            requireLogin: false
        }
    }

};
