(function(){
    angular.module('main',[]);
})();

(function() {
    'use strict';

    angular.module('main')
        .controller('mainController',['$scope','$rootScope','$window','$state','authService','dataResources','jwtHelper','store','eventService','$timeout',
            function ($scope,$rootScope, $window, $state, authService, dataResources,jwtHelper, store,eventService,$timeout) {
                console.log("Enter main controller");

                $scope.uname = "";
                $scope.password = "";

                $scope.width = $window.innerWidth;
                
                angular.element($window).bind('resize', function(){
                
                    $scope.width = $window.innerWidth;
                
                    // manuall $digest required as resize event
                    // is outside of angular
                    $scope.$digest();
                });

                $scope.isCollapsed = false;
                $scope.showContent = false;

                if(helpers.viewport().width < 1350){
                    $scope.mobile = true;
                } else {
                    $scope.mobile = false;
                }

                $scope.initCart = function(){
                    $scope.cart = {cou:0,content:[]};
                    var c = store.get("cart");
                    if(!helpers.isArray(c)) {
                        $scope.cart = store.get("cart");
                    }
                };

                //restore token
                if(localStorage.getItem('token')){
                    var decodedToken = jwtHelper.decodeToken(localStorage.getItem('token'));
                    dataResources.customer.get({id:decodedToken.jti},function(data){
                        $rootScope.currentUser.name = data.firstName;
                    });
                    $rootScope.currentUser.roles = decodedToken.roles;
                }

                //add new item to cart
                $scope.addToCart = function(item){
                    //check if item already in cart
                    var item_in = {};
                    if($scope.cart==null){
                        $scope.cart = {cou:0,content:[]};
                    }
                    if($scope.cart.content.length>0){
                        item_in = helpers.findInArrayById($scope.cart.content,item.id);
                    }
                    if(helpers.isEmpty(item_in)){
                        $scope.cart.content.push(angular.extend({cou:1},item));
                    } else {
                        item_in.cou++;
                    }
                    $scope.cart.cou++;
                    store.set("cart",$scope.cart);
                    Materialize.toast(item.name + ' добавлен в корзину', 2500);
                };

                $scope.clearCart = function(){
                    store.remove('cart');
                    $scope.cart = {cou:0,content:[]};
                    $scope.initCart();
                    $state.reload();
                };

                function clearCookieInfo(){
                    localStorage.removeItem('token');
                    localStorage.removeItem('menus');
                    localStorage.removeItem('subMenus');
                    store.remove('cart');
                    $rootScope.currentUser = {};
                    $rootScope.menus = [];
                    $rootScope.subMenus = [];
                    $scope.cart = {cou:0,content:[]};
                    $rootScope.currentUser = {};
                    $scope.refreshMenu();
                }

                //явный logout через меню
                $scope.logout = function(){
                    /*dataResources.authLogout.post().$promise.then(function(data){
                        /!* clear all data *!/
                        clearCookieInfo();
                        /!* go to main page *!/
                        // $state.transitionTo("home");
                    });*/
                    clearCookieInfo();
                };

                //обновление меню после login/logout
                //TODO: исправить зависимость меню от роли
                $scope.refreshMenu = function(){
                    $scope.menu = [];
                    angular.forEach(menu.getMenu(),function(item){
                        if(item.displayCondition){
                            if(item.displayCondition.admin && item.displayCondition.auth && authService.isAuth()){
                                $scope.menu.push(item);
                            } else if(item.displayCondition.auth && authService.isAuth()){
                                $scope.menu.push(item);
                            } else if(!item.displayCondition.auth && !authService.isAuth()){
                                $scope.menu.push(item);
                            }
                        } else {
                            $scope.menu.push(item);
                        }
                    })
                };

                //удаление всех promises для login из сервиса, отмена login'а
                $scope.cancel = $scope.$dismiss;

                //подтверждение аутентификации, получение token'а
                $scope.$on('onLogin',function(){
                    dataResources.authLogin.post(eventService.data,
                        function (response) {
                            if(response && response.token){
                                var token = response.token;
                                console.log(token);
                                var decodedToken = jwtHelper.decodeToken(token);

                                /* clear old menu */
                                clearCookieInfo();

                                //try get customer name
                                dataResources.customer.get({id:decodedToken.jti},function(data){
                                    $rootScope.currentUser.name = data.firstName;
                                });

                                $rootScope.currentUser.roles = decodedToken.roles;

                                /* store expired token */
                                localStorage.setItem('token',token);
                                //set current user promises
                                $timeout(eventService.onComplete(),100);
                            } else {
                                eventService.onComplete();
                            }
                        }, function(){
                            eventService.onComplete();
                        });
                });

                //$scope.mainMenu = $scope.refreshMenu();
                $scope.auth = authService;

                //init and restore cart content
                $scope.initCart();

                //slider content (for now left only root's)
                dataResources.categoryMenu.get(function(data){
                    $scope.nodes= data;
                });

                dataResources.categoryRootMap.get().$promise.then(function(data){
                    $scope.categoryFilter = data;
                    console.log(data);
                });

                /* handle event in side menu .Broadcast event */
                $scope.filterProduct = function(node){
                    eventService.onFilter(node);
                };

                $timeout(function() {
                    $(".button-collapse").sideNav();
                    $(".collapsible").collapsible();
                    $('.dropdown-button').dropdown();
                }, 100);

                //TEMPORARY ADD HERE
                $scope.itemView = function(id){
                    $state.go("product.detail", {itemId: id});
                };

                //increment item cou in order
                $scope.incrementCou = function (idx) {
                    var item = $scope.cart.content[idx];
                    item.cou++;
                    $scope.cart.cou++;
                    store.set("cart",$scope.cart);
                };

                //decrement item cou in order
                $scope.decrementCou = function (idx) {
                    var item = $scope.cart.content[idx];
                    if (item.cou > 0) {
                        item.cou--;
                        $scope.cart.cou--;
                        store.set("cart",$scope.cart);
                    }
                };

                $scope.removeFromCart = function(idx){
                    var item_cou = $scope.cart.content[idx].cou;
                    $scope.cart.content.splice(idx,1);
                    $scope.cart.cou = $scope.cart.cou - item_cou;
                    store.set("cart",$scope.cart);
                    if($scope.cart && $scope.cart.cou==0){
                        $scope.showContent = false;
                    }
                };

                $scope.handleClick = function(event){
                    if(event.toElement.className.indexOf('search') == -1){
                        eventService.onSearchHide();
                    }
                };

        }]);

})();
