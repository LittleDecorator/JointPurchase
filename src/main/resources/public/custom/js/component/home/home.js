(function () {
   angular.module('home', []);
})();

(function () {
   'use strict';

   angular.module('home')
       .controller('homeController', ['$scope', 'dataResources', '$state', '$timeout', '$interval', '$mdToast', '$mdDialog', '$rootScope', 'store', '$window', function ($scope, dataResources, $state, $timeout, $interval, $mdToast, $mdDialog, $rootScope, store, $window) {

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
          vm.loadPosts = loadPosts;
          vm.scrollDown = scrollDown;
          vm.isSubscribed = isSubscribed;
          vm.unsubscribe = unsubscribe;
          vm.getRandomColor = getRandomColor;
          vm.toCatalogByCategory = toCatalogByCategory;
          vm.toCatalogByCompany = toCatalogByCompany;
          vm.toCatalogBySale = toCatalogBySale;
          vm.toItemDetail = toItemDetail;
          vm.nextBrand = nextBrand;
          vm.prevBrand = prevBrand;
          vm.nextSeller = nextSeller;
          vm.prevSeller = prevSeller;
          vm.nextSale = nextSale;
          vm.prevSale = prevSale;
          vm.loadTopSellers = loadTopSellers;
          vm.scrollLeft = scrollLeft;
          vm.scrollRight = scrollRight;
          vm.showPost = showPost;

          vm.links = [// { icon: 'mail' },
             // { icon: 'message' },
             {icon: 'facebook', url: ''}, {icon: 'vk', url: 'https://vk.com/club68247236'}, {icon: 'instagram', url: 'http://www.instagram.com/grimmstory'}];

          vm.menus = [{name: 'Каталог', ref: 'catalog'}, {name: 'О нас', ref: 'about'}, {name: 'Контакты', ref: 'contact'}, {name: 'Доставка', ref: 'delivery'}, {name: 'Акции', ref: 'stock'}];
          vm.subscriber = {id: null, email: null, subjectId: null, active: true, dateAdd: null};
          vm.forms = {};
          vm.posts = [];
          vm.categories = [];
          vm.topSellers = [];
          vm.sales = [];

          /**
           *
           */
          function loadTopSellers() {
             dataResources.catalog.best.all().$promise.then(function (data) {
                angular.forEach(data, function (item, key) {
                   item.idx = '#' + translator.toWords(++key) + '!';
                   vm.topSellers.push(item);
                });

                $timeout(function () {
                   // инициализация карусели лидеров продаж
                   sellersCarousel = $('#bestsalers .carousel.carousel-slider');
                   var sellersOps = {fullWidth: true};
                   if (mvm.width < 601) {
                      sellersOps.draggable = false;
                   }
                   sellersCarousel.carousel(sellersOps);
                }, 100);

             })
          }

          function loadSales(){
             dataResources.sale.all({active_only: true}).$promise.then(function (data) {
                angular.forEach(data, function (item, key) {
                   if(item.bannerId){
                      item.idx = '#' + translator.toWords(++key) + '!';
                      vm.sales.push(item);
                   }
                });

                $timeout(function () {
                   // инициализация карусели акций
                   saleCarousel = $('#sales .carousel.carousel-slider');
                   var saleOps = {fullWidth: true, duration:460};
                   saleCarousel.carousel(saleOps);

                   if(vm.sales.length > 1){
                      $interval(function(){
                         saleCarousel.carousel('next');
                      }, 8 * 1000);
                   }
                }, 300);
             });
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
          function toCatalogByCategory(category) {
             $state.go('catalog.type', {name: category.name, type: 'category'});
          }

          function toCatalogByCompany() {
             var companyName = brandCarousel.find('.active').attr('id');
             $state.go('catalog.type', {name: companyName, type: 'company'});
          }

          function toCatalogBySale() {
             var saleName = saleCarousel.find('.active').attr('id');
             $state.go('catalog.type', {name: saleName, type: 'sale'});
          }

          function toItemDetail(itemName) {
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
          function loadPosts() {
             // TODO: Выключим, пока не придумаем легкий запрос для получения instagram изображений
             vm.posts = dataResources.instagram.fullPosts.all();
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
             function randomNumber(min, max) {
                return Math.floor(Math.random() * (max - min + 1) + min);
             }

             // получение случайного числа для цветовой величины
             function randomByte() {
                return randomNumber(0, 255);
             }

             var r = randomNumber(50, 200);
             var g = randomNumber(50, 200);
             var b = randomNumber(50, 250);

             return 'rgba(' + [r, g, b, 0.7].join(',') + ')';
          }

          /**
           * Скроллирование до следующей секции
           */
          function scrollDown() {
             $('#main').animate({scrollTop: $('#categories').offset().top - 50}, 500, 'linear');
          }

          /**
           *
           */
          function nextBrand() {
             brandCarousel.carousel('next');
          }

          /**
           *
           */
          function prevBrand() {
             brandCarousel.carousel('prev');
          }

          function nextSale() {
             saleCarousel.carousel('next');
          }

          function prevSale() {
             saleCarousel.carousel('prev');
          }

          function nextSeller() {
             sellersCarousel.carousel('next');
          }

          function prevSeller() {
             sellersCarousel.carousel('prev');
          }

          function scrollRight() {
             var container = $('.md-virtual-repeat-container.md-orient-horizontal .md-virtual-repeat-scroller');
             sideScroll(container, 'right', 500);
          }

          function scrollLeft() {
             var container = $('.md-virtual-repeat-container.md-orient-horizontal .md-virtual-repeat-scroller');
             sideScroll(container, 'left', 500);
          }

          /**
           *
           * @param element
           * @param direction
           * @param distance
           */
          function sideScroll(element, direction, distance) {
             if (direction === 'left') {
                element.animate({scrollLeft: element.scrollLeft() - distance}, "slow");
             } else {
                element.animate({scrollLeft: element.scrollLeft() + distance}, "slow");
             }
          }

          /**
           *
           */
          function showPost(post, event, idx) {
             $mdDialog.show({
                controller: DialogController,
                templateUrl: mvm.width > 600 ? 'pages/modal/instagramModal.html' : 'pages/modal/instagram-modal-sm.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose: true,
                onComplete: afterShowAnimation,
                onRemoving: beforeCloseAnimation,
                fullscreen: true, // Only for -xs, -sm breakpoints.
                locals: {
                   post: post, posts: vm.posts, index: idx
                }
             })
                 .then(function (answer) {
                    // console.log('You said the information was "' + answer + '".');
                 }, function () {
                    // console.log('You cancelled the dialog.');
                 });

             function afterShowAnimation(scope, element, options) {
                element.find('.dialog-controls').show();
             }

             function beforeCloseAnimation(element, removePromise) {
                element.find('.dialog-controls').hide();
             }

             function DialogController($scope, $mdDialog, post, posts, index) {
                $scope.post = post;
                $scope.modalProceed = true;
                var currentIdx = index;

                $scope.hide = function () {
                   $mdDialog.hide();
                };

                $scope.next = function () {
                   if (currentIdx === posts.length - 1) {
                      currentIdx = 0;
                   } else {
                      currentIdx++;
                   }
                   $scope.post = posts[currentIdx];
                   setPhoto();
                };

                $scope.priv = function () {
                   if (currentIdx === 0) {
                      currentIdx = posts.length - 1;
                   } else {
                      currentIdx--;
                   }
                   $scope.post = posts[currentIdx];
                   setPhoto();
                };

                function setPhoto() {
                   var url = $scope.post.post.contentUrls[0];
                   $scope.photoUrl = 'media/image/view/' + url.substr(url.lastIndexOf("/") + 1);
                }

                setPhoto();

             }
          }

          /*============= INITIALIZATION ============*/
          loadPosts();
          isSubscribed();
          loadTopSellers();
          loadSales();

          $timeout(function () {
             vm.categories = mvm.nodes[0].nodes;
          }, 150);

          $timeout(function () {
             // инициализация карусели производителя
             brandCarousel = $('#brand .carousel.carousel-slider');
             brandCarousel.carousel({fullWidth: true});

             // проставим цвета плиткам категорий
             angular.forEach($('.categoryBlock'), function (elem) {
                elem.style.backgroundColor = getRandomColor();
             })
          }, 1000);
       }]);
})();
