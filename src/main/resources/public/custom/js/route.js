'use strict';

var route = {
    home: {
        name:'home',
        url: '/',
        templateUrl : 'pages/home.html',
        controller: 'mainController'
    },
    product : {
        name: 'product',
        url:'/product',
        templateUrl : 'pages/product.html',
        controller: 'productController',
        data:{
            displayName: 'Продукция'
        }
    },
    about : {
        name: 'about',
        url:'/about',
        templateUrl : 'pages/about.html',
        controller: 'aboutController',
        data:{
            displayName: 'О нас'
        }
    },
    orders : {
        name: 'orders',
        url: '/orders?customerId',
        templateUrl : 'pages/orders.html',
        controller: 'orderController'
    },
    person : {
        name: 'person',
        url:'/person?companyId',
        templateUrl : 'pages/persons.html',
        controller: 'personController'
    },
    company : {
        name: 'company',
        url:'/company',
        templateUrl : 'pages/companies.html',
        controller: 'companyController'
    },
    contact : {
        name: 'contact',
        url:'/contact',
        templateUrl : 'pages/contact.html',
        controller: 'contactController'
    },
    item:{
        name:'item',
        url:'/item?:companyId:orderId',
        templateUrl : 'pages/item/items.html',
        controller: 'itemController'
    },
    gallery:{
        name:'gallery',
        url:'/gallery?:itemId',
        templateUrl : 'pages/item/gallery.html',
        controller: 'galleryController'
    },
    login:{
        name:'login',
        url:'/login',
        templateUrl : 'pages/login.html',
        controller: 'authController'
    },
    detail:{
        name:'detail',
        url:'/detail?:itemId',
        templateUrl : 'pages/item/detail.html',
        controller: 'detailController'
    }

};
