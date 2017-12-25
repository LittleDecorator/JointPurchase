(function () {
   angular.module('main', []);
})();

(function () {
   'use strict';

   angular.module('main')
       .controller('mainController', ['$scope', '$rootScope', '$window', '$state', '$stateParams', 'authService', 'dataResources', 'jwtHelper', 'store', 'eventService', '$timeout', '$mdSidenav', '$mdUtil', '$log', 'modal', '$mdToast', 'resolveService', '$location', '$q', '$deepStateRedirect', function ($scope, $rootScope, $window, $state, $stateParams, authService, dataResources, jwtHelper, store, eventService, $timeout, $mdSidenav, $mdUtil, $log, modal, $mdToast, resolveService, $location, $q, $deepStateRedirect) {

          $rootScope.toast = $mdToast.simple().position('top right').hideDelay(3500);

          var templatePath = "pages/fragment/menu/";
          var devNotionDialog = null;
          var bugReportDialog = null;
          var mvm = this;

          mvm.initCart = initCart;
          mvm.initWishList = initWishList;
          mvm.initMenu = initMenu;
          mvm.addToCart = addToCart;
          mvm.clearCart = clearCart;
          mvm.clearCookieInfo = clearCookieInfo;
          mvm.logout = logout;
          mvm.filterProduct = filterProduct;
          mvm.incrementCou = incrementCou;
          mvm.decrementCou = decrementCou;
          mvm.removeFromCart = removeFromCart;
          mvm.goto = goto;
          mvm.getMenuTemplateUrl = getMenuTemplateUrl;
          mvm.afterMenuInclude = afterMenuInclude;
          mvm.isCurrent = isCurrent;
          mvm.initInfoPanel = initInfoPanel;
          mvm.hideInfoPanel = hideInfoPanel;
          mvm.itemView = itemView;
          mvm.toggleMenu = toggleMenu;
          mvm.toggleSideFilter = toggleSideFilter;
          mvm.clearSearch = clearSearch;
          mvm.querySearch = querySearch;
          mvm.searchItem = searchItem;
          mvm.searchKeyPress = searchKeyPress;
          mvm.toggleSideFilter = toggleSideFilter;
          mvm.openSearch = openSearch;
          mvm.showDevNotion = showDevNotion;
          mvm.showBugReport = showBugReport;
          mvm.openSubPanel = openSubPanel;
          mvm.closeSubPanel = closeSubPanel;
          mvm.getSubMenu = getSubMenu;
          mvm.toCatalog = toCatalog;
          mvm.requestToList = requestToList;
          mvm.preorderToList = preorderToList;
          mvm.getAddToWishListButtonLabel = getAddToWishListButtonLabel;
          mvm.showWishlistModal = showWishlistModal;

          mvm.THUMB_URL = "media/image/thumb/";
          mvm.PREVIEW_URL = "media/image/preview/";
          mvm.VIEW_URL = "media/image/view/";
          mvm.ORIG_URL = "media/image/";
          mvm.search = {criteria: null};
          mvm.uname = "";
          mvm.password = "";
          mvm.width = $window.innerWidth;
          mvm.isCollapsed = false;
          mvm.showContent = false;
          mvm.cart = null;
          mvm.wishListEmail = null;
          mvm.wishlists = 0;
          mvm.notifications = 0;
          mvm.auth = authService;
          // удаление всех promises для login из сервиса, отмена login'а
          mvm.cancel = $scope.$dismiss;
          mvm.states = [];
          mvm.selectedItem = null;
          mvm.searchText = null;
          mvm.lockSideFilter = false;
          // нужно ли отображать view карточки
          mvm.showDetail = false;
          mvm.currentSubMenu = null;

          /**
           * Получение label'а кнопок.
           * @param item
           */
          function getAddToWishListButtonLabel(item) {
             var label = "";
             if (item.inWishlist) {
                label = item.status.id === 'preorder' ? 'Заказан' : 'Отложен';
             } else {
                label = item.status.id === 'preorder' ? 'Заказать' : 'Отложить';
             }
             return label;
          }

          /**
           * Показ модального для "отложить"
           * @param item
           * @param wClass
           */
          function preorderToList(item) {
             if (item.inWishlist) {
                goto('wishlist', {id: item.id});
             }
             var wClass = mvm.width < 601 ? 'w-80' : 'w-30';
             mvm.showWishlistModal(item, mvm.width < 601 ? "pages/fragment/modal/preorderModal.html" : "pages/modal/preorderModal.html", 'ngdialog-theme-default ' + wClass)
          }

          /**
           * Добавление товара в список желаемого
           * @param item
           * @param templateUrl
           * @param className
           */
          function showWishlistModal(item, templateUrl, className) {
             var toast = $mdToast.simple().position('top right').hideDelay(3000);
             if (!mvm.wishListEmail) {
                //определим модальное окно
                var wishListDialog = modal({
                   templateUrl: templateUrl, className: className, closeByEscape: true, controller: "wishlistController as vm", data: item
                });

                // Слушатель закрытия модального
                wishListDialog.closePromise.then(function (output) {

                   // запомним stash client email
                   if (!mvm.wishListEmail && output.value) {
                      mvm.wishListEmail = output.value.email;
                      store.set("wishListEmail", mvm.wishListEmail);
                   } else {
                      return;
                   }

                   item.inWishlist = true;
                   mvm.getAddToWishListButtonLabel(item);
                   $mdToast.show(toast.textContent('Заявка на заказ товара [' + item.name + '] принята').theme('info'));
                });
             } else {
                var stashed = {id: null, itemId: item.id, subjectId: null, email: mvm.wishListEmail};
                dataResources.wishlist.core.post(stashed).$promise.then(function (data) {
                   $mdToast.show(toast.textContent('Товар [' + item.name + '] отложен').theme('info'));
                   item.inWishlist = true;
                   mvm.getAddToWishListButtonLabel(item);
                }, function (error) {
                   $mdToast.show(toast.textContent('Неудалось добавить товар в список желаемого').theme('error'));
                });
             }
          }

          /**
           * Показ модального для "заказать"
           * @param item
           * @param wClass
           */
          function requestToList(item) {
             if (item.inWishlist) {
                goto('wishlist', {id: item.id});
             }
             var wClass = mvm.width < 601 ? 'w-80' : 'w-30';
             mvm.showWishlistModal(item, mvm.width < 601 ? "pages/fragment/modal/requestItemModal.html" : "pages/modal/requestItemModal.html", 'ngdialog-theme-default ' + wClass)
          }

          /**
           * Обработка нажатия Enter для поиска
           * @param keyCode
           */
          function searchKeyPress(keyCode) {
             if (keyCode === 13) {
                searchItem();
             }
          }

          /**
           * восстановление token'а
           */
          if (localStorage.getItem('token')) {
             var decodedToken = jwtHelper.decodeToken(localStorage.getItem('token'));
             dataResources.customer.get({id: decodedToken.jti}, function (data) {
                $rootScope.currentUser = data;
                $rootScope.currentUser.name = data.firstName;
                $rootScope.currentUser.role = decodedToken.role;
             });
          }

          /**
           * Добавление товара в корзину
           * @param item
           */
          function addToCart(item) {
             // подготовим тост
             var toast = $mdToast.simple()
                 .textContent(item.name + ' добавлен в корзину')
                 .action('ПЕРЕЙТИ')
                 .hideDelay(3500)
                 .highlightAction(true)
                 .highlightClass('md-default')
                 .position('top right');

             // проверка что товар уже в корзине
             var item_in = {};
             if (!mvm.cart) {
                mvm.cart = {cou: 0, content: []};
             }
             if (mvm.cart.content.length > 0) {
                item_in = helpers.findInArrayById(mvm.cart.content, item.id);
             }
             if (helpers.isEmpty(item_in)) {
                mvm.cart.content.push(angular.extend({cou: 1}, item));
             } else {
                item_in.cou++;
             }
             mvm.cart.cou++;
             store.set("cart", mvm.cart);
             console.log(mvm.cart)

             // Показываем тост и добавляем действие
             $mdToast.show(toast).then(function (response) {
                if (response === 'ok') {
                   goto('cart');
                }
             }, function (error) {
                // гасим предыдущий вызов
                error.deferred.reject();
             });
          }

          /**
           * Очистка корзины
           */
          function clearCart() {
             console.log('clearCart')
             store.remove('cart');
             mvm.cart = {cou: 0, content: []};
             mvm.initCart();
          }

          /**
           * Очищаем информацию из storage
           */
          function clearCookieInfo() {
             localStorage.removeItem('token');
             localStorage.removeItem('menus');
             localStorage.removeItem('subMenus');

             $rootScope.currentUser = {};
             $rootScope.menus = [];
             $rootScope.subMenus = [];

             $rootScope.currentUser = {};
          }

          /**
           * явный logout через меню
           */
          function logout() {
             if ($mdSidenav('left').isOpen() && mvm.width < 1530) {
                $mdSidenav('left').close();
             }
             if ($mdSidenav("menu").isOpen()) {
                $mdSidenav("menu").close();
                $('.subPanel').removeClass('isOpen');
             }

             clearCookieInfo();
             store.remove('cart');
             store.remove('wishListEmail');
             mvm.cart = {cou: 0, content: []};
             if($state.current.data && $state.current.data.requireLogin){
                $state.go('home');
             }
          }

          /**
           * Обрабатывает выбор в боковом меню товаров.
           * Распространяет событие "onFilter"
           * @param node
           */
          function filterProduct(node) {
             // if(node.name === $state.params.name){
             //    return;
             // }
             if ($mdSidenav('left').isOpen() && mvm.width < 1530) {
                $mdSidenav('left').close();
             }

             if ($mdSidenav("menu").isOpen()) {
                $mdSidenav("menu").close();
                $('.subPanel').removeClass('isOpen');
             }
             eventService.onFilter(node);
          }

          /**
           * Увеличение товара в корзине
           * @param idx
           */
          function incrementCou(idx) {
             var item = mvm.cart.content[idx];
             item.cou++;
             mvm.cart.cou++;
             store.set("cart", mvm.cart);
          }

          /**
           * Уменьшение товара в корзине
           * @param idx
           */
          //TODO: возможно стоит перенести в CartController
          function decrementCou(idx) {
             var item = mvm.cart.content[idx];
             if (item.cou > 0) {
                item.cou--;
                mvm.cart.cou--;
                if (item.cou === 0) {
                   removeFromCart(idx);
                } else {
                   store.set("cart", mvm.cart);
                }
             }
          }

          /**
           * Удаление товара из корзины
           * @param idx
           */
          //TODO: возможно стоит перенести в CartController
          function removeFromCart(idx) {
             var item_cou = mvm.cart.content[idx].cou;
             mvm.cart.content.splice(idx, 1);
             mvm.cart.cou = mvm.cart.cou - item_cou;
             store.set("cart", mvm.cart);
             if (mvm.cart && mvm.cart.cou === 0) {
                mvm.showContent = false;
             }
          }

          /**
           * Видимо переход из бокового меню
           * @param name
           * @param params
           */
          //TODO: перевесить на слушателя события, а само событие генерить при попытке изменения URL
          function goto(name, params) {
             //закрываем панель меню
             $mdSidenav('menu').close().then(function () {
                $('.subPanel').removeClass('isOpen');
                $deepStateRedirect.reset();
                mvm.showDetail = false;
                $state.go(name, params);
             });
          }

          /**
           *
           * @param menu
           */
          function toCatalog(menu) {
             var type;
             // определяет тип выбранного узла
             if (menu.company) {
                type = 'company';
             } else {
                type = 'category';
             }
             // переходим на страницу результата фильтрации
             $deepStateRedirect.reset();
             goto('catalog.type', {name: menu.name, type: type});
          }

          /**
           * Проверка что требуемый state является текущим
           * @param name
           * @returns {boolean}
           */
          function isCurrent(name) {
             if (name) {
                return $state.current.name === name;
             } else {
                return false;
             }
          }

          /**
           * Для перехода в карточку из корзины
           * @param name
           */
          function itemView(name) {
             if (name) {
                clearSearch();
                $state.go("catalog.detail", {itemName: name});
             }
          }

          /**
           * Скрытие info панели касательно тестового режима
           */
          function hideInfoPanel() {
             localStorage.setItem('hideInfo', true);
             mvm.isInfoPanelClosed = true;
          }

          /**
           * нажатие кнопки поиск
           */
          function searchItem() {
             if (mvm.search.criteria) {
                // обновляем state т.к имя могло измениться
                if ($state.current === 'search') {
                   $state.go('search', {criteria: mvm.search.criteria}, {notify: false}).then(function () {
                      $stateParams.criteria = name.search.criteria;
                      $rootScope.$broadcast('$refreshBreadcrumbs', $state);
                   });
                } else {
                   $state.go('search', {criteria: mvm.search.criteria});
                }
             }
          }

          function clearSearch() {
             mvm.search.criteria = null;
          }

          /**
           * Search for states... use $timeout to simulate
           * remote dataservice call.
           */
          function querySearch(query) {
             // без искуственных задержек
             // return query ? dataResources.catalog.search.get({criteria: query}).$promise : mvm.states;

             // искуственно добавим задержку в 2 сек
             var results = query ? dataResources.catalog.search.get({criteria: query}).$promise : mvm.states, deferred;
             if (!helpers.isArray(results)) {
                deferred = $q.defer();
                $timeout(function () {
                   deferred.resolve(results);
                }, Math.random() * 2000, false);
                return deferred.promise;
             } else {
                return results;
             }

          }

          /**
           * Показ\сокрытие панели меню
           */
          function toggleMenu() {
             $mdSidenav("menu").toggle().then(function () {
                // отключаем скролирование страницы
                if ($mdSidenav("left").isOpen()) {
                   $mdSidenav("left").close();
                   $('.subPanel').removeClass('isOpen');
                }
             });
          }

          /**
           * Открываем подменю
           * @param event
           */
          function openSubPanel(event) {
             var parent = $(event.currentTarget.closest('md-content'));
             mvm.currentSubMenu = parent.next('.subPanel');
             mvm.currentSubMenu.toggleClass('isOpen');
          }

          /**
           * Закрываем подменю
           * @param event
           */
          function closeSubPanel(event) {
          // function closeSubPanel() {
             var current = $(event.currentTarget.closest('md-content'));
             current.toggleClass('isOpen');
             // mvm.currentSubMenu.toggleClass('isOpen');
          }

          function toggleSideFilter() {
             $mdSidenav("left").toggle().then(function () {
             });
          }

          /**
           * Отложенное выполнение
           */
          // function isSideFilterLocked(){
          // 	var result = $mdUtil.debounce(function(){
          // 		console.log("val",val);
          // 	},300);
          // 	return result
          // }

          /**
           * Открытие модального окна поиска
           * @param event
           */
          function openSearch() {
             var dialog = modal({
                templateUrl: "pages/modal/searchModal.html", className: 'ngdialog-theme-default fixed-full-width', closeByEscape: true, closeByDocument: true, controller: "searchController"
             });

             dialog.closePromise.then(function (output) {
                if (output.value && output.value !== '$escape') {
                }
             });
          }

          /**
           * Открытие дилогового окна с информацией, что сайт в разработке
           */
          function showDevNotion(wClass) {
             devNotionDialog = modal({
                templateUrl: "pages/modal/develModal.html", className: 'ngdialog-theme-default ' + wClass, closeByEscape: true, closeByDocument: true, scope: $scope
             });

             devNotionDialog.closePromise.then(function (output) {
                if (output.value && output.value !== '$escape') {
                }
             });
          }

          function showBugReport(wClass) {
             bugReportDialog = modal({
                templateUrl: "pages/modal/bugReport.html", className: 'ngdialog-theme-default ' + wClass, closeByEscape: true, closeByDocument: true, scope: $scope
             });

             bugReportDialog.closePromise.then(function (output) {
                if (output.value && output.value !== '$escape') {
                }
             });
          }

          /*========================== INITIALIZATION ============================*/

          /**
           * Инициализация корзины
           */
          function initCart() {
             mvm.cart = {cou: 0, content: []};
             var cart = store.get("cart");
             if (!helpers.isArray(cart)) {
                mvm.cart = cart;
             }
          }

          /**
           * Инициализация списка желаемого
           */
          function initWishList() {
             var clientEmail = store.get("wishListEmail");
             if (!helpers.isArray(clientEmail)) {
                mvm.wishListEmail = clientEmail;
             }
          }

          /**
           * Инициализация информационной панели
           */
          function initInfoPanel() {
             if (localStorage.getItem('hideInfo')) {
                mvm.isInfoPanelClosed = localStorage.getItem('hideInfo');
             } else {
                mvm.isInfoPanelClosed = false;
             }
          }

          /**
           * Основная инициализация
           */
          function initMenu() {
             //slider content (for now left only root's)
             dataResources.categoryMenu.get(function (data) {
                mvm.nodes = data;
             });

             // получим кол-во новых уведомлений
             resolveService.getNewNotifications().then(function (data) {
                mvm.notifications = data.result;
             });

             // получим кол-во новых уведомлений
             resolveService.getWishlistCount().then(function (data) {
                mvm.wishlists = data.result;
             });
          }

          /**
           *
           * @param node
           */
          function getSubMenu(node) {
             mvm.submenu = node.nodes
          }

          /**
           * Основная инициализация
           */
          function init() {
             angular.element($window).bind('resize', function () {
                mvm.width = $window.innerWidth;
                $scope.$digest();
             });

             initCart();
             initWishList();
             initMenu();
             initInfoPanel();

             // показываем боковое меню категорий
             if ($location.$$url.includes("catalog")) {
                mvm.lockSideFilter = (mvm.width > 1530);
             }

          }

          /**
           * Получение шаблонов меню
           * @returns {string}
           */
          function getMenuTemplateUrl() {
             if (mvm.width < 720) {
                return templatePath + "menu-sm.html";
             }
             if (mvm.width < 1025) {
                return templatePath + "menu-md.html";
             } else {
                return templatePath + "menu-lg.html";
             }
          }

          /**
           * Событие загрузки шаблона
           */
          function afterMenuInclude() {
             // console.log("afterMenuInclude");
          }

          /* подтверждение аутентификации, получение token'а */
          //TODO: перенести в login контроллер
          $scope.$on('onLogin', function () {
             dataResources.authLogin.post(eventService.data, function (response) {
                if (response && response.token) {
                   var token = response.token;
                   var decodedToken = jwtHelper.decodeToken(token);

                   /* clear old menu */
                   clearCookieInfo();

                   //try get customer name
                   dataResources.customer.get({id: decodedToken.jti}, function (data) {
                      $rootScope.currentUser.name = data.firstName;
                      $rootScope.currentUser.role = decodedToken.role;
                      //todo: нужно сделать его больше размером
                      $mdToast.show($rootScope.toast.textContent('Добро пожаловать, ' + $rootScope.currentUser.name).theme('success'));
                   });
                   /* store expired token */
                   localStorage.setItem('token', token);
                   //set current user promises
                   $timeout(eventService.onComplete(), 100);
                } else {
                   $mdToast.show($rootScope.toast.textContent('Неверные Логин/Пароль').theme('error'));
                }
             }, function () {
                $mdToast.show($rootScope.toast.textContent('Неверные Логин/Пароль').theme('error'));
             });
          });

          /**
           * Событие необходимости скрытия боковой панели
           */
          $scope.$on('onSideHide', function () {

             var isCatalog = eventService.data;
             // если переходим на не "Каталог", то скрываем панель
             if (!isCatalog && ($mdSidenav('left').isOpen() || $mdSidenav('left').isLockedOpen)) {
                mvm.lockSideFilter = false;
                $mdSidenav('left').close();
             }

             //после закрытия проверяем должена ли быть панель фильтрации фиксированна слева
             var shouldLock = (isCatalog && mvm.width > 1530);
             if (shouldLock !== mvm.lockSideFilter) {
                mvm.lockSideFilter = shouldLock;
             }
          });

          init();

       }])

})();
