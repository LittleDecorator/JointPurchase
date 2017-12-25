(function () {
   angular.module('purchase', ['ui.router', 'ngDialog', 'ngMaterial', 'ngMessages', 'ngSanitize', 'angularBootstrapNavTree', 'ngResource', 'mdPickers', 'ngMask', 'ui.router.metatags', 'angularFileUpload', 'angular-jwt', 'md-steppers', 'backToTop', 'purchase.controllers', 'purchase.directives', 'purchase.factories', 'purchase.filters', 'purchase.services', 'ngBreadcrumbs', 'infinite-scroll', 'purchase.validators', 'ct.ui.router.extras']);
})();

(function () {
   angular.module('purchase')
   /**
    * Конфигуратор приложения
    */
       .config(['$stateProvider', '$stickyStateProvider', '$urlRouterProvider', '$httpProvider', '$mdThemingProvider', '$mdIconProvider', '$locationProvider', 'UIRouterMetatagsProvider', function ($stateProvider, $stickyStateProvider, $urlRouterProvider, $httpProvider, $mdThemingProvider, $mdIconProvider, $locationProvider, UIRouterMetatagsProvider) {

          // объявленем interceptor для каждого http запроса
          $httpProvider.interceptors.push('authInterceptor');

          UIRouterMetatagsProvider
          // .setTitlePrefix('prefix - ')
              .setTitleSuffix(' | Grimmstory')
              .setDefaultTitle('Grimmstory')
              .setDefaultDescription('description')
              .setDefaultKeywords('grimms toys игрушки деревянные купить')
              .setDefaultRobots('index')
              .setStaticProperties({
                 // 'fb:app_id': 'your fb app id',
                 'og:site_name': 'grimmstory'
              })
              .setOGURL(true);

          // правило redirect для всех неизвестных адресов
          $urlRouterProvider.otherwise('/');

          // наполним мапу маршрутов
          angular.forEach(route.getRoutes(), function (route) {
             $stateProvider.state(route);
          });

          // $stickyStateProvider.enableDebug(true);
          // включение режима красивого url
          $locationProvider.html5Mode({
             enabled: true, requireBase: false, rewriteLinks: false
          });

          // определим основные md* стили
          $mdThemingProvider.theme('success', 'default').dark();
          $mdThemingProvider.theme('error', 'default').dark();
          $mdThemingProvider.theme('warn', 'default').dark();

          $mdIconProvider
              .icon('mail', 'custom/icons/email.svg', 24)
              .icon('message', 'custom/icons/message-text.svg', 24)
              .icon('vk', 'custom/icons/vk-box.svg', 24)
              .icon('facebook', 'custom/icons/facebook-box.svg', 24)
              .icon('bug', 'custom/icons/bug.svg', 24)
              .icon('instagram', 'custom/icons/instagram.svg', 24)
              .icon('xls', 'custom/icons/file-excel.svg', 24);
       }])

       .run(['$state', '$rootScope', '$location', '$templateRequest', 'ngDialog', '$mdDialog', 'eventService', 'MetaTags', function ($state, $rootScope, $location, $templateRequest, ngDialog, $mdDialog, eventService, MetaTags) {

          $rootScope.MetaTags = MetaTags;

          var urls = ['custom/icons/instagram.svg', 'custom/icons/email.svg', 'custom/icons/message-text.svg', 'custom/icons/vk-box.svg', 'custom/icons/facebook-box.svg', 'custom/icons/bug.svg', 'custom/icons/file-excel.svg'];

          angular.forEach(urls, function (url) {
             $templateRequest(url);
          });

          $rootScope.currentUser = {};
          $rootScope.menu = {};

          /**
           * Слушатель события успешного изменения URL
           */
          $rootScope.$on('$locationChangeSuccess', function (event, toState, fromState, toParams, fromParams) {

             /* emulate click outside of mobile-menu */
             $("#sidenav-overlay").trigger("click");

             $rootScope.actualLocation = $location.$$url;

             // проверим должна ли быть доступна боковая панель (игнорируются переходы НЕ с/на страницу каталога)
             var fromCatalog = fromState.indexOf("catalog") !== -1;
             var toCatalog = toState.indexOf("catalog") !== -1;
             if (fromCatalog) {
                if (!toCatalog) eventService.onSideHide(false);
             } else {
                if (toCatalog) eventService.onSideHide(true);
             }

          });

          /**
           * Слушатель события попытки изменения URL
           */
          $rootScope.$on('$locationChangeStart', function (event, toState, toParams, fromState, fromParams) {

             /* Закрываем все диалоговые окна */
             var isNgDialogOpen = ngDialog.getOpenDialogs().length > 0;
             var isMdDialogOpen = angular.element(document.body).hasClass('md-dialog-is-showing');

             if (isNgDialogOpen) {
                ngDialog.closeAll();
                event.preventDefault();
                return;
             }

             if (isMdDialogOpen) {
                $mdDialog.cancel();
                event.preventDefault();
             }

          });

          /**
           * Слушатель события начала изменения STATE.
           *
           * Если token просрочен или клиент не авторизован, тогда сбрасываем все данные и отправляем на главную страницу
           */
          $rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams, options) {

             //$timeout(function(){
             //    $("#spinner").fadeTo(800, 1, function(){ $(this).show()});
             //},10);

             var token = localStorage.getItem('token');
             if (!token) {
                localStorage.removeItem('menus');
                localStorage.removeItem('subMenus');
                $rootScope.currentUser = {};
                $rootScope.menus = [];
                if (toState.data && toState.data.requireLogin) {
                   event.preventDefault();
                   $state.go('home');
                }
             } else {
                if (fromState.name !== toState.parent && fromState.name !== 'cart') {
                   localStorage.removeItem(fromState.name);
                }
             }
          });

          /**
           * Наблюдатель возврата на предыдущую страницу
           */
          $rootScope.$watch(
              function () {
                 return $location.path()
              },
              function (newLocation, oldLocation) {
                 $rootScope.newLocation = newLocation;
                 $rootScope.oldLocation = oldLocation;
                 if ($rootScope.actualLocation === newLocation) {
                    $rootScope.$broadcast('locBack', true);

              }
          });

       }])
})();