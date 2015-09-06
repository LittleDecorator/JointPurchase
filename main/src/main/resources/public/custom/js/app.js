(function(){
    angular.module('purchase', ['ngCookies','ui.router', 'ui.bootstrap','angularBootstrapNavTree' ,'ngResource', 'angularFileUpload', 'angular-jwt',
        'purchase.controllers','purchase.directives','purchase.factories','purchase.filters','purchase.services']);
})();

(function() {
    angular.module('purchase')

        .config(['$stateProvider', '$urlRouterProvider','$httpProvider',function ($stateProvider, $urlRouterProvider,$httpProvider) {
            $httpProvider.interceptors.push('authInterceptor');

            // For any unmatched url, redirect to /state1
            $urlRouterProvider.otherwise('/');

            // Now set up the states
            angular.forEach(route.getRoutes(),function(route){
                $stateProvider.state(route);
            });
        }])

        .run(['$state', '$rootScope', '$location','$cookies','loginModal',function ($state, $rootScope, $location,$cookies,loginModal) {

            //$cookies.remove('token');
            $rootScope.currentUser = {};
            $rootScope.menu = {};

            $rootScope.$on('$locationChangeSuccess', function (event, toState, toParams) {
                //$rootScope.actualLocation = $location.path();

                //hide side menu
                $('.slide-outt').removeClass('slide-inn');

                $rootScope.actualLocation = $location.$$url;
            });

            //Capturing attempted go to locked pages
            /*$rootScope.$on('$stateChangeStart', function (event, toState, toParams) {
             console.log("change state");
             //console.log(toState);
             var requireLogin = toState.data.requireLogin;

             if (requireLogin && typeof $cookies.get('token') == 'undefined' ) {
             event.preventDefault();
             // get me a login modal!
             loginModal()
             .then(function () {
             console.log("module -> stateChangeStart -> login -> then");
             return $state.transitionTo(toState.name, toParams);
             })
             .catch(function () {
             //return $state.go('welcome');
             console.log("module -> stateChangeStart -> login -> catch");
             return $state.transitionTo('home');
             });
             }
             });*/

            $rootScope.$watch(
                function () {
                    return $location.path()
                },
                function (newLocation, oldLocation) {
                    //console.log("actual -> "+$rootScope.actualLocation);
                    if ($rootScope.actualLocation == newLocation) {
                        $rootScope.$broadcast('locBack', true);
                    }
                }
            );
        }])
})();