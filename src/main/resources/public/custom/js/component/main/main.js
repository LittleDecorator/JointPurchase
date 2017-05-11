(function () {
	angular.module('main', []);
})();

(function () {
	'use strict';

	angular.module('main')
			.controller('mainController', ['$scope', '$rootScope', '$window', '$state', '$stateParams', 'authService', 'dataResources', 'jwtHelper', 'store', 'eventService', '$timeout', '$mdSidenav', '$log', 'modal', '$mdToast', 'resolveService',
				function ($scope, $rootScope, $window, $state, $stateParams, authService, dataResources, jwtHelper, store, eventService, $timeout, $mdSidenav, $log, modal, $mdToast, resolveService) {

					var templatePath = "pages/fragment/menu/";
					var mvm = this;

					mvm.initCart = initCart;
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
					mvm.afterInclude = afterInclude;
					mvm.isCurrent = isCurrent;
					mvm.initCartAndMenu = initCartAndMenu;
					mvm.hideInfoPanel = hideInfoPanel;
					mvm.itemView = itemView;
					mvm.toggleMenu = toggleMenu;

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

					$rootScope.toast = $mdToast.simple().position('top right').hideDelay(5000);

					angular.element($window).bind('resize', function () {
						mvm.width = $window.innerWidth;
						$scope.$digest();
					});

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
								.highlightClass('md-accent')
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
								$state.go('cart');
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
						$mdSidenav('left').close();
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
					function removeFromCart(idx) {
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
					function goto(name) {
						localStorage.setItem('tab', name);
						helpers.centerItFixedWidth('#' + name, 'div#menu-tabs');
						$state.go(name);
						if ($mdSidenav('menu').isOpen()) {
							$mdSidenav('menu').close();
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
					function afterInclude() {
						$timeout(function () {
							$('.dropdown-button').dropdown();
							$(".button-collapse").sideNav();
							$('ul.tabs').tabs();
						}, 100);
					}

					/**
					 * Проверка что требуемый state является текущим
					 * @param name
					 * @returns {boolean}
					 */
					function isCurrent(name) {
						return $state.current.name == name;
					}

					/**
					 * Основная инициализация
					 */
					function initCartAndMenu(){
						initCart();

						//slider content (for now left only root's)
						dataResources.categoryMenu.get(function (data) {
							mvm.nodes = data;
						});

						// получим кол-во новых уведомлений
						resolveService.getNewNotifications().then(function(data){
							mvm.notifications = data.result;
						});

					}

					/**
					 * Для перехода в карточку из корзины
					 * @param id
					 */
					function itemView(id) {
						$state.go("catalog.detail", {itemId: id});
					}

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
										console.log("OnLogin", $rootScope.currentUser);
									} else {
										$mdToast.show($rootScope.toast.textContent('Неверные Логин/Пароль').theme('error'));
									}
								}, function () {
									$mdToast.show($rootScope.toast.textContent('Неверные Логин/Пароль').theme('error'));
								});
					});

					initCartAndMenu();

					/* инициализация swipebox */
					$timeout(function () {
						$('.swipebox').swipebox();
					}, 10);

					// ВРЕМЕННО УБРАНО
					// $scope.handleClick = function (event) {
					// 	if (event.toElement.className.indexOf('search') == -1) {
					// 		eventService.onSearchHide();
					// 	}
					// };

					// ВРЕМЕННО УБРАНО
					// if (localStorage.getItem('tab')) {
					// 	var tab = localStorage.getItem('tab');
					// 	$('.tab > a').removeClass('active');
					// 	$('#' + tab + '.tab > a').addClass('active');
					// 	$('ul.tabs').tabs();
					// }

					/*========================== INFO PANEL ============================*/

					if (localStorage.getItem('hideInfo')) {
						mvm.isInfoPanelClosed = localStorage.getItem('hideInfo');
					} else {
						mvm.isInfoPanelClosed = false;
					}

					/**
					 * Скрытие info панели касательно тестового режима
					 */
					function hideInfoPanel() {
						localStorage.setItem('hideInfo', true);
						mvm.isInfoPanelClosed = true;
					}

					/*======================== SEARCH ==============================*/

					mvm.querySearch   = querySearch;
					mvm.searchItem = searchItem;
					mvm.keyPress = keyPress;
					mvm.states        = [];
					mvm.selectedItem  = null;
					mvm.searchText    = null;

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
					 * Обработка нажатия Enter
					 * @param keyCode
					 */
					function keyPress(keyCode) {
						if (keyCode == 13) {
							searchItem();
						}
					}

					/**
					 * Search for states... use $timeout to simulate
					 * remote dataservice call.
					 */
					function querySearch (query) {
						//TODO: показывать загрузку
						return query ? dataResources.catalog.search.get({criteria: query}).$promise : $scope.states;
					}

					/*========================== SIDE NAV ============================*/
					//TODO: move all this to directive
					mvm.debounce = debounce;
					mvm.buildToggler = buildToggler;
					mvm.openSearch = openSearch;

					mvm.toggleLeft = buildToggler('left');
					// mvm.toggleMenu = buildToggler('menu');

					/**
					 * Отложенный вызов
					 * @param func
					 * @param wait
					 * @returns {debounced}
					 */
					function debounce(func, wait) {
						var timer;
						return function debounced() {
							var context = $scope, args = Array.prototype.slice.call(arguments);
							$timeout.cancel(timer);
							timer = $timeout(function () {
								timer = undefined;
								func.apply(context, args);
							}, wait || 10);
						};
					}

					function toggleMenu(){
						$mdSidenav("menu").toggle().then(function(){
							$mdSidenav("left").close();
						});
					}

					/**
					 * Построитель Toggle для sideNav
					 * @param navID
					 * @returns {Function}
					 */
					function buildToggler(navID) {
						return function () {
							$mdSidenav(navID)
									.toggle()
									.then(function () {
										$log.debug("toggle " + navID + " is done");
									});
						}
					}

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

				}])

})();
