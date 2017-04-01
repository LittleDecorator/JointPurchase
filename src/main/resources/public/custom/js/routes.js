'use strict';

var route = {
    getRoutes: function(){
        return [
            /* домашняя страница */
            {
                name: 'home',
                url: '/',
                views: {
                    'main@': {
                        templateUrl: 'pages/home.html'
                    }
                },
                data: {
                    requireLogin: false
                }
            },
                /* корзина покупателя */
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
                /* Оформление заказа */
            {
                name: 'cart.confirm',
                url: '/private/confirm',
                parent:'cart',
                views: {
                    'main@': {
                        templateUrl: 'pages/confirm.html',
                        controller: 'confirmController'
                    }
                },
                data: {
                    requireLogin: false,
                    displayName: 'Оформление заказа'
                },
                resolve:{
                    deliveries: function(resolveService){
                        return resolveService.getDeliveryMap()
                    }
                }
            },
                /* страница О нас */
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
                /* Личный кабинет */
            {
                name: 'cabinet',
                url:'/cabinet',
                views: {
                    'main@': {
                        templateUrl : 'pages/cabinet.html',
                        controller: 'cabinetController'
                    }
                },
                data:{
                    displayName: 'Личный кабинет',
                    requireLogin: true
                },
                resolve: {
                    statusMap:function(resolveService) {
                        return resolveService.getOrderStatusMap();
                    },
                    deliveryMap:function(resolveService) {
                        return resolveService.getDeliveryMap();
                    }
                }
            },
                /* Просмотр заказа в личном кабинете */
            {
                name: 'cabinet.historyDetail',
                url: '/history/:id',
                parent:'cabinet',
                views: {
                    'main@': {
                        templateUrl: 'pages/history.html',
                        controller: 'cabinetHistoryDetailController'
                    }
                },
                data: {
                    requireLogin: true,
                    displayName: 'Заказ #{{order.uid}}'
                },
                resolve: {
                    order: function($stateParams, resolveService) {
                        return resolveService.getOrder($stateParams.id);
                    },
                    items: function($stateParams,resolveService) {
                        return resolveService.getOrderItems($stateParams.id);
                    }
                }
            },
                /* Почта */
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
                    displayName: 'Почта'
                }
            },
                /* Настройки приложения */
            {
                name: 'settings',
                url:'/settings',
                views: {
                    'main@': {
                        templateUrl : 'pages/settings.html',
                        controller: 'settingsController'
                    }
                },
                data:{
                    requireLogin: false,
                    displayName: 'Настройки'
                }
            },
                /* Страница с условиями доставки */
            {
                name: 'delivery',
                url:'/delivery',
                views: {
                    'main@': {
                        templateUrl : 'pages/delivery.html',
                        controller: 'deliveryController'
                    }
                },
                data:{
                    requireLogin: false,
                    displayName: 'Доставка'
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
                    requireLogin: false
                }
            },
                /* Страца с контактами для связи с нами */
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
                    requireLogin: false
                }
            },
                /* Страница регистрации покупателя */
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
                    requireLogin: false
                }
            },
                /* Страница восстановления доступа */
            {
                name:'restore',
                url:'/restore',
                views: {
                    'main@': {
                        templateUrl : 'pages/restore.html',
                        controller: 'restoreController'
                    }
                },
                data: {
                    requireLogin: false
                }
            },
                /* Страница результата восстановления доступа */
            {
                name:'restore.result',
                url:'/restore?confirmed',
                views: {
                    'main@': {
                        templateUrl : 'pages/restoreResult.html',
                        controller: 'restoreResultController'
                    }
                },
                data: {
                    requireLogin: false
                }
            },
                /* Страница результата регистрации */
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
                /* Каталог товаров */
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
                name: 'order',
                url: '/order?customerId',
                views: {
                    'main@': {
                        templateUrl : 'pages/orders.html',
                        controller: 'orderController',
                        controllerAs: 'order'
                    }
                },
                data: {
                    displayName: 'Заказы',
                    requireLogin: true
                },
                resolve: {
                    statusMap:function(resolveService) {
                        return resolveService.getOrderStatusMap();
                    },
                    deliveryMap:function(resolveService) {
                        return resolveService.getDeliveryMap();
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
                    },
                    items: function($stateParams,resolveService) {
                        return resolveService.getOrderItems($stateParams.id);
                    },
                    statusMap:function(resolveService) {
                        return resolveService.getOrderStatusMap();
                    },
                    deliveryMap:function(resolveService) {
                        return resolveService.getDeliveryMap();
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
                    displayName:'{{(person.firstName + " " + person.lastName)|nvl:"Создание"}}',
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
                    requireLogin: true
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
                    },
                    statuses: function($stateParams, resolveService){
                        return resolveService.getItemStatusMap();
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
                /* Страница редактирования конкретного изображения */
            {
                name:'item.detail.gallery.crop',
                parent:'item.detail.gallery',
                url:'/:imageId/crop',
                views: {
                    'main@': {
                        templateUrl : 'pages/crop.html',
                        controller: 'cropController'
                    }
                },
                data: {
                    requireLogin: true,
                    displayName: 'Редактирование изображения',
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
