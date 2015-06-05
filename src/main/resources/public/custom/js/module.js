var purchase = angular.module('purchase', ['ngCookies','ui.router', 'ui-breadcrumbs', 'ui.bootstrap', 'ngResource', 'angularFileUpload', 'ui.tree']);

// configure our routes
    purchase.config(function ($stateProvider, $urlRouterProvider,$httpProvider) {

        $httpProvider.interceptors.push('authInterceptor');

        // For any unmatched url, redirect to /state1
        $urlRouterProvider.otherwise('/');

        // Now set up the states
        $stateProvider.state(route.home)
            .state(route.about)
            .state(route.orders)
            .state(route.person)
            .state(route.gallery)
            .state(route.company)
            .state(route.contact)
            .state(route.login)
            .state(route.item)
            .state(route.detail)
            .state(route.product)
            .state(route.registration);
    });

    purchase.run(function ($state, $rootScope, $location,$cookies,loginModal) {

        /*$cookies.remove('token');*/
        $rootScope.currentUser = {};

        $rootScope.$on('$locationChangeSuccess', function () {
            $rootScope.actualLocation = $location.path();
        });

        //Capturing attempted state changes
        $rootScope.$on('$stateChangeStart', function (event, toState, toParams) {
            console.log("change state");
            console.log(toState);
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
        });

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
    });