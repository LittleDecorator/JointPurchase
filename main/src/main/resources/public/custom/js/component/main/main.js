(function(){
    angular.module('main',[]);
})();

(function() {
    'use strict';

    angular.module('main')
        .controller('mainController',['$scope','$rootScope','$cookies','$state','loginModal','authService','dataResources','jwtHelper','store','factoryModal',
            function ($scope,$rootScope,$cookies, $state, loginModal,authService, dataResources,jwtHelper, store,factoryModal) {
            console.log("Enter main controller");

            $scope.uname = "";
            $scope.password = "";

            $scope.isCollapsed = false;

            //$scope.boolChangeClass = false;

            angular.element(document).ready(function(){
                // the "href" attribute of .modal-trigger must specify the modal ID that wants to be triggered
                $('.modal-trigger').leanModal();
            });

            $scope.initCart = function(){
                $scope.cart = {cou:0,content:[]};
                console.log($scope.cart);
                console.log(typeof $scope.cart);
                var c = store.get("cart");
                console.log(c);
                if(helpers.isArray(c)) {
                    console.log(c);
                } else {
                    console.log(c);
                    $scope.cart = store.get("cart");
                    console.log($scope.cart);
                }
            };

            //restore token
            if($cookies.get('token')){
                var decodedToken = jwtHelper.decodeToken($cookies.get('token'));
                console.log(decodedToken);
                $rootScope.currentUser.name = decodedToken.jti;
                $rootScope.currentUser.roles = decodedToken.roles;
            }

            //add new item to cart
            $scope.addToCart = function(item){
                //console.log($scope.cart);
                //check if item already in cart
                var item_in = helpers.findInArrayById($scope.cart.content,item.id);
                if(helpers.isEmpty(item_in)){
                    $scope.cart.content.push(angular.extend({cou:1},item));
                } else {
                    item_in.cou++;
                }
                $scope.cart.cou++;
                store.set("cart",$scope.cart);
            };

            $scope.clearCart = function(){
                store.remove('cart');
                $scope.cart = null;
                $scope.initCart();
                $state.reload();
            };

            //нажата кнопка меню login
            $scope.login = function(){
                //angular.element('#modal1').openModal();
                /*$scope.modal = factoryModal.open({
                 /!*templateUrl: 'pages/template/loginModal.html',*!/
                 templateUrl:'pages/template/loginModal.html',
                 controller: 'mainController'
                 });*/


                $scope.modal = loginModal()
                    .then(function () {
                        console.log("then");
                        $scope.refreshMenu();

                        //return $state.transitionTo(toState.name, toParams);
                    })
                    .catch(function () {
                        console.log("catch");
                        //return $state.go('welcome');
                        console.log("direct login failed");
                        //return $state.transitionTo('home');
                    });

            };

            //явный logout через меню
            $scope.logout = function(){
                $cookies.remove('token');
                store.remove('cart');
                $scope.cart = null;
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
            $scope.submit = function () {
                console.log($scope);
                console.log("submit user");
                console.log($scope.uname);
                dataResources.authLogin.post({name: $scope.uname,password:$scope.password},
                    function (response) {
                        console.log("like normal");
                        if(response && response.token){
                            var token = response.token;
                            $cookies.put('token',token);
                            console.log(token);
                            var decodedToken = jwtHelper.decodeToken(token);
                            console.log(decodedToken);
                            $rootScope.currentUser.name = decodedToken.jti;
                            $rootScope.currentUser.roles = decodedToken.roles;
                            //set current user promises
                            $scope.$close($scope.uname);
                            $scope.refreshMenu();
                        } else {
                            console.log("null came");
                            $scope.$close();
                        }
                    }, function(){
                        console.log("some error");
                        $scope.$dismiss;
                        //$scope.$close("fla");
                    });

            };

            $scope.mainMenu = $scope.refreshMenu();
            $scope.auth = authService;

            //init and restore cart content
            $scope.initCart();

            console.log($scope);

        }]);
})();
