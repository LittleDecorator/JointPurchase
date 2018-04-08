(function () {
   angular.module('catalog', []);
})();

(function () {
   'use strict';

   angular.module('catalog')

   /* Основной контроллер работы со списком товара. */
       .controller('catalogController', ['$scope', '$state', 'dataResources', '$timeout', 'eventService', '$stateParams', '$rootScope', 'node', 'modal', '$mdToast', 'store', function ($scope, $state, dataResources, $timeout, eventService, $stateParams, $rootScope, node, modal, $mdToast, store) {

          var mvm = $scope.$parent.mvm;
          var vm = this;

          var busy = false;   // маркертого, что происходит подгрузка данных
          var portion = 0;    // номер порции данных
          var limit = mvm.width < 601 ? 15 : 30;      // устанавливаем кол-во получаемых данных. Зависит от устройства.

          vm.loadData = loadData;
          vm.itemView = itemView;
          vm.catalogView = catalogView;
          vm.filterBySubcategory = filterBySubcategory;
          vm.filterByCategory = filterByCategory;
          vm.filterByCompany = filterByCompany;
          vm.getSubcategoryUrl = getSubcategoryUrl;
          vm.lockCategory = lockCategory;

          vm.init = init;

          // используется только под администратором
          vm.searchFilter = {category: null, subcategory: null, sale: null, company: null, offset: 0, limit: limit};
          vm.items = [];
          vm.categories = [];
          vm.companies = [];
          vm.showSideFilter = false;
          vm.showLoadMore = vm.stopLoad = mvm.width < 601;
          vm.detailLock = false;        // флаг ограничения автоматической подгрузки данных, при нахождении в карточке
          vm.allDataLoaded = false;   // флаг того, что загружать больше нечего
          vm.infiniteDistance = 2;
          vm.selectedCategory = null;     // выбранная категория
          vm.selectedCompany = null;      // выбранный производитель
          vm.currentNodeType = $stateParams.type;
          vm.currentNode = node;

          // listen lock event
         $scope.$on("detailLock", function() {
           vm.detailLock = true;
           console.log('detailLock happen!')
         });

          /**
           * Инициализация страницы
           */
          function init() {
             // выбранный узел бокового меню
             if (node) {
                console.log(node)
                // promise получение подкатегорий
                var categoryPromise = null;
                var companyPromise = null;

                // сохраненый фильтр (достанем фильтр из памяти)
                var stashedFilter = localStorage.getItem($state.current.name);
                if (stashedFilter && stashedFilter !== "undefined") {
                   vm.searchFilter = angular.fromJson(stashedFilter);
                   vm.searchFilter.offset = 0;
                }

                // если пришли через категорию
                if ($stateParams.type === 'category') {
                   // если выбранный узел относится к Категориям
                   if (vm.searchFilter.category !== node.id) {
                      // если категория отличается от предыдущей
                      vm.searchFilter.category = node.id;
                      vm.searchFilter.subcategory = null;
                   }
                   // получим все категории
                   categoryPromise = dataResources.categoryChildrenMap.get({id: node.id});
                   // получить всех производителей для подкатегорий
                   companyPromise = dataResources.categoryChildrenCompanyMap.get({id: node.id});
                }
                if ($stateParams.type === 'company') {
                   // если выбранный узел относится к Производителям
                   vm.searchFilter.company = node.id;
                   // получим все категории для данного производителя
                   categoryPromise = dataResources.companyCategories.get({id: node.id});
                }
                if($stateParams.type === 'sale'){
                   console.log(vm.items);
                   vm.searchFilter.sale = node.id;
                   vm.items = node.items;
                   vm.currentNodeType = 'sale';
                   vm.currentNode = {contentId:node.bannerId, description: node.description, title: node.title, startDate: node.startDate, endDate: node.endDate};
                   vm.items.forEach(function (elem, idx) {
                      elem.url = 'media/image/preview/'+ elem.image;
                   });
                   vm.stopLoad = true;
                   vm.allDataLoaded = true;
                }

                vm.searchFilter.clientEmail = mvm.wishListEmail;
                localStorage.setItem($state.current.name, angular.toJson(vm.searchFilter));
                vm.confirmedFilter = angular.copy(vm.searchFilter);

                // обработка promise Категории
                if (categoryPromise !== null) {
                   categoryPromise.$promise.then(function (data) {
                      data.forEach(function (category) {
                         // если выбранная есть в доступных, то отметим её как по-умолчанию
                         category.isDefault = (category.id === node.id);
                         vm.categories.push(category);
                         if (vm.searchFilter.category === category.id || vm.searchFilter.subcategory === category.id) {
                            // мы выбрали категорию для UI
                            vm.selectedCategory = category;
                         }
                      });
                      // наверно можно будет убрать, ведь обобщающая возвращается вместе с остальными
                      if (!vm.selectedCategory) {
                         vm.selectedCategory = vm.categories[0];
                      }
                   });
                }

                // обработка promise Производителей
                if (companyPromise !== null) {
                   companyPromise.$promise.then(function (data) {
                      vm.selectedCompany = null;
                      data.forEach(function (company) {
                         vm.companies.push(company);
                         if (vm.searchFilter.company === company.id) {
                            vm.selectedCompany = company;
                         }
                      });

                      if (!vm.selectedCompany) {
                         vm.selectedCompany = vm.companies[0];
                      }
                   });
                }
             } else {
                vm.searchFilter.clientEmail = mvm.wishListEmail;
                vm.confirmedFilter = angular.copy(vm.searchFilter);
                localStorage.setItem($state.current.name, angular.toJson(vm.confirmedFilter));
             }

             // инициируем первый запрос если просмотр с мобильника
             if (vm.showLoadMore && $stateParams.type !== 'sale') {
                loadData();
             }

             if ($stateParams.type === 'category') {
                $timeout(function () {
                   var target = $('button[disabled]');
                   var rect = target[0].getBoundingClientRect();
                   var docWidth = $(window).width();
                   if (rect.right > docWidth) {
                      $('.md-virtual-repeat-scroller').animate({scrollLeft: $(target).offset().left}, 900);
                   }
                }, 200)
             }
          }

          /**
           * получение данных с сервера
           * @param isClean - нужно ли очищать существующий набор данных
           * @param isGrouping - нужна ли группировка
           */
          function loadData(isClean, isGrouping) {

             function addItem(item, category) {
                var categoryId = category.id;
                var newElem = {id: categoryId, name: category.name, values: []};
                if (vm.items.length > 0) {
                   var result = vm.items.filter(function (e) {
                      return e.id === categoryId
                   });
                   if (result.length > 0) {
                      result[0].values.push(item);
                   } else {
                      newElem.values.push(item);
                      vm.items.push(newElem)
                   }
                } else {
                   newElem.values.push(item);
                   vm.items.push(newElem)
                }
             }

             // если загрузка разрешена и не заняты
             if (!vm.detailLock && (!vm.stopLoad || mvm.width < 601) && !busy) {
                mvm.showLoader = true;
                busy = true;

                //FIXME: every load change from $promise
                dataResources.catalog.list.all(vm.confirmedFilter, function(data,h){
                  var headers = h();
                  console.log(headers)
                  // очистим данные если требуется
                  if (isClean) {
                    vm.items = [];
                  }
                  if ($stateParams.type === 'category') {
                    if (data.length === 1) {
                      addItem(data[0], helpers.findInArrayById(vm.categories, vm.confirmedFilter.category));
                    } else {
                      data.forEach(function (e, i) {
                        var category = helpers.findInArrayById(vm.categories, vm.confirmedFilter.category);
                        addItem(e, category);
                      });
                    }
                  } else {
                    vm.items = vm.items.concat(data);
                  }

                  // если размер полученных данных меньше запрошенных, то запрещаем дальнейшую подгрузку
                  if (headers['x-page-number'] === headers['x-total-pages'] || headers['x-total-pages'] <= 1) {
                    vm.stopLoad = true;
                    vm.showLoadMore = false;
                  } else {
                    portion++;
                    vm.confirmedFilter.offset = portion * vm.confirmedFilter.limit;
                  }

                  //говорим что можно отображать
                  vm.allDataLoaded = true;
                  busy = false;
                  mvm.showLoader = false;
                });
             }
          }

          /**
           * Новая загрузка после смены подкатегории
           * @param group
           */
          function filterBySubcategory(group) {
            if (group === undefined) {
              group = vm.selectedCategory;
            }
            if (group.id === vm.searchFilter.category) {
              vm.searchFilter.subcategory = null;
            } else {
              vm.searchFilter.subcategory = group.id;
            }
            vm.currentCategory = group.id;
            vm.searchFilter.offset = 0;
            vm.confirmedFilter = angular.copy(vm.searchFilter);
            localStorage.setItem($state.current.name, angular.toJson(vm.searchFilter));
            vm.stopLoad = false;
            loadData(true, group.isDefault);
          }

          /**
           * Новая загрузка после смены подкатегории
           * @param group
           */
          function filterByCategory(group) {
             if (group === undefined) {
                group = vm.selectedCategory;
             }
             vm.searchFilter.category = group.id;
             vm.searchFilter.subcategory = null;
             vm.searchFilter.offset = 0;
             vm.confirmedFilter = angular.copy(vm.searchFilter);
             localStorage.setItem($state.current.name, angular.toJson(vm.searchFilter));
             vm.stopLoad = false;
             loadData(true, group.isDefault);
          }

          /**
           * Новая загрузка после смены производителя
           * @param companyGroup
           */
          function filterByCompany(companyGroup) {
             if (companyGroup === undefined) {
                companyGroup = vm.selectedCompany;
             }
             console.log(vm.searchFilter)
             if(vm.searchFilter.subcategory === vm.searchFilter.category){
                console.log('clear subcategory')
                vm.searchFilter.subcategory = null;
             }
             vm.currentCompany = companyGroup.id;
             vm.searchFilter.company = companyGroup.id;
             vm.searchFilter.offset = 0;
             vm.confirmedFilter = angular.copy(vm.searchFilter);
             // vm.confirmedFilter.category = vm.selectedCategory.id;
             vm.confirmedFilter.company = companyGroup.id;
             localStorage.setItem($state.current.name, angular.toJson(vm.searchFilter));
             vm.stopLoad = false;
             loadData(true, vm.selectedCategory.isDefault);
          }

          /**
           * Переход на карточку товара
           * @param id
           * @param name
           */
          function itemView(name) {
             vm.detailLock = true;
             $state.go("catalog.detail", {itemName: name});
          }

          function catalogView(name) {
             vm.detailLock = true;
             $state.go("catalog.type.detail", {itemName: name});
          }

          /* Получения шаблона страницы */
          function getSubcategoryUrl() {
             var templatePath = "pages/fragment/catalog/list/";
             if (mvm.width < 601) {
                return templatePath + "subcategory-sm.html";
             } else {
                return templatePath + "subcategory-lg.html";
             }
          }

          // callback загрузки шаблона страницы
          function afterInclude() {
          }

          function lockCategory(id){
             if(vm.searchFilter.subcategory){
                return id === vm.searchFilter.subcategory;
             } else {
                return id === vm.searchFilter.category;
             }
          }

          /**
           * Слушатель события "onFilter"
           */
          $scope.$on('onFilter', function () {
             var node = eventService.data;
             var type;
             // определяет тип выбранного узла
             if (node.company) {
                vm.searchFilter.company = node.name;
                vm.searchFilter.category = null;
                type = 'company';
             } else {
                // если выбранная совпадает с текущей, то ничего не делаем
                if (vm.confirmedFilter.category === node.id) {
                   return;
                }
                vm.searchFilter.company = null;
                vm.searchFilter.category = node.name;
                vm.showSideFilter = true;
                type = 'category';
             }
             // переходим на страницу результата фильтрации
             $state.go('catalog.type', {name: node.name, type: type}, {notify: true}).then(function () {
                // неявно обновим Breadcrumbs
                $rootScope.$broadcast('$refreshBreadcrumbs', $state);
             });
             // сбросим фильтр получения данных с сервера
             portion = 0;
             vm.searchFilter.offset = 0;
             vm.stopLoad = false;
             // запросим данные
             loadData(true);
          });

          /**
           * Слушатель нажатия кнопки НАЗАД
           */
          $scope.$on('locBack', function () {
             mvm.showDetail = false;
             vm.detailLock = false;
          });

          // выключаем view карточки
          mvm.showDetail = false;

          init();
       }])

       /* Контроллер работы с карточкой товара */
       .controller('catalogCardController', ['$scope', '$state', 'product', 'eventService', function ($scope, $state, product, eventService) {

          var mvm = $scope.$parent.mvm;
          mvm.showDetail = true;
          var vm = this;
          var gallery = null;
          var sliderNum = 0;

          vm.show = show;
          vm.showGallery = showGallery;
          vm.getTemplateUrl = getTemplateUrl;
          vm.afterInclude = afterInclude;
          vm.openSlider = openSlider;
          vm.init = init;

          vm.item = angular.extend({}, product);
          vm.mainImage = vm.item.url;
          vm.item.images = [];

          function init() {

            vm.item.images = $.map(vm.item.contentIds, function(value, index) {
              var obj = {contentId:index, meta:{orientation:''}};
              if(value){
                obj.meta= angular.fromJson(value);
              }
              return obj;
            });

            var mainId = vm.item.url.substring(vm.item.url.lastIndexOf('/')+1);
            var selected = vm.item.images.find(function(obj, idx){
              return obj.contentId === mainId;
            });
            vm.orientationClass = selected.meta.orientation;
          }

          /**
           * Установка изображения активным на просмотр
           * @param id - изображение которое нужно показать как main
           * @param event
           */
          function show(id) {
             // поиск выбранного в списке
            var selected = vm.item.images.find(function(obj, idx){
              sliderNum = idx;
              return obj.contentId === id;
            });

             vm.mainImage = (mvm.width < 601 ? mvm.PREVIEW_URL : mvm.VIEW_URL) + id;
             vm.orientationClass = selected.meta.orientation;
          }

          /**
           * Открываем слайдер
           */
          function openSlider() {
             var el = vm.item.images.map(function (elem) {
                return {
                   src: (mvm.width < 601 ? mvm.PREVIEW_URL : mvm.VIEW_URL) + elem.contentId, thumb: mvm.THUMB_URL + elem.contentId
                }
             });
             // настройки для swipe
             var options = {
                selector: '.item', dynamic: true, getCaptionFromTitleOrAlt: false, dynamicEl: el
             };
             // изменим отображение для маленького экрана
             if (mvm.width < 601) {
                options.controls = false;
                options.thumbnail = false;
             }
             gallery = $(this).lightGallery(options);
             gallery.one('onAfterOpen.lg', function () {
                gallery.data('lightGallery').slide(sliderNum);
             });

          }

          $scope.$on('onFilter', function () {
             var node = eventService.data;
             var type;
             // определяет тип выбранного узла
             if (node.company) {
                type = 'company';
             } else {
                type = 'category';
             }
             // переходим на страницу результата фильтрации
             $state.go('catalog.type', {name: node.name, type: type}, {notify: true});
          });

          /**
           * Переход в галерею товара
           */
          function showGallery() {
             $state.go("product.detail.gallery", {id: vm.item.id});
          }

          /* Получения шаблона страницы */
          function getTemplateUrl() {
             var templatePath = "pages/fragment/catalog/card/";
             if (mvm.width < 601) {
                return templatePath + "catalog-card-sm.html";
             } else {
                return templatePath + "catalog-card-lg.html";
             }
          }

          // callback загрузки шаблона страницы
          function afterInclude() {
            console.log(vm,'vm');
            console.log('scope',$scope);
          }

          // function getMeta(url){
          //     var img = new Image();
          //     img.onload = function(){
          //       vm.orientationClass = this.naturalHeight > 445 ? 'vertical' : 'horizontal';
          //     };
          //     img.src = url;
          //
          // }

          init();
       }]);
})();
