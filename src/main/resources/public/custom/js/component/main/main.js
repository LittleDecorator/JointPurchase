(function () {
	angular.module('main', []);
})();

(function () {
	'use strict';

	angular.module('main')
			.controller('mainController', ['$scope', '$rootScope', '$window', '$state', '$stateParams', 'authService', 'dataResources', 'jwtHelper', 'store', 'eventService', '$timeout', '$mdSidenav', '$log', 'modal', '$mdToast','$q',
				function ($scope, $rootScope, $window, $state, $stateParams, authService, dataResources, jwtHelper, store, eventService, $timeout, $mdSidenav, $log, modal, $mdToast, $q) {
					console.log("Enter main controller");

					$scope.THUMB_URL = "media/image/thumb/";
					$scope.PREVIEW_URL = "media/image/preview/";
					$scope.VIEW_URL = "media/image/view/";
					$scope.ORIG_URL = "media/image/";

					$scope.search = {criteria:null};

					$rootScope.toast = $mdToast.simple().position('top right').hideDelay(5000);

					var templatePath = "pages/fragment/menu/";

					$scope.uname = "";
					$scope.password = "";

					$scope.width = $window.innerWidth;

					angular.element($window).bind('resize', function () {
						$scope.width = $window.innerWidth;
						$scope.$digest();
					});

					$scope.isCollapsed = false;
					$scope.showContent = false;

					$scope.initCart = function () {
						$scope.cart = {cou: 0, content: []};
						var c = store.get("cart");
						if (!helpers.isArray(c)) {
							//$scope.cart = store.get("cart");
							$scope.cart = c;
						}
					};

					//restore token
					if (localStorage.getItem('token')) {
						var decodedToken = jwtHelper.decodeToken(localStorage.getItem('token'));
						dataResources.customer.get({id: decodedToken.jti}, function (data) {
							$rootScope.currentUser = data;
							$rootScope.currentUser.name = data.firstName;
						});
						$rootScope.currentUser.roles = decodedToken.roles;
					}

					if (localStorage.getItem('tab')) {
						var tab = localStorage.getItem('tab');
						console.log(tab);
						$('.tab > a').removeClass('active');
						$('#' + tab + '.tab > a').addClass('active');
						$('ul.tabs').tabs();
					}

					//add new item to cart
					$scope.addToCart = function (item) {
						// подготовим тост
						var toast = $mdToast.simple()
								.textContent(item.name + ' добавлен в корзину')
								.action('ПЕРЕЙТИ')
								.hideDelay(3500)
								.highlightAction(true)
								.highlightClass('md-accent')
								.position('top right');

						//check if item already in cart
						var item_in = {};
						if ($scope.cart == null) {
							$scope.cart = {cou: 0, content: []};
						}
						if ($scope.cart.content.length > 0) {
							item_in = helpers.findInArrayById($scope.cart.content, item.id);
						}
						if (helpers.isEmpty(item_in)) {
							$scope.cart.content.push(angular.extend({cou: 1}, item));
						} else {
							item_in.cou++;
						}
						$scope.cart.cou++;
						store.set("cart", $scope.cart);

						/* Показываем тост и добавляем действие */
						$mdToast.show(toast).then(function(response) {
							if ( response == 'ok' ) {
								$state.go('cart');
							}
						});
					};

					$scope.clearCart = function () {
						store.remove('cart');
						$scope.cart = {cou: 0, content: []};
						$scope.initCart();
						//ЗАЧЕМ БЫЛ RELOAD????
						// $state.reload();
					};

					function clearCookieInfo() {
						localStorage.removeItem('token');
						localStorage.removeItem('menus');
						localStorage.removeItem('subMenus');

						$rootScope.currentUser = {};
						$rootScope.menus = [];
						$rootScope.subMenus = [];

						$rootScope.currentUser = {};
					}

					//явный logout через меню
					$scope.logout = function () {
						clearCookieInfo();
						store.remove('cart');
						$scope.cart = {cou: 0, content: []};
						$state.go('home');
					};

					//удаление всех promises для login из сервиса, отмена login'а
					$scope.cancel = $scope.$dismiss;

					//подтверждение аутентификации, получение token'а
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
										});

										$rootScope.currentUser.roles = decodedToken.roles;

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

					//$scope.mainMenu = $scope.refreshMenu();
					$scope.auth = authService;

					//init and restore cart content
					$scope.initCart();

					//slider content (for now left only root's)
					dataResources.categoryMenu.get(function (data) {
						$scope.nodes = data;
					});

					//dataResources.categoryRootMap.get().$promise.then(function(data){
					//    $scope.categoryFilter = data;
					//    console.log(data);
					//});


					/* handle event in side menu .Broadcast event */
					/**
					 * Обрабатывает выбор в боковом меню товаров.
					 * Распространяет событие "onFilter"
					 * @param node
					 */
					$scope.filterProduct = function (node) {
						$mdSidenav('left').close();
						eventService.onFilter(node);
					};

					$timeout(function () {
						$('.swipebox').swipebox();
					}, 10);

					//TEMPORARY ADD HERE
					$scope.itemView = function (id) {
						$state.go("catalog.detail", {itemId: id});
					};

					//increment item cou in order
					$scope.incrementCou = function (idx) {
						var item = $scope.cart.content[idx];
						item.cou++;
						$scope.cart.cou++;
						store.set("cart", $scope.cart);
					};

					//decrement item cou in order
					$scope.decrementCou = function (idx) {
						var item = $scope.cart.content[idx];
						if (item.cou > 0) {
							item.cou--;
							$scope.cart.cou--;
							if(item.cou == 0) {
								$scope.removeFromCart(idx);
							} else {
								store.set("cart", $scope.cart);
							}
						}
					};

					$scope.removeFromCart = function (idx) {
						var item_cou = $scope.cart.content[idx].cou;
						$scope.cart.content.splice(idx, 1);
						$scope.cart.cou = $scope.cart.cou - item_cou;
						store.set("cart", $scope.cart);
						console.log($scope.cart);
						if ($scope.cart && $scope.cart.cou == 0) {
							$scope.showContent = false;
						}
					};

					$scope.handleClick = function (event) {
						if (event.toElement.className.indexOf('search') == -1) {
							eventService.onSearchHide();
						}
					};

					$scope.goto = function (name) {
						console.log(name);
						localStorage.setItem('tab', name);
						helpers.centerItFixedWidth('#' + name, 'div#menu-tabs');
						$state.go(name);
						if ($mdSidenav('menu').isOpen()) {
							$mdSidenav('menu').close();
						}
					};

					$scope.getMenuTemplateUrl = function () {
						if ($scope.width < 720) {
							return templatePath + "menu-sm.html";
						}
						if ($scope.width < 1025) {
							return templatePath + "menu-md.html";
						} else {
							return templatePath + "menu-lg.html";
							// return templatePath + "menu-sm.html";
						}
					};

					$scope.afterInclude = function () {
						$timeout(function () {
							$('.dropdown-button').dropdown();
							$(".button-collapse").sideNav();
							$('ul.tabs').tabs();
							//$('md-toolbar .indicator').css('background-color','transparent');
						}, 100);
					};

					$scope.isCurrent = function (name) {
						console.log(name);
						console.log($state);
						return $state.current.name == name;
					};

					/*========================== INFO PANEL ============================*/

					if (localStorage.getItem('hideInfo')) {
						$scope.isInfoPanelClosed = localStorage.getItem('hideInfo');
					} else {
						$scope.isInfoPanelClosed = false;
					}

					$scope.hideInfoPanel = function () {
						localStorage.setItem('hideInfo', true);
						$scope.isInfoPanelClosed = true;
					};

					/*======================== SEARCH ==============================*/

					//TODO: Нужно получать сразу все имена, и фильтровать на клиенте
					$scope.states        = [];
					$scope.selectedItem  = null;
					$scope.searchText    = null;
					$scope.querySearch   = querySearch;

					// нажатие кнопки поиск
					$scope.searchItem = function(){
						if($scope.search.criteria){
							/* refresh state because name can be changed */
							if($state.current == 'search'){
								$state.go('search', {criteria: $scope.search.criteria},{notify:false}).then(function(){
									// $scope.item.id = data.result;
									$stateParams.criteria = $scope.search.criteria;
									$rootScope.$broadcast('$refreshBreadcrumbs',$state);
								});
							} else {
								$state.go('search', {criteria: $scope.search.criteria});
							}
						}
					};

					$scope.keyPress = function(keyCode) {
						if (keyCode == 13) {
							$scope.searchItem();
						}
					};

					/**
					 * Search for states... use $timeout to simulate
					 * remote dataservice call.
					 */
					function querySearch (query) {
						console.log("query");
						//TODO: показывать загрузку
						return query ? dataResources.catalog.search.get({criteria: query}).$promise : $scope.states;
					}

					/*========================== SIDE NAV ============================*/
					//TODO: move all this to directive
					$scope.toggleLeft = buildToggler('left');
					$scope.toggleMenu = buildToggler('menu');

					function debounce(func, wait) {
						var timer;
						return function debounced() {
							var context = $scope,
									args = Array.prototype.slice.call(arguments);
							$timeout.cancel(timer);
							timer = $timeout(function () {
								timer = undefined;
								func.apply(context, args);
							}, wait || 10);
						};
					}

					function buildToggler(navID) {
						return function () {
							$mdSidenav(navID)
									.toggle()
									.then(function () {
										$log.debug("toggle " + navID + " is done");
									});
						}
					}
					
					// модальное окно поиска
					$scope.openSearch = function (event) {
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
