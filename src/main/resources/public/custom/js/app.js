(function(){
    angular.module('purchase', ['ui.router', 'ngDialog','ngMaterial', 'ngMessages','angularBootstrapNavTree', 'ngResource','mdPickers','ngMask', 'angularFileUpload', 'angular-jwt','md-steppers','backToTop',
        'purchase.controllers', 'purchase.directives', 'purchase.factories', 'purchase.filters', 'purchase.services', 'ngBreadcrumbs', 'infinite-scroll', 'purchase.validators']);

    // angular.module("purchase").run(["$templateCache", function (e) {
    //     e.put("partials/menu-toggle.tmpl.html", '<md-button class="md-button-toggle"\n  ng-click="toggle()"\n  aria-controls="docs-menu-{{section.name | nospace}}"\n  aria-expanded="{{isOpen()}}">\n  <div flex layout="row">\n    {{section.name}}\n    <span flex></span>\n    <span aria-hidden="true" class="md-toggle-icon"\n    ng-class="{\'toggled\' : isOpen()}">\n      <md-icon md-svg-icon="md-toggle-arrow"></md-icon>\n    </span>\n  </div>\n  <span class="md-visually-hidden">\n    Toggle {{isOpen()? \'expanded\' : \'collapsed\'}}\n  </span>\n</md-button>\n\n<ul id="docs-menu-{{section.name | nospace}}"\n  class="menu-toggle-list"\n  aria-hidden="{{!renderContent}}"\n  ng-style="{ visibility: renderContent ? \'visible\' : \'hidden\' }">\n\n  <li ng-repeat="page in section.pages">\n    <menu-link section="page"></menu-link>\n  </li>\n</ul>\n')
    // }])
})();

(function() {
    angular.module('purchase')

        /**
         * Конфигуратор приложения
         */
        .config(['$stateProvider', '$urlRouterProvider','$httpProvider','$locationProvider','$mdThemingProvider',
            function ($stateProvider, $urlRouterProvider,$httpProvider,$locationProvider, $mdThemingProvider) {

                // объявленем interceptor для каждого http запроса
                $httpProvider.interceptors.push('authInterceptor');

                // правило redirect для всех неизвестных адресов
                $urlRouterProvider.otherwise('/');

                // наполним мапу маршрутов
                angular.forEach(route.getRoutes(),function(route){
                    $stateProvider.state(route);
                });

                // включение режима красивого url
                //$locationProvider.html5Mode({
                //    enabled: true,
                //    requireBase: false,
                //    rewriteLinks: false
                //});

                // определим основные md* стили
                $mdThemingProvider.theme('success','default').dark();
                $mdThemingProvider.theme('error','default').dark();
                $mdThemingProvider.theme('warn','default').dark();
            }])

        .run(['$state', '$rootScope', '$location','$timeout','ngDialog','$templateCache','$http', '$mdDialog',
            function ($state, $rootScope, $location,$timeout,ngDialog , $templateCache, $http, $mdDialog) {

                /* попытка добавить страницу в cache */
                // $http.get('pages/fragment/items/card/item-card-sm.html').then(function(response) {
                //     $templateCache.put('item-card-sm.html', response.data);
                // });
            
                $rootScope.currentUser = {};
                $rootScope.menu = {};

                var dialog = null;

                /**
                 * Слушатель события успешного изменения адреса
                 */
                $rootScope.$on('$locationChangeSuccess', function (event, toState, toParams) {
                    //TODO: почистить метод. Возможное использование - spinner
                    //hide side menu filter
                    // var slider = $('.slide-outt');
                    // if(slider.hasClass('slide-inn')){
                    //     slider.removeClass('slide-inn');
                    //     $('.button-collapse').sideNav('hide');
                    // }

                    /* emulate click outside of mobile-menu */
                    $("#sidenav-overlay").trigger("click");

                    $rootScope.actualLocation = $location.$$url;

                    //$timeout(function() {
                    //    $("#spinner").fadeTo(800, 0, function(){ $(this).hide()});
                    //},10);

                });

                /**
                 * Слушатель события попытки изменения адреса
                 */
                $rootScope.$on('$locationChangeStart', function(event){
                    var isNgDialogOpen = ngDialog.getOpenDialogs().length > 0;
                    var isMdDialogOpen = angular.element(document.body).hasClass('md-dialog-is-showing');

                    if(isNgDialogOpen){
                        ngDialog.closeAll();
                        event.preventDefault();
                        return;
                    }

                    if(isMdDialogOpen){
                        $mdDialog.cancel();
                        event.preventDefault();
                    }
                });

                /**
                 * Слушатель события начала изменения state.
                 *
                 * Если token просрочен или клиент не авторизован, тогда сбрасываем все данные и отправляем на главную страницу
                 */
                $rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams, options) {

                    //$timeout(function(){
                    //    $("#spinner").fadeTo(800, 1, function(){ $(this).show()});
                    //},10);

                    if(localStorage.getItem('token') == undefined){
                        localStorage.removeItem('menus');
                        localStorage.removeItem('subMenus');
                        $rootScope.currentUser = {};
                        $rootScope.menus = [];
                        if (toState.data.requireLogin ) {
                            event.preventDefault();
                            $state.transitionTo('login');
                        }
                    } else {
                        if(fromState.name!=toState.parent){
                            localStorage.removeItem(fromState.name);
                        }
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