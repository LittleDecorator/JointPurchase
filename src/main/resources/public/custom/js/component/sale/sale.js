(function () {
   angular.module('sale', []);
})();

(function () {
   'use strict';

   angular.module('sale')
       .controller('saleController', ['$scope', '$rootScope', '$state', '$mdToast', 'dataResources', function ($scope, $rootScope, $state, $mdToast, dataResources) {

          var busy = false;
          var portion = 0;
          var mvm = $scope.$parent.mvm;
          var vm = this;
          var toast = $mdToast.simple().position('top right').hideDelay(3000);

          vm.loadData = loadData;
          vm.clear = clear;
          vm.apply = apply;
          vm.getTemplateUrl = getTemplateUrl;
          vm.editSale = editSale;
          vm.activateSale = activateSale;
          vm.addSale = addSale;

          vm.sales = [];
          vm.filter = {limit: 30, offset: 0};
          vm.showHints = true;
          vm.forms = {saleForm: {}};
          var confirmedFilter = angular.copy(vm.filter);

          vm.scrolling = {stopLoad: false, allDataLoaded: false, infiniteDistance: 2};

          /* Получение данных */
          function loadData(isClean) {
             if (!vm.scrolling.stopLoad && !busy) {
                busy = true;

                dataResources.sale.all(confirmedFilter).$promise.then(function (data) {

                   if (data.length < confirmedFilter.limit) {
                      vm.scrolling.stopLoad = true;
                   }

                   if (isClean) {
                      vm.sales = [];
                   }

                   vm.sales = vm.sales.concat(data);

                   portion++;
                   confirmedFilter.offset = portion * confirmedFilter.limit;
                   vm.scrolling.allDataLoaded = true;
                   busy = false;

                });
             }
          }

          /**
           *
           * @param sale
           */
          function activateSale(sale) {
             if(sale.active){
                if(sale.endDate > new Date().getTime()){
                   console.log(sale)
                   dataResources.sale.activate({id: sale.id, activate: true},{}, function(data){
                      $mdToast.show(toast.textContent('Акция "'+ sale.title +'" успешно активирована!').theme('success'));
                   })
                } else {
                   $mdToast.show(toast.textContent('Нельзя активировать прошедшую акцию!').theme('error'));
                   sale.active = false;
                }
             } else {
                dataResources.sale.activate({id: sale.id, activate: false},{}, function(data){
                   $mdToast.show(toast.textContent('Акция "'+sale.title+'" успешно выключена!').theme('success'));
                })
             }

          }

          // очистка фильтра
          function clear() {
             portion = 0;
             vm.filter = {limit: 30, offset: 0};
             confirmedFilter = angular.copy(vm.filter);
             // удалим старый фильтр
             localStorage.removeItem($state.current.name);
             vm.scrolling.stopLoad = false;
             loadData(true);
          }

          // подтверждение фильтра
          function apply() {
             portion = 0;
             vm.filter.offset = portion * vm.filter.limit;
             confirmedFilter = angular.copy(vm.filter);
             // запомним фильтр
             localStorage.setItem($state.current.name, angular.toJson(confirmedFilter));
             vm.scrolling.stopLoad = false;
             loadData(true);
          }

          function editSale(id) {
             $state.go("sale.detail", {id: id});
          }

          // Добавление клиента
          function addSale() {
             $state.go("sale.detail");
          }

          // получение шаблона страницы
          function getTemplateUrl() {
             var result = "pages/fragment/sale/";
             if (mvm.width < 601) {
                return result + "sale-sm.html"
             }
             if (mvm.width > 600) {
                if (mvm.width < 961) {
                   return result + "sale-md.html"
                }
                return result + "sale-lg.html"
             }
             return result;
          }
       }])

       .controller('saleDetailController', ['$scope', '$rootScope', '$state', '$stateParams', '$mdToast', '$filter', 'dataResources', 'sale', 'FileUploader', 'itemClssModal','modal', function ($scope, $rootScope, $state, $stateParams, $mdToast, $filter, dataResources, sale, FileUploader, itemClssModal,modal) {
          var mvm = $scope.$parent.mvm;
          var vm = this;
          var uploader = $scope.uploader = new FileUploader();
          var toast = $mdToast.simple().position('top right').hideDelay(3000);

          vm.init = init;
          vm.activateSale = activateSale;
          vm.removeItem = removeItem;
          vm.addItem = addItem;
          vm.getTemplateUrl = getTemplateUrl;
          vm.addBanner = addBanner;
          vm.deleteBanner = deleteBanner;
          vm.deleteSale = deleteSale;
          vm.save = save;

          vm.forms = {saleCard: {}};
          vm.sale = sale === null ? {startDate: null, endDate: null, items: []} : sale;
          if (vm.sale.startDate) {
             vm.sale.startDate = new Date(vm.sale.startDate);
          }
          if (vm.sale.endDate) {
             vm.sale.endDate = new Date(vm.sale.endDate);
          }

          /**
           * Удаление товара из списка участвующих в акции
           * @param itemId
           */
          function removeItem(idx) {
             vm.sale.items.splice(idx, 1);

             // проверяем валидность формы
             var saleBlocked = false;
             vm.sale.items.some(function(elem){
                if(elem.blocked){
                   return saleBlocked = true;
                } else {
                   return saleBlocked = false;
                }
             });
             vm.forms.saleCard.$valid = !saleBlocked
          }

          /**
           * Добавление товара в список участвующих в акции
           * @param itemId
           */
          function addItem(clazz) {
             var dialog;
             if(clazz){
                dialog = modal({
                   templateUrl: "pages/fragment/modal/itemModal.html",
                   className: 'ngdialog-theme-default fullscreen' ,
                   closeByEscape: true,
                   controller: "itemClssController as vm",
                   data: vm.sale.items
                });
             } else {
                 dialog  = itemClssModal(vm.sale.items, 'wp-50');
             }

             // var dialog
             dialog.closePromise.then(function (output) {
                if (output.value && output.value !== '$escape') {

                   // если что-то было выбрано раньше, то очистим
                   if (vm.sale.items.length !== 0) {
                      vm.sale.items = [];
                   }

                   // добавим вновь выбраные
                   angular.forEach(output.value, function (item) {
                      item.blocked = false;
                      if(item.sale){
                         if(
                             item.sale.startDate > vm.sale.startDate && item.sale.startDate < vm.sale.endDate
                             ||  item.sale.endDate > vm.sale.startDate && item.sale.endDate < vm.sale.endDate
                             || item.sale.startDate < vm.sale.startDate && item.sale.endDate > vm.sale.endDate
                         ) {
                            item.blocked = true;
                            // делаем не валидным
                            vm.forms.saleCard.$valid = false;
                         }
                      }
                      vm.sale.items.push(item);
                   });

                   // пометим форму как грязную
                   vm.forms.saleCard.$setDirty(true);
                }
             });
          }

          /**
           *
           * @param sale
           */
          function activateSale() {
             // если активируем
             if(vm.sale.active){
                if(!vm.forms.saleCard.$dirty && vm.forms.saleCard.$valid){
                   if(vm.sale.endDate > new Date().getTime()){
                        dataResources.sale.activate({id: vm.sale.id, activate: vm.sale.active},{}, function(data){
                           $mdToast.show(toast.textContent('Акция успешно активирована!').theme('success'));
                        })
                   } else {
                      $mdToast.show(toast.textContent('Нельзя активировать прошедшую акцию!').theme('error'));
                      vm.sale.active = false;
                   }
                } else {
                   $mdToast.show(toast.textContent('Необходимо сохранить изменения!').theme('error'));
                   vm.sale.active = false;
                }
             } else {
                dataResources.sale.activate({id: vm.sale.id, activate: vm.sale.active},{}, function(data){
                   $mdToast.show(toast.textContent('Акция успешно выключена!').theme('success'));
                })
             }

          }

          function deleteSale(saleId, idx) {
             console.log('TODO:// add sale delete')
          }

          /**
           * Получение шаблона страницы
           * @returns {*}
           */
          function getTemplateUrl() {
             var result = "pages/fragment/sale/card/";
             if (mvm.width < 601) {
                return result + "sale-card-sm.html"
             }
             if (mvm.width > 600) {
                if (mvm.width < 961) {
                   return result + "sale-card-md.html"
                }
                return result + "sale-card-lg.html"
             }
             return result;
          }

          /**
           * загрузка файлов
           */
          function upload() {
             var items = uploader.getNotUploadedItems();
             var formData = new FormData();

             angular.forEach(items, function (item, idx) {
                formData.append("file" + idx, item._file);
             });

             formData.append("saleId", $stateParams.id);

             // загружаем изображение
             dataResources.saleContent.upload(formData, function (data) {
                vm.sale.bannerId = data.result;
                uploader.clearQueue();
             });
          }

          /**
           * proxy добаление
           */
          function addBanner() {
             $('#uploadBtn').click();
          }

          /**
           * Удаление баннера
           */
          function deleteBanner() {
             dataResources.saleImage.delete({id:vm.sale.id}, function(){
                vm.sale.bannerId = null;
             }, function () {
                $mdToast.show(toast.textContent('Неудалось удалить баннер \n'+ error.message).theme('error'));
             });

          }

          /**
           * init section
           */
          function init() {
             vm.images = dataResources.saleImage.get({id: $stateParams.id});
          }

          /**
           * Сохраним изменения
           */
          function save() {
             var toast = $mdToast.simple().position('top right').hideDelay(3000);
             if (vm.forms.saleCard.$dirty) {
                if (vm.forms.saleCard.$valid) {
                   if (vm.sale && vm.sale.id) {
                      dataResources.sale.put(vm.sale).$promise.then(function (data) {
                         $mdToast.show(toast.textContent('Акция ' + vm.sale.title + ' успешно изменена').theme('success'));
                      }, function (error) {
                         $mdToast.show(toast.textContent('Не удалось изменить акцию ' + vm.sale.title).theme('error'));
                      });
                   } else {
                      dataResources.sale.post(vm.sale).$promise.then(function (data) {
                         $mdToast.show(toast.textContent('Акция ' + vm.sale.title + ' успешно создана').theme('success'));
                         vm.forms.saleCard.$setPristine();
                         $state.go($state.current, {id: data.result}, {notify: false}).then(function () {
                            $rootScope.$broadcast('$refreshBreadcrumbs', $state);
                         });
                      }, function (error) {
                         console.log(error);
                         $mdToast.show(toast.textContent('Не удалось создать акцию ' + vm.sale.title).theme('error'));
                      });
                   }
                   vm.showHints = true;
                } else {
                   vm.showHints = false;
                }
             }
          }

          /**
           * callback on file select
           */
          uploader.onAfterAddingAll = function () {
             upload();
          };

          // FILTERS
          uploader.filters.push({
             name: 'imageFilter', fn: function (item /*{File|FileLikeObject}*/, options) {
                var type = '|' + item.type.slice(item.type.lastIndexOf('/') + 1) + '|';
                return '|webp|jpg|png|jpeg|bmp|gif|'.indexOf(type) !== -1;
             }
          });

          init();
       }])
})();