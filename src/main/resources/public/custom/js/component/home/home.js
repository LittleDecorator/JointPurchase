(function () {
  angular.module('home', []);
})();

(function () {
  'use strict';

  angular.module('home')
      .controller('homeController', ['$scope', 'dataResources', '$state', '$timeout', '$mdToast', '$rootScope', 'store',
        function ($scope, dataResources, $state, $timeout, $mdToast, $rootScope, store) {

          var vm = this;

          vm.linkClick = linkClick;
          vm.menuClick = menuClick;
          vm.subscribe = subscribe;
          vm.loadImages = loadImages;
          vm.scrollDown = scrollDown;
          vm.isSubscribed = isSubscribed;
          vm.unsubscribe = unsubscribe;

          vm.links = [// { icon: 'mail' },
            // { icon: 'message' },
            {icon: 'facebook', url: ''}, {icon: 'vk', url: 'https://vk.com/club68247236'}, {icon: 'instagram', url: ''}];

          vm.menus = [{name: 'Каталог', ref: 'catalog'}, {name: 'О нас', ref: 'about'}, {name: 'Контакты', ref: 'contact'}, {name: 'Доставка', ref: 'delivery'}, {name: 'Акции', ref: 'stock'}];

          vm.subscriber = {id: null, email: null, subjectId: null, active: true, dateAdd: null};

          vm.forms = {};

          vm.images = [];

          function linkClick($index) {
            //TODO: add opened socials in new tab
          }

          function menuClick($index) {
            $state.go(vm.menus[$index].ref);
          }

          function subscribe() {
            if (vm.forms.homeForm.$dirty) {
              if (vm.forms.homeForm.$valid) {
                dataResources.subscriber.post(vm.subscriber).$promise.then(function (data) {
                  vm.subscriber = data;
                  store.set("subscriber", vm.subscriber);
                  $mdToast.show($rootScope.toast.textContent('Подписка оформлена').theme('success'));
                }, function (error) {
                  console.log(error);
                })
              }
            }
          }

          function unsubscribe() {
            dataResources.subscriber.delete({id: vm.subscriber.id}).$promise.then(function (data) {
              vm.subscriber = {id: null, email: null, subjectId: null, active: true, dateAdd: null};
              store.remove("subscriber");
              $mdToast.show($rootScope.toast.textContent('Подписка отменена').theme('success'));
            }, function (error) {
              console.log(error);
            })
          }

          function loadImages() {
            // TODO: Выключим, пока не придумаем легкий запрос для получения instagram изображений
            // vm.images = dataResources.instagram.image.all();
          }

          function isSubscribed() {
            var subscriber = store.get("subscriber");
            if (helpers.isArray(subscriber) && $rootScope.currentUser.email) {
              subscriber = dataResources.subscriber.get({mail: $rootScope.currentUser.email});
            }
            if (!helpers.isArray(subscriber)) vm.subscriber = subscriber;
          }

          loadImages();
          isSubscribed();

          /**
           * Скроллирование до следующей секции
           */
          function scrollDown() {
            $('#main').animate({scrollTop: $('#nature').offset().top - 50}, 500, 'linear');
          }

          // $timeout(function() {
          // 	$('.carousel.carousel-slider').carousel({fullWidth: true});
          // }, 800);
        }]);
})();
