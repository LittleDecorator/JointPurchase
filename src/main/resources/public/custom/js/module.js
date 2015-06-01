var purchase = angular.module('purchase', ['ui.router', 'ui-breadcrumbs', 'ui.bootstrap', 'ngResource', 'angularFileUpload', 'ui.tree']);

// configure our routes
    purchase.config(function ($stateProvider, $urlRouterProvider) {

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
            .state(route.product);

    }).run(function ($state, $rootScope, $location) {
        $state.transitionTo('home');

        $rootScope.$on('$locationChangeSuccess', function () {
            $rootScope.actualLocation = $location.path();
        });

        $rootScope.$watch(function () {
            return $location.path()
        }, function (newLocation, oldLocation) {
            //console.log("new -> "+newLocation);
            //console.log("old -> "+oldLocation);
            //console.log("actual -> "+$rootScope.actualLocation);
            if ($rootScope.actualLocation == newLocation) {
                $rootScope.$broadcast('locBack', true);
            }
        });
    });