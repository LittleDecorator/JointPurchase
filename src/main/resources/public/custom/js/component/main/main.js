(function () {
	angular.module('main', []);
})();

(function () {
	'use strict';

	angular.module('main')
			.controller('mainController', ['$scope', '$rootScope', '$window', '$state', '$stateParams', 'authService', 'dataResources', 'jwtHelper', 'store', 'eventService', '$timeout', '$mdSidenav','$mdUtil', '$log', 'modal', '$mdToast', 'resolveService', '$location','$q',
				function ($scope, $rootScope, $window, $state, $stateParams, authService, dataResources, jwtHelper, store, eventService, $timeout, $mdSidenav,$mdUtil, $log, modal, $mdToast, resolveService, $location, $q) {

					$rootScope.toast = $mdToast.simple().position('top right').hideDelay(5000);

					var templatePath = "pages/fragment/menu/";
					var mvm = this;

					mvm.initCart = initCart;
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
					mvm.querySearch   = querySearch;
					mvm.searchItem = searchItem;
					mvm.searchKeyPress = searchKeyPress;
					mvm.toggleSideFilter = toggleSideFilter;
					mvm.openSearch = openSearch;

					mvm.THUMB_URL = "media/image/thumb/";
					mvm.PREVIEW_URL = "media/image/preview/";
					mvm.VIEW_URL = "media/image/view/";
					mvm.ORIG_URL = "media/image/";
					mvm.search = {criteria:null};
					mvm.uname = "";
					mvm.password = "";
					mvm.width = $window.innerWidth;
					mvm.isCollapsed = false;
					mvm.showContent = false;
					mvm.cart = null;
					mvm.notifications = 0;
					mvm.auth = authService;
					// удаление всех promises для login из сервиса, отмена login'а
					mvm.cancel = $scope.$dismiss;
					mvm.states        = [];
					mvm.selectedItem  = null;
					mvm.searchText    = null;
					mvm.lockSideFilter = false;

					/**
					 * Обработка нажатия Enter для поиска
					 * @param keyCode
					 */
					function searchKeyPress(keyCode) {
						if (keyCode == 13) {
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
						if (mvm.cart == null) {
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

						// Показываем тост и добавляем действие
						$mdToast.show(toast).then(function(response) {
							if ( response == 'ok' ) {
								goto('cart');
							}
						});
					}

					/**
					 * Очистка корзины
					 */
					function clearCart() {
						store.remove('cart');
						$scope.cart = {cou: 0, content: []};
						$scope.initCart();
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
						clearCookieInfo();
						store.remove('cart');
						mvm.cart = {cou: 0, content: []};
						$state.go('home');
					}

					/**
					 * Обрабатывает выбор в боковом меню товаров.
					 * Распространяет событие "onFilter"
					 * @param node
					 */
					function filterProduct(node) {
						if($mdSidenav('left').isOpen() && mvm.width < 1530){
							$mdSidenav('left').close();
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
							if(item.cou == 0) {
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
					function removeFromCart(idx){
						var item_cou = mvm.cart.content[idx].cou;
						mvm.cart.content.splice(idx, 1);
						mvm.cart.cou = mvm.cart.cou - item_cou;
						store.set("cart", mvm.cart);
						if (mvm.cart && mvm.cart.cou == 0) {
							mvm.showContent = false;
						}
					}

					/**
					 * Видимо переход из бокового меню
					 * @param name
					 */
					//TODO: перевесить на слушателя события, а само событие генерить при попытке изменения URL
					function goto(name) {
						//закрываем панель меню
						$mdSidenav('menu').close().then(function(){
							$state.go(name);
						});
					}

					/**
					 * Проверка что требуемый state является текущим
					 * @param name
					 * @returns {boolean}
					 */
					function isCurrent(name) {
						if(name){
							return $state.current.name == name;
						} else {
							return false;
						}
					}

					/**
					 * Для перехода в карточку из корзины
					 * @param id
					 */
					function itemView(id) {
						$state.go("catalog.detail", {itemId: id});
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
					function searchItem (){
						if(mvm.search.criteria){
							// обновляем state т.к имя могло измениться
							if($state.current == 'search'){
								$state.go('search', {criteria: mvm.search.criteria},{notify:false}).then(function(){
									$stateParams.criteria = name.search.criteria;
									$rootScope.$broadcast('$refreshBreadcrumbs',$state);
								});
							} else {
								$state.go('search', {criteria: mvm.search.criteria});
							}
						}
					}

					/**
					 * Search for states... use $timeout to simulate
					 * remote dataservice call.
					 */
					function querySearch (query) {
						// без искуственных задержек
						// return query ? dataResources.catalog.search.get({criteria: query}).$promise : mvm.states;

						// искуственно добавим задержку в 2 сек
						var results = query ? dataResources.catalog.search.get({criteria: query}).$promise : mvm.states, deferred;
						if(!helpers.isArray(results)){
							deferred = $q.defer();
							$timeout(function () { deferred.resolve( results ); }, Math.random() * 2000, false);
							return deferred.promise;
						} else {
							return results;
						}

					}

					/**
					 * Показ\сокрытие панели меню
					 */
					function toggleMenu(){
						$mdSidenav("menu").toggle().then(function(){
							if($mdSidenav("left").isOpen()){
								$mdSidenav("left").close();
							}
						});
					}

					function toggleSideFilter(){
						$mdSidenav("left").toggle()
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
							templateUrl: "pages/modal/searchModal.html",
							className: 'ngdialog-theme-default fixed-full-width',
							closeByEscape: true,
							closeByDocument: true,
							controller: "searchController"
						});

						dialog.closePromise.then(function (output) {
							if (output.value && output.value != '$escape') {
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
					 * Инициализация информационной панели
					 */
					function initInfoPanel(){
						if (localStorage.getItem('hideInfo')) {
							mvm.isInfoPanelClosed = localStorage.getItem('hideInfo');
						} else {
							mvm.isInfoPanelClosed = false;
						}
					}

					/**
					 * Основная инициализация
					 */
					function initMenu(){
						//slider content (for now left only root's)
						dataResources.categoryMenu.get(function (data) {
							mvm.nodes = data;
						});

						// получим кол-во новых уведомлений
						// resolveService.getNewNotifications().then(function(data){
						// 	mvm.notifications = data.result;
						// });
					}

					/**
					 * Основная инициализация
					 */
					function init(){
						angular.element($window).bind('resize', function () {
							mvm.width = $window.innerWidth;
							$scope.$digest();
						});

						initCart();
						initMenu();
						initInfoPanel();

						// показываем боковое меню категорий
						if($location.$$url.includes("catalog")){
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
					function afterMenuInclude() {}
					/* подтверждение аутентификации, получение token'а */
					//TODO: перенести в login контроллер
					$scope.$on('onLogin', function () {
						dataResources.authLogin.post(eventService.data,
								function (response) {
									if (response && response.token) {
										var token = response.token;
										var decodedToken = jwtHelper.decodeToken(token);

										/* clear old menu */
										clearCookieInfo();

										//try get customer name
										dataResources.customer.get({id: decodedToken.jti}, function (data) {
											$rootScope.currentUser.name = data.firstName;
											$rootScope.currentUser.role = decodedToken.role;
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

					$scope.$on('onSideHide', function () {

						var isCatalog = eventService.data;
						// если переходим на не "Каталог", то скрываем панель
						if(!isCatalog  && ($mdSidenav('left').isOpen() || $mdSidenav('left').isLockedOpen)){
							mvm.lockSideFilter = false;
							$mdSidenav('left').close();
						}
						
						//после закрытия проверяем должена ли быть панель фильтрации фиксированна слева
						var shouldLock = (isCatalog && mvm.width > 1530);
						if(shouldLock!=mvm.lockSideFilter){
							mvm.lockSideFilter = shouldLock;
						}
					});

					init();

				}])

})();
