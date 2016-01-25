(function(){
    angular.module('main',[]);
})();

(function() {
    'use strict';

    angular.module('main')
        .controller('mainController',['$scope','$rootScope','$cookies','$state','loginModal','authService','dataResources','jwtHelper','store','eventService','$timeout',
            function ($scope,$rootScope,$cookies, $state, loginModal,authService, dataResources,jwtHelper, store,eventService,$timeout) {
                console.log("Enter main controller");

                $scope.uname = "";
                $scope.password = "";

                $scope.isCollapsed = false;
                $scope.showContent = false;

                if(helpers.viewport().width < 1350){
                    $scope.view = "mobile";
                } else {
                    $scope.view = "normal";
                }

                angular.element(document).ready(function(){
                    $('.modal-trigger').leanModal();
                });

                $scope.initCart = function(){
                    $scope.cart = {cou:0,content:[]};
                    var c = store.get("cart");
                    if(!helpers.isArray(c)) {
                        $scope.cart = store.get("cart");
                    }
                };

                //restore token
                if($cookies.get('token')){
                    var decodedToken = jwtHelper.decodeToken($cookies.get('token'));
                    //$rootScope.currentUser.name = decodedToken.jti;
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

                //нажата кнопка меню login      -- uncomment if use modal login
                /*$scope.login = function(){
                    $scope.modal = loginModal();
                };*/

                //явный logout через меню
                $scope.logout = function(){
                    $cookies.remove('token');
                    store.remove('cart');
                    $scope.cart = {cou:0,content:[]};
                    $rootScope.currentUser = {};
                    $scope.refreshMenu();
                    $state.transitionTo("home");
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
                                $cookies.put('token',token);
                                var decodedToken = jwtHelper.decodeToken(token);
                                //try get customer name
                                dataResources.customer.get({id:decodedToken.jti},function(data){
                                    $rootScope.currentUser.name = data.firstName;
                                });

                                $rootScope.currentUser.roles = decodedToken.roles;
                                //set current user promises
                                //$scope.refreshMenu();
                                $timeout(eventService.onComplete(),500);
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

                dataResources.categoryMenu.get(function(data){
                    $scope.nodes= data;

                    //$(".button-collapse").sideNav();

                    /* на всякий случай */
                    //$(".collapsible").collapsible();
                });

                $scope.filterProduct = function(node){
                    eventService.onFilter(node);
                };

                $timeout(function() {
                    $(".button-collapse").sideNav();
                    //$(".collapsible").collapsible();
                    $('.dropdown-button').dropdown();
                }, 800);

                console.log($scope);


                //TEMPORARY ADD HERE

                $scope.itemView = function(id){
                    console.log("itemView");
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
                    console.log($scope);
                    var item_cou = $scope.cart.content[idx].cou;
                    $scope.cart.content.splice(idx,1);
                    $scope.cart.cou = $scope.cart.cou - item_cou;
                    store.set("cart",$scope.cart);

                    console.log($scope.cart);
                    if($scope.cart && $scope.cart.cou==0){
                        $scope.showContent = false;
                        console.log("NO ITEMS in CART")
                    }
                };

                $scope.handleClick = function(event){
                    console.log(event);
                    console.log(event.toElement.className);

                    if(event.toElement.className.indexOf('search') == -1){
                        eventService.onSearchHide();
                    }
                };

        }]);

})();
