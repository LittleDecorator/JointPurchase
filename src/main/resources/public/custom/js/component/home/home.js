(function () {
  angular.module('home', []);
})();

(function () {
  'use strict';

  angular.module('home')
      .controller('homeController', ['$scope', 'dataResources', '$state', '$timeout', '$mdToast', '$rootScope', 'store','$window',
        function ($scope, dataResources, $state, $timeout, $mdToast, $rootScope, store, $window) {

          var mvm = $scope.$parent.mvm;
          var vm = this;

          var translator = new T2W("EN_US");
          var brandCarousel;
          var sellersCarousel;
          var saleCarousel;

          var sellersScroll;
          var saleScroll;

          vm.linkClick = linkClick;
          vm.menuClick = menuClick;
          vm.subscribe = subscribe;
          vm.loadImages = loadImages;
          vm.scrollDown = scrollDown;
          vm.isSubscribed = isSubscribed;
          vm.unsubscribe = unsubscribe;
          vm.getRandomColor = getRandomColor;
          vm.toCatalogByCategory = toCatalogByCategory;
          vm.toCatalogByCompany = toCatalogByCompany;
          vm.toItemDetail = toItemDetail;
          vm.nextBrand = nextBrand;
          vm.prevBrand = prevBrand;
          vm.nextSeller = nextSeller;
          vm.prevSeller = prevSeller;
          vm.loadTopSellers = loadTopSellers;
          vm.scrollLeft = scrollLeft;
          vm.scrollRight = scrollRight;

          vm.links = [// { icon: 'mail' },
            // { icon: 'message' },
            {icon: 'facebook', url: ''}, {icon: 'vk', url: 'https://vk.com/club68247236'}, {icon: 'instagram', url: ''}];

          vm.menus = [{name: 'Каталог', ref: 'catalog'}, {name: 'О нас', ref: 'about'}, {name: 'Контакты', ref: 'contact'}, {name: 'Доставка', ref: 'delivery'}, {name: 'Акции', ref: 'stock'}];
          vm.subscriber = {id: null, email: null, subjectId: null, active: true, dateAdd: null};
          vm.forms = {};
          vm.images = [];
          vm.categories = mvm.nodes[0].nodes;
          vm.topSellers = [];

          /**
           *
           */
          function loadTopSellers(){
              dataResources.catalog.best.all().$promise.then(function (data) {
                angular.forEach(data, function (item, key) {
                  item.idx = '#'+translator.toWords(++key)+'!';
                  vm.topSellers.push(item);
                });

                $timeout(function() {
                  // инициализация карусели лидеров продаж
                  sellersCarousel = $('#bestsalers .carousel.carousel-slider');
                  var sellersOps = {fullWidth: true};
                  if(mvm.width < 601){
                    sellersOps.draggable = false;
                  }
                  sellersCarousel.carousel(sellersOps);
                }, 100);

              })
          }

          /**
           *
           * @param $index
           */
          function linkClick($index) {
            //TODO: add opened socials in new tab
          }

          /**
           *
           * @param $index
           */
          function menuClick($index) {
            $state.go(vm.menus[$index].ref);
          }

          /**
           *
           * @param category
           */
          function toCatalogByCategory(category){
            $state.go('catalog.type', {name: category.name, type: 'category'});
          }

          function toCatalogByCompany(){
            var companyName = brandCarousel.find('.active').attr('id')
            $state.go('catalog.type', {name: companyName, type: 'company'});
          }

          function toItemDetail(itemName){
            $state.go('catalog.detail', {itemName: itemName});
          }

          /**
           *
           */
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

          /**
           *
           */
          function unsubscribe() {
            dataResources.subscriber.delete({id: vm.subscriber.id}).$promise.then(function (data) {
              vm.subscriber = {id: null, email: null, subjectId: null, active: true, dateAdd: null};
              store.remove("subscriber");
              $mdToast.show($rootScope.toast.textContent('Подписка отменена').theme('success'));
            }, function (error) {
              console.log(error);
            })
          }

          /**
           *
           */
          function loadImages() {
            // TODO: Выключим, пока не придумаем легкий запрос для получения instagram изображений
            vm.images = dataResources.instagram.image.all();
          }

          /**
           *
           */
          function isSubscribed() {
            var subscriber = store.get("subscriber");
            if (helpers.isArray(subscriber) && $rootScope.currentUser.email) {
              subscriber = dataResources.subscriber.get({mail: $rootScope.currentUser.email});
            }
            if (!helpers.isArray(subscriber)) vm.subscriber = subscriber;
          }

          /**
           * Получение случайного цвета
           */
          function getRandomColor() {
            // случайная величина из диапазона
            function randomNumber(min, max){
              return Math.floor(Math.random() * (max - min + 1) + min);
            }
            // получение случайного числа для цветовой величины
            function randomByte(){
              return randomNumber(0, 255);
            }

            var r = randomNumber(50, 200);
            var g = randomNumber(50, 200);
            var b = randomNumber(50, 250);

            return 'rgba('+ [r, g, b, 0.7].join(',')+')';
          }

          /**
           * Скроллирование до следующей секции
           */
          function scrollDown() {
            $('#main').animate({scrollTop: $('#nature').offset().top - 50}, 500, 'linear');
          }

          /**
           *
           */
          function nextBrand(){
            brandCarousel.carousel('next');
          }

          /**
           *
           */
          function prevBrand(){
            brandCarousel.carousel('prev');
          }

          function nextSeller(){
            sellersCarousel.carousel('next');
          }

          function prevSeller(){
            sellersCarousel.carousel('prev');
          }

          function scrollRight() {
            var container = $('.md-virtual-repeat-container.md-orient-horizontal .md-virtual-repeat-scroller');
            sideScroll(container,'right',500);
          }

          function scrollLeft() {
            var container = $('.md-virtual-repeat-container.md-orient-horizontal .md-virtual-repeat-scroller');
            sideScroll(container,'left',500);
          }

          function sideScroll(element, direction, distance){
              if(direction === 'left'){
                element.animate({scrollLeft: element.scrollLeft() - distance}, "slow");
              } else {
                element.animate({scrollLeft: element.scrollLeft() + distance}, "slow");
              }
          }

          /*============= INITIALIZATION ============*/
          loadImages();
          isSubscribed();
          loadTopSellers();


          $timeout(function() {
            // инициализация карусели производителя
            brandCarousel = $('#brand .carousel.carousel-slider');
            brandCarousel.carousel({fullWidth: true});

          	// проставим цвета плиткам категорий
            angular.forEach($('.categoryBlock'), function(elem){
              elem.style.backgroundColor = getRandomColor();
            })
          }, 1000);
        }]);
})();
