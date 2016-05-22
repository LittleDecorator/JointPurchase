(function(){
    angular.module('purchase', ['ui.router', 'ngDialog','ngMaterial','ngTouch', 'angularBootstrapNavTree', 'ngResource', 'angularFileUpload', 'angular-jwt',
        'purchase.controllers', 'purchase.directives', 'purchase.factories', 'purchase.filters', 'purchase.services', 'ngBreadcrumbs', 'infinite-scroll', 'purchase.validators']);
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

        .run(['$state', '$rootScope', '$location','$timeout','modal',function ($state, $rootScope, $location,$timeout,modal) {

            $rootScope.currentUser = {};
            $rootScope.menu = {};

            var dialog = null;

            /* when location changed we save actual location */
            $rootScope.$on('$locationChangeSuccess', function (event, toState, toParams) {

                //hide side menu filter
                // var slider = $('.slide-outt');
                // if(slider.hasClass('slide-inn')){
                //     slider.removeClass('slide-inn');
                //     $('.button-collapse').sideNav('hide');
                // }

                /* emulate click outside of mobile-menu */
                // $("#sidenav-overlay").trigger("click");

                $rootScope.actualLocation = $location.$$url;

                // $timeout(function() {
                //     if(dialog){
                //         dialog.close();
                //     }
                // },1000);
            });

            /* if token expired or not login then move to mane page and clear all data */
            $rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams, options) {
                if(localStorage.getItem('token') == undefined){
                    localStorage.removeItem('menus');
                    localStorage.removeItem('subMenus');
                    $rootScope.currentUser = {};
                    $rootScope.menus = [];
                    $rootScope.subMenus = [];
                    if (toState.data.requireLogin ) {
                        event.preventDefault();
                        $state.transitionTo('home');
                    }
                } else {
                    if(fromState.name!=toState.parent){
                        localStorage.removeItem(fromState.name);
                    }
                    // if(( toState.name!='login' && toState.name!='home')){
                    //     dialog = modal({templateUrl:"pages/template/spinner.html",controller:null,closeByEscape:false});
                    //     $timeout(function(){
                    //         $('.ngdialog-content').css('width','30%');
                    //     },100);
                    // }
                }
            });

            $rootScope.$watch(
                function () {
                    return $location.path()
                },
                function (newLocation, oldLocation) {
                    $rootScope.newLocation = newLocation;
                    $rootScope.oldLocation = oldLocation;
                    //console.log("actual -> "+$rootScope.actualLocation);
                    if ($rootScope.actualLocation == newLocation) {
                        $rootScope.$broadcast('locBack', true);

                    }
                }
            );
        }])
})();