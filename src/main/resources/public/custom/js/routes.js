'use strict';

var route = {
  getRoutes: function () {
    return [

      /*=================================== ДОМАШНЯЯ СТРАНИЦА ====================================*/
      {
        name: 'home', url: '/', views: {
        'base@': {
          templateUrl: 'pages/home.html'
        }
      }, data: {
        requireLogin: false,
      }, metaTags: {
        title: 'Магазин детских игрушек', description: 'Только качественные и открытые игрушки...', keywords: 'lots of interresting keywords', properties: {
          'og:title': 'Магазин детских игрушек'
        }
      }
      },

      /*=================================== КОРЗИНА ====================================*/
      {
        name: 'cart', url: '/cart', views: {
        'main@': {
          templateUrl: 'pages/cart.html', controller: 'cartController', controllerAs: 'vm'
        }
      }, data: {
        requireLogin: false, displayName: 'Корзина'
      }
      }, /*=================================== ПОЧТА ====================================*/
      {
        name: 'mail', url: '/mail', views: {
        'main@': {
          templateUrl: 'pages/email.html', controller: 'emailController', controllerAs: 'vm'
        }
      }, resolve: {
        resolved: function (resolveService) {
          return null;
        }
      }, data: {
        requireLogin: true, displayName: 'Почта'
      }
      },

      /*=================================== СПИСОК ЖЕЛАЕМОГО ====================================*/
      {
        name: 'wishlist', url: '/wishlist', views: {
        'main@': {
          templateUrl: 'pages/wishlist.html', controller: 'wishlistController', controllerAs: 'vm'
        }
      }, resolve: {
        resolved: function (resolveService) {
          return null;
        }
      }, data: {
        requireLogin: true, displayName: 'Список желаемого'
      }
      },

      {
        name: 'wishlist.detail', url: '/:email', parent: 'wishlist', views: {
        'main@': {
          templateUrl: 'pages/card/wishlistCard.html', controller: 'wishlistCardController', controllerAs: 'vm'
        }
      }, resolve: {
        list: function ($stateParams, resolveService) {
          return resolveService.getWishlist($stateParams.email);
        }
      }, data: {
        requireLogin: true, displayName: 'Клиент {{list.email}}'
      }
      },

      /*=================================== ОФОРМЛЕНИЕ ЗАКАЗА ====================================*/
      {
        name: 'cart.confirm', url: '/private/confirm', parent: 'cart', views: {
        'main@': {
          templateUrl: 'pages/confirm.html', controller: 'confirmController', controllerAs: 'vm'
        }
      }, data: {
        requireLogin: false, displayName: 'Оформление заказа'
      }, resolve: {
        deliveries: function (resolveService) {
          return resolveService.getDeliveryMap()
        }
      }
      }, {
        name: 'cart.confirm.done', url: '/:id/status', parent: 'cart.confirm', views: {
          'main@': {
            templateUrl: 'pages/orderComplete.html', controller: 'confirmCompleteController', controllerAs: 'vm'
          }
        }, data: {
          displayName: "Результат", requireLogin: false
        }, resolve: {
          order: function ($stateParams, resolveService) {
            return resolveService.getOrder($stateParams.id);
          }
        }
      },

      /*=================================== О НАС * ====================================*/
      {
        name: 'about', url: '/about', views: {
        'main@': {
          templateUrl: 'pages/about.html', controller: 'aboutController'
        }
      }, data: {
        requireLogin: false
      }, metaTags: {
        title: 'О магазине', description: 'Наша миссия заключается в том, чтобы у каждого ребенка были хорошие игрушки', keywords: 'о магазине нас', properties: {
          'og:title': 'О магазине'
        }
      }
      }, /*=================================== УВЕДОМЛЕНИЯ * ====================================*/
      {
        name: 'notification', url: '/notification', views: {
        'main@': {
          templateUrl: 'pages/notifications.html', controller: 'notificationController', controllerAs: 'vm'
        }
      }, data: {
        displayName: 'Уведомления', requireLogin: true
      }
      }, {
        name: 'notification.detail', url: '/:id', parent: 'notification', views: {
          'main@': {
            templateUrl: 'pages/card/notificationCard.html', controller: 'notificationCardController', controllerAs: 'vm'
          }
        }, data: {
          displayName: '{{notification.title}}', requireLogin: true
        }, resolve: {
          notification: function ($stateParams, resolveService) {
            return resolveService.getNotification($stateParams.id);
          }
        }
      },

      /*=================================== ЛИЧНЫЙ КАБИНЕТ * ====================================*/
      {
        name: 'cabinet', url: '/cabinet', views: {
        'main@': {
          templateUrl: 'pages/cabinet.html', controller: 'cabinetController', controllerAs: 'vm'
        }
      }, data: {
        displayName: 'Личный кабинет', requireLogin: true
      }, resolve: {
        statusMap: function (resolveService) {
          return resolveService.getOrderStatusMap();
        }, deliveryMap: function (resolveService) {
          return resolveService.getDeliveryMap();
        }
      }
      }, /* Просмотр заказа в личном кабинете */
      {
        name: 'cabinet.historyDetail', url: '/history/:id', parent: 'cabinet', views: {
        'main@': {
          templateUrl: 'pages/history.html', controller: 'cabinetHistoryDetailController'
        }
      }, data: {
        requireLogin: true, displayName: 'Заказ #{{order.uid}}'
      }, resolve: {
        order: function ($stateParams, resolveService) {
          return resolveService.getOrder($stateParams.id);
        }, items: function ($stateParams, resolveService) {
          return resolveService.getOrderItems($stateParams.id);
        }
      }
      },

      /*=================================== ПОЧТА ** ====================================*/
      {
        name: 'email', url: '/email', views: {
        'main@': {
          templateUrl: 'pages/email.html', controller: 'emailController'
        }
      }, data: {
        requireLogin: false, displayName: 'Почта'
      }
      },

      /*=================================== НАСТРОЙКИ ** ====================================*/
      {
        name: 'settings', url: '/settings', views: {
        'main@': {
          templateUrl: 'pages/settings.html', controller: 'settingsController'
        }
      }, data: {
        requireLogin: false, displayName: 'Настройки'
      }
      },

      /*=================================== УСЛОВИЯ ДОСТАВКИ * ====================================*/
      {
        name: 'delivery', url: '/delivery', views: {
        'main@': {
          templateUrl: 'pages/delivery.html', controller: 'deliveryController'
        }
      }, data: {
        requireLogin: false, displayName: 'Доставка'
      }, metaTags: {
        title: 'Доставка', description: 'Осуществляем доставку по всей России', keywords: 'доставка', properties: {
          'og:title': 'Доставка'
        }
      }
      },

      /*=================================== АКЦИИ * ====================================*/
      {
        name: 'stock', url: '/stock', views: {
        'main@': {
          templateUrl: 'pages/stock.html', controller: 'aboutController'
        }
      }, data: {
        requireLogin: false
      }, metaTags: {
        title: 'Акции', description: 'Распродажа игрушек, конкурсы и сезонные скидки', keywords: 'акции', properties: {
          'og:title': 'Акции'
        }
      }
      },

      /*=================================== КОНТАКТЫ ====================================*/
      {
        name: 'contact', url: '/contact', views: {
        'main@': {
          templateUrl: 'pages/contact.html', controller: 'contactController', controllerAs: 'vm'
        }
      }, data: {
        requireLogin: false
      }, metaTags: {
        title: 'Контакты', description: 'Где нас найти? Где забрать заказ?', keywords: 'контакты', properties: {
          'og:title': 'Контакты'
        }
      }
      },

      /*=================================== РЕГИСТРАЦИЯ * ====================================*/
      {
        name: 'registration', url: '/registration', views: {
        'main@': {
          templateUrl: 'pages/registration.html', controller: 'registrationController', controllerAs: 'vm'
        }
      }, data: {
        requireLogin: false
      }
      }, /* Страница альтернативного подтверждения регистрации */
      {
        name: 'registrationConfirm', url: '/registration/confirm', views: {
        'main@': {
          templateUrl: 'pages/registrationConfirm.html', controller: 'registrationConfirmController', controllerAs: 'vm'
        }
      }, data: {
        requireLogin: false
      }
      }, /* Страница результата регистрации */
      {
        name: 'registrationResult', url: '/registration/result?confirmed', views: {
        'main@': {
          templateUrl: 'pages/registrationResult.html', controller: 'registrationResultController', controllerAs: 'vm'
        }
      }, data: {
        requireLogin: false
      }
      },

      /*=================================== ВОССТАНОВЛЕНИЕ ДОСТУПА * ====================================*/
      /* Страница восстановления доступа */
      {
        name: 'restore', url: '/restore', views: {
        'main@': {
          templateUrl: 'pages/restore.html', controller: 'restoreController'
        }
      }, data: {
        requireLogin: false
      }
      }, /* Страница результата восстановления доступа */
      {
        name: 'restoreResult', url: '/restore/result?confirmed', views: {
        'main@': {
          templateUrl: 'pages/restoreResult.html', controller: 'restoreResultController'
        }
      }, data: {
        requireLogin: false
      }
      },

      /*=================================== КАТАЛОГ ====================================*/

      {
        name: 'catalog',
        url: '/catalog', // при переходе на этот state переадресовываем его сразу на списочную страницу
        deepStateRedirect: {
          default: "catalog.list"
        }
      },

      // не отфильтрованный список товаров
      {
        name: 'catalog.list',
        url: '/',
        views: {
          'main@': {
            templateUrl: 'pages/catalog.html', controller: 'catalogController', controllerAs: 'vm'
          }
        },
        sticky: true,
        deepStateRedirect: true,
        data: {
          displayName: 'Каталог',
          requireLogin: false,
          options: function () {
            return {reload: false}
          }
        },
        resolve: {
          node: function () {
            return null;
          }
        },
        metaTags: {
          title: 'Каталог', description: 'Описание раздела каталог', keywords: 'каталог', properties: {
            'og:title': 'Каталог'
          }
        }
      },

      // карточка товара
      {
        name: 'catalog.detail', url: '/card/:itemName', parent: 'catalog',
        views: {
          'detail@': {
            templateUrl: 'pages/card/catalogCard.html', controller: 'catalogCardController', controllerAs: 'vm'
          }
        },
        data: {
          displayName: '{{product.name}}', requireLogin: false
        },
        metaTags: {
          title: function (product) {
            return product.name;
          }, keywords: '{{product.name}}', description: '{{product.description}}'
        },
        resolve: {
          product: function ($stateParams, resolveService) {
            return resolveService.getProduct($stateParams.itemName);
          }
        }
      },



      // список каталога отфильтрованный по категории или бренду
      {
        name: 'catalog.type',
        url: '/:type/:name',
        views: {
          'main@': {
            templateUrl: 'pages/catalog-list.html', controller: 'catalogController', controllerAs: 'vm'
          }
        },
        data: {
          displayName: '{{node.name}}', requireLogin: false
        },
        resolve: {
          node: function ($stateParams, resolveService) {
            if ($stateParams.type === 'category') {
              return resolveService.getCategory($stateParams.name);
            } else {
              return resolveService.getCompanyByName($stateParams.name);
            }
          }
        },
        metaTags: {
          title: function (node) {
            return node.name;
          }, keywords: '{{node.name}}', description: '{{node.description}}'
        }
      },


      // карточка товара из отфильтрованного списка товаров
      {
        name: 'catalog.type.detail', url: '/card/:itemName', parent: 'catalog.type',
        views: {
          'detail@': {
            templateUrl: 'pages/card/catalogCard.html', controller: 'catalogCardController', controllerAs: 'vm'
          }
        },
        data: {
          displayName: '{{product.name}}', requireLogin: false
        },
        metaTags: {
          title: function (product) {
            return product.name;
          }, keywords: '{{product.name}}', description: '{{product.description}}'
        },
        resolve: {
          product: function ($stateParams, resolveService) {
            return resolveService.getProduct($stateParams.itemName);
          }
        }
      },

      /*=================================== ПОИСК * ====================================*/
      {
        name: 'search', url: '/search/:criteria', views: {
        'main@': {
          templateUrl: 'pages/template/searchResult.html', controller: 'searchController'
        }
      }, data: {
        displayName: 'Результат поиска', requireLogin: false, options: function () {
          return {reload: true}
        }
      }, resolve: {
        node: function () {
          return null;
        }
      }
      }, {
        name: 'search.detail', url: '/:itemId', parent: 'search', views: {
          'main@': {
            templateUrl: 'pages/card/catalogCard.html', controller: 'catalogCardController'
          }
        }, data: {
          displayName: '{{product.name}}', requireLogin: false
        }, resolve: {
          product: function ($stateParams, resolveService) {
            return resolveService.getProduct($stateParams.itemId);
          }
        }
      },

      /*=================================== ЗАКАЗЫ ====================================*/
      {
        name: 'order', url: '/order?customerId', views: {
        'main@': {
          templateUrl: 'pages/orders.html', controller: 'orderController', controllerAs: 'vm'
        }
      }, data: {
        displayName: 'Заказы', requireLogin: true
      }, resolve: {
        statusMap: function (resolveService) {
          return resolveService.getOrderStatusMap();
        }, deliveryMap: function (resolveService) {
          return resolveService.getDeliveryMap();
        }
      }
      }, {
        name: 'order.detail', url: '/:id', parent: 'order', views: {
          'main@': {
            templateUrl: 'pages/card/orderCard.html', controller: 'orderDetailController', controllerAs: 'vm'
          }
        }, data: {
          requireLogin: true, displayName: '{{order.uid|nvl:"Создание"}}',
        }, resolve: {
          order: function ($stateParams, resolveService) {
            return resolveService.getOrder($stateParams.id);
          }, items: function ($stateParams, resolveService) {
            return resolveService.getOrderItems($stateParams.id);
          }, statusMap: function (resolveService) {
            return resolveService.getOrderStatusMap();
          }, deliveryMap: function (resolveService) {
            return resolveService.getDeliveryMap();
          }
        }
      },

      /*=================================== КЛИЕНТЫ ====================================*/
      {
        name: 'person.detail', url: '/:id', parent: 'person', views: {
        'main@': {
          templateUrl: 'pages/card/personCard.html', controller: 'personDetailController', controllerAs: 'vm'
        }
      }, data: {
        displayName: '{{(person.firstName + " " + person.lastName)|nvl:"Создание"}}', requireLogin: true
      }, resolve: {
        person: function ($stateParams, resolveService) {
          return resolveService.getPerson($stateParams.id);
        }, companies: function ($stateParams, resolveService) {
          return resolveService.getCompanyMap();
        }
      }
      }, {
        name: 'person', url: '/person?companyId', views: {
          'main@': {
            templateUrl: 'pages/persons.html', controller: 'personController', controllerAs: 'vm'
          }
        }, data: {
          displayName: 'Клиенты', requireLogin: true
        }
      },

      /*=================================== ПОСТАВЩИКИ ====================================*/
      {
        name: 'company',
        url: '/company', // при переходе на этот state переадресовываем его сразу на списочную страницу
        deepStateRedirect: {
          default: "company.list"
        }
      },
        // списочный state компаний
      {
        name: 'company.list',
        url: '/list',
        sticky: true,
        deepStateRedirect: true,
        views: {
          'main@': {
            templateUrl: 'pages/companies.html', controller: 'companyController', controllerAs: 'vm'
          }
        },
        data: {
          displayName: 'Поставщики', requireLogin: true
        }
      },
        // карточка компании. Живет в отдельном detail view
      {
        name: 'company.detail', url: '/:id', parent: 'company', views: {
          'detail@': {
            templateUrl: 'pages/card/companyCard.html', controller: 'companyDetailController', controllerAs: 'vm'
          }
        },
        data: {
          requireLogin: true, displayName: '{{company.name|nvl:"Создание"}}'
        },
        resolve: {
          company: function ($stateParams, resolveService) {
            return resolveService.getCompany($stateParams.id);
          }
        }
      },
        // страница галлереии для компании (отображает баннер)
      {
        name: 'company.detail.gallery', parent: 'company.detail', url: '/gallery',
        views: {
          'main@': {
            templateUrl: 'pages/gallery.html', controller: 'galleryController', controllerAs: 'vm'
          }
        },
        data: {
          requireLogin: true, displayName: 'Изображения'
        }
      },

      /*=================================== ТОВАР ====================================*/
      {
        name: 'item', url: '/item', // при переходе на этот state переадресовываем его сразу на списочную страницу
        deepStateRedirect: {
          default: "item.list"
        }
      },
      {
        name: 'item.list', url: '/list?:companyId:orderId', views: {
        'main@': {
          templateUrl: 'pages/items.html', controller: 'itemController', controllerAs: 'vm'
        }
      }, data: {
        displayName: 'Товар', requireLogin: true
      }, sticky: true, deepStateRedirect: true, resolve: {
        companies: function (resolveService) {
          return resolveService.getCompanyMap();
        }
      }
      },
      {
        name: 'item.detail', parent: 'item', url: '/:id', views: {
          'detail@': {
            templateUrl: 'pages/card/itemCard.html', controller: 'itemDetailController', controllerAs: 'vm'
          }
        }, data: {
          requireLogin: true, displayName: '{{item.name|nvl:"Создание"}}'
        }, resolve: {
          item: function ($stateParams, resolveService) {
            return resolveService.getItemDetail($stateParams.id);
          }, companies: function (resolveService) {
            return resolveService.getCompanyMap();
          }, statuses: function ($stateParams, resolveService) {
            return resolveService.getItemStatusMap();
          }
        }
      },

      /*=================================== ГАЛЕРЕЯ ====================================*/
      {
        name: 'item.detail.gallery', parent: 'item.detail', url: '/gallery', views: {
        'main@': {
          templateUrl: 'pages/gallery.html', controller: 'galleryController', controllerAs: 'vm'
        }
      }, data: {
        requireLogin: true, displayName: 'Изображения'
      }
      }, /* Страница редактирования конкретного изображения */
      {
        name: 'item.detail.gallery.crop', parent: 'item.detail.gallery', url: '/:imageId/crop', views: {
        'main@': {
          templateUrl: 'pages/crop.html', controller: 'cropController'
        }
      }, data: {
        requireLogin: true, displayName: 'Редактирование изображения',
      }
      },

      /*=================================== INSTAGRAM ====================================*/
      {
        name: 'instagram', url: '/instagram', views: {
        'main@': {
          templateUrl: 'pages/instagram.html', controller: 'instagramController', controllerAs: 'vm'
        }
      }, data: {
        requireLogin: true, displayName: 'Instagram'
      }
      },

      /*=================================== КАТЕГОРИИ ====================================*/
      {
        name: 'category',
        url: '/category',
        views: {
          'main@': {
            templateUrl: 'pages/category.html', controller: 'categoryController', controllerAs: 'vm'
          }
        },
        data: {
          displayName: 'Категории',
          requireLogin: true
        },
        resolve: {
          categoryNodes: function ($stateParams, resolveService) {
            return resolveService.getCategoryTreeData();
          },
          categories: function ($stateParams, resolveService) {
            return resolveService.getCategoryMap();
          }
        }
      },
      {
        name: 'category.card',
        url: '/:name',
        parent: 'category',
        views: {
          'detail@': {
            templateUrl: 'pages/card/categoryCard.html',
            controller: 'categoryCardController',
            controllerAs: 'vm'
          }
        },
        data: {
          displayName: '{{category.name|nvl:"Создание"}}',
          requireLogin: true
        },
        resolve: {
          rootCategories: function ($stateParams, resolveService) {
            return resolveService.getCategoryMap();
          },
          category: function ($stateParams, resolveService) {
            return resolveService.getCategory($stateParams.name);
          },
          categoryItems: function (resolveService, $stateParams) {
            return resolveService.getCategoryItems($stateParams.name)
          }
        }
      },

      /*=================================== АВТОРИЗАЦИЯ * ====================================*/
      {
        name: 'login', url: '/login', views: {
        'main@': {
          templateUrl: 'pages/login.html', controller: 'loginController'
        }
      }, data: {
        requireLogin: false, displayName: false
      }
      }]
  }
};
