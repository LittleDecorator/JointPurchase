(function () {
    angular.module('catalog', []);
})();

(function () {
    'use strict';

    angular.module('catalog')

        /* Основной контроллер работы со списком товара. */
        .controller('catalogController', ['$scope', '$state', 'dataResources', '$timeout', 'eventService', '$stateParams', '$rootScope', 'node','modal','$mdToast','store',
            function ($scope, $state, dataResources, $timeout, eventService, $stateParams, $rootScope, node, modal, $mdToast, store) {

                var mvm = $scope.$parent.mvm;
                var vm = this;

                var busy = false;
                var portion = 0;
                var limit = mvm.width < 601 ? 15 : 30;

                vm.loadData = loadData;
                vm.itemView = itemView;
                vm.catalogView = catalogView;
                vm.filterBySubcategory = filterBySubcategory;
                vm.filterByCompany = filterByCompany;
                vm.getSubcategoryUrl = getSubcategoryUrl;
                vm.showWishlistModal = showWishlistModal;
                vm.requestToList = requestToList;
                vm.preorderToList = preorderToList;
                vm.getAddToWishListButtonLabel = getAddToWishListButtonLabel;
                vm.init = init;

                // используется только под администратором
                vm.searchFilter = {category: null, subcategory: null, company: null, criteria: null, offset: 0, limit: limit};
                vm.items = [];
                vm.categories = [];
                vm.companies = [];
                vm.showSideFilter = false;
                vm.showLoadMore = vm.stopLoad = mvm.width < 601;
                vm.allDataLoaded = false;
                vm.infiniteDistance = 2;
                vm.currentCategory = null;
                vm.currentCompany = null;
                vm.selectedCategory = null;
                vm.selectedCompany = null;
                vm.currentNodeType = $stateParams.type;
                vm.currentNode = node;

                function init() {
                    // выбранный узел бокового меню
                    if (node) {
                        console.log(node)
                        // promise получение подкатегорий
                        var categoryPromise = null;
                        var companyPromise = null;
                        // сохраненый фильтр
                        var stashedFilter = localStorage.getItem($state.current.name);
                        if (stashedFilter && stashedFilter !== "undefined") {
                            vm.searchFilter = angular.fromJson(stashedFilter);
                            vm.searchFilter.offset = 0;
                        }

                        if ($stateParams.type === 'category') {
                            // если выбранный узел относится к Категориям
                            if(vm.searchFilter.category !== node.id){
                                vm.searchFilter.category = node.id;
                                vm.searchFilter.subcategory = null;
                                vm.currentCategory = node.id;
                            } else {
                                if(vm.searchFilter.subcategory!==null){
                                    vm.currentCategory = vm.searchFilter.subcategory;
                                } else {
                                    vm.currentCategory = vm.searchFilter.category;
                                }
                                vm.currentCompany = vm.searchFilter.company;
                            }
                            // получим все категории
                            categoryPromise = dataResources.categoryChildrenMap.get({id: node.id});
                            // получить всех производителей для подкатегорий
                            companyPromise = dataResources.categoryChildrenCompanyMap.get({id: node.id});
                        } else {
                            console.log("Производитель");
                            // если выбранный узел относится к Производителям
                            vm.searchFilter.company = node.id;
                            // получим все категории для данного производителя
                            categoryPromise = dataResources.companyCategories.get({id: node.id});
                            // для компаний будет заполнена только подкатегория
                            vm.searchFilter.category = vm.searchFilter.subcategory;
                            vm.currentCategory = vm.searchFilter.category;
                        }

                        vm.searchFilter.clientEmail=mvm.wishListEmail;
                        localStorage.setItem($state.current.name, angular.toJson(vm.searchFilter));
                        vm.confirmedFilter = angular.copy(vm.searchFilter);
                        vm.confirmedFilter.category = vm.currentCategory;

                        // если был выбран узел категории, и promise не пустой, то ...
                        if (categoryPromise !== null) {
                            categoryPromise.$promise.then(function (data) {
                                data.forEach(function (category) {
                                    category.isDefault = (category.id === node.id);
                                    vm.categories.push(category);
                                    if (vm.currentCategory === category.id || vm.searchFilter.subcategory === category.id) {
                                        vm.selectedCategory = category;
                                    }
                                });
                                if(!vm.selectedCategory){
                                    vm.selectedCategory = vm.categories[0];
                                }
                            });
                        }
                        if(companyPromise !== null){
                            companyPromise.$promise.then(function (data) {
                                vm.selectedCompany = null;
                                data.forEach(function (company) {
                                    vm.companies.push(company);
                                    if(vm.currentCompany === company.id){
                                        vm.selectedCompany = company;
                                    }
                                });
                                if(!vm.selectedCompany){
                                  vm.selectedCompany = vm.companies[0];
                                }
                            });
                        }
                    } else {
                        vm.searchFilter.clientEmail=mvm.wishListEmail;
                        vm.confirmedFilter = angular.copy(vm.searchFilter);
                        localStorage.setItem($state.current.name, angular.toJson(vm.confirmedFilter));
                    }

                    // инициируем первый запрос если просмотр с мобильника
                    if (vm.showLoadMore) {
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
                        }, 100)
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
                    if ((!vm.stopLoad || mvm.width < 601) && !busy) {
                        busy = true;
                        dataResources.catalog.list.all(vm.confirmedFilter).$promise.then(function (data) {
                            // если размер полученных данных меньше запрошенных, то запрещаем дальнейшую подгрузку
                            if (data.length < vm.confirmedFilter.limit) {
                                vm.stopLoad = true;
                                vm.showLoadMore = false;
                            }

                            // очистим данные если требуется
                            if (isClean) {
                                vm.items = [];
                            }
                            if ($stateParams.type === 'category') {
                                var commonGroups = [];
                                var tmpItems = [];
                                var commonGroup = null;
                                if (data.length === 1) {
                                    addItem(data[0], helpers.findInArrayById(vm.categories, vm.confirmedFilter.category));
                                } else {
                                    data.forEach(function (e, i) {
                                        if (typeof(isGrouping) !== "undefined" && !isGrouping) {
                                            var category = helpers.findInArrayById(vm.categories, vm.confirmedFilter.category);
                                            addItem(e, category);
                                        } else {
                                            // если общая группа уже определена, то...
                                            if (commonGroup) {
                                                // проверим совпадают ли группы нового товара с общими
                                                var matched = e.categories.filter(function (obj) {
                                                    return obj.id === commonGroup.id;
                                                });
                                                // если совпадают, то просто добавим товар
                                                if (matched.length > 0) {
                                                    addItem(e, commonGroup);
                                                } else {
                                                    // если не совпал, то...
                                                    // иначе новый товар
                                                    tmpItems = [];
                                                    tmpItems.push(e);
                                                    commonGroups = e.categories;
                                                    commonGroup = null;
                                                }
                                            } else {
                                                // если общая группа не определена
                                                // добавим товар во временную коллекцию
                                                tmpItems.push(e);
                                                // если товар уже выбирался раньше, то...
                                                if (commonGroups.length > 0) {
                                                    // если определилась одна общая группа, то...
                                                    if (commonGroups.length === 1 && i > 0) {
                                                        commonGroup = commonGroups[0];
                                                        // из временной коллекции добавим товары
                                                        tmpItems.forEach(function (tmp) {
                                                            addItem(tmp, commonGroup)
                                                        });
                                                        // очистим временное
                                                        tmpItems = [];
                                                        commonGroups = [];
                                                    } else {
                                                        // если общей группы пока нет, то пытаемся его найти через новые группы
                                                        commonGroups = commonGroups.filter(function (obj) {
                                                            return e.categories.some(function (o2) {
                                                                return obj.id === o2.id;
                                                            });
                                                        });
                                                        // если нет общих групп, значит новая
                                                        // если группа единична, то добавим
                                                        if (commonGroups.length === 0) {
                                                            if (e.categories.length === 1) {
                                                                // проверим может у нас единичный товар
                                                                addItem(tmpItems[0], e.categories[0])
                                                            } else {
                                                                // удалим из списка
                                                                tmpItems = [];
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    // инициализация при первом товаре
                                                    commonGroups = e.categories;
                                                }
                                            }
                                        }
                                    });

                                    // пройдем по выбранным группам, если первый товар в группе не в наличие, то передвинем группу ниже в списке
                                    // выполняется только на первой загрузке
                                    if (portion === 0) {
                                        var itemsCopy = angular.copy(vm.items);
                                        for (var i = 0; i < itemsCopy.length - 1; i++) {
                                            if (itemsCopy[i].values[0].status.id === "preorder") {
                                                // данный вариант вернет первый подходящий
                                                // var index = itemsCopy.findIndex(x => x.values[0].status.id != "preorder");
                                                // данный вариант вернет все подходящие
                                                var indexes = itemsCopy.map(function (obj, index) {
                                                    if (obj.values[0].status.id !== "preorder") {
                                                        return index;
                                                    }
                                                }).filter(function (idx) {
                                                    return idx > i;
                                                });
                                                if (indexes[0] !== -1) {
                                                    vm.items.move(i, indexes[0]);
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }
                            } else {
                                vm.items = vm.items.concat(data);
                            }

                            portion++;
                            vm.confirmedFilter.offset = portion * vm.confirmedFilter.limit;
                            //говорим что можно отображать
                            vm.allDataLoaded = true;
                            busy = false;
                        });
                    }
                }

                /**
                 * Новая загрузка после смены подкатегории
                 * @param group
                 */
                function filterBySubcategory(group) {
                    if(group === undefined){
                        group = vm.selectedCategory;
                    }
                    console.log(group)
                    vm.currentCategory = group.id;
                    vm.searchFilter.subcategory = group.id;
                    vm.searchFilter.offset = 0;
                    vm.confirmedFilter = angular.copy(vm.searchFilter);
                    vm.confirmedFilter.category = group.id;
                    localStorage.setItem($state.current.name, angular.toJson(vm.searchFilter));
                    vm.stopLoad = false;
                    loadData(true, group.isDefault);
                }

              /**
               * Новая загрузка после смены производителя
               * @param companyGroup
               */
              function filterByCompany(companyGroup) {
                  console.log(vm.searchFilter);
                if(companyGroup === undefined){
                  companyGroup = vm.selectedCompany;
                }
                vm.currentCompany = companyGroup.id;
                vm.searchFilter.company = companyGroup.id;
                vm.searchFilter.offset = 0;
                vm.confirmedFilter = angular.copy(vm.searchFilter);
                vm.confirmedFilter.category = vm.selectedCategory.id;
                vm.confirmedFilter.company = companyGroup.id;
                localStorage.setItem($state.current.name, angular.toJson(vm.searchFilter));
                vm.stopLoad = false;
                console.log(vm.selectedCategory);
                console.log(vm.searchFilter);
                loadData(true, vm.selectedCategory.isDefault);
              }

                /**
                 * Переход на карточку товара
                 * @param id
                 */
                function itemView(name) {
                    $state.go("catalog.detail", {itemName: name});
                }

              function catalogView(name) {
                $state.go("catalog.type.detail", {itemName: name});
              }

                /**
                 * Получение label'а кнопок.
                 * @param item
                 */
                function getAddToWishListButtonLabel(item){
                    var label = "";
                    if (item.inWishlist){
                        label = item.status.id === 'preorder' ? 'Заказан' : 'Отложен';
                    } else {
                        label = item.status.id === 'preorder' ? 'Заказать' : 'Отложить';
                    }
                    return label;
                }

                /**
                 * Показ модального для "отложить"
                 * @param item
                 * @param wClass
                 */
                function preorderToList(item) {
                    if(item.inWishlist){
                        goto('wishlist', {id: item.id});
                    }
                    var wClass = mvm.width < 601 ? 'w-80' : 'w-30';
                    vm.showWishlistModal(item, mvm.width < 601 ? "pages/fragment/modal/preorderModal.html" : "pages/modal/preorderModal.html", 'ngdialog-theme-default ' + wClass)
                }

                /**
                 * Показ модального для "заказать"
                 * @param item
                 * @param wClass
                 */
                function requestToList(item) {
                    if(item.inWishlist){
                        goto('wishlist', {id: item.id});
                    }
                    var wClass = mvm.width < 601 ? 'w-80' : 'w-30';
                    vm.showWishlistModal(item, mvm.width < 601 ? "pages/fragment/modal/requestItemModal.html" : "pages/modal/requestItemModal.html", 'ngdialog-theme-default ' + wClass)
                }

                /**
                 * Добавление товара в список желаемого
                 * @param item
                 * @param templateUrl
                 * @param className
                 */
                function showWishlistModal(item, templateUrl, className) {
                    var toast = $mdToast.simple().position('top right').hideDelay(3000);
                    if(mvm.wishListEmail == null){
                        //определим модальное окно
                        var wishListDialog = modal({
                            templateUrl: templateUrl,
                            className: className,
                            closeByEscape: true,
                            controller: "wishlistController as vm",
                            data: item
                        });

                        // Слушатель закрытия модального
                        wishListDialog.closePromise.then(function(output) {

                            // запомним stash client email
                            if(mvm.wishListEmail == null){
                                mvm.wishListEmail = output.value.email;
                                store.set("wishListEmail", mvm.wishListEmail);
                            }

                            item.inWishlist = true;
                            vm.getAddToWishListButtonLabel(item);
                            $mdToast.show(toast.textContent('Заявка на заказ товара ['+ item.name + '] принята').theme('info'));
                        });
                    } else {
                        var stashed = {id: null, itemId: item.id, subjectId: null, email: mvm.wishListEmail};
                        dataResources.wishlist.core.post(stashed).$promise.then(function(data){
                            $mdToast.show(toast.textContent('Товар ['+ item.name + '] отложен').theme('info'));
                            item.inWishlist = true;
                            vm.getAddToWishListButtonLabel(item);
                        }, function(error){
                            $mdToast.show(toast.textContent('Неудалось добавить товар в список желаемого').theme('error'));
                        });
                    }
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
                        if (vm.confirmedFilter.category === node.name) {
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

                init();
            }])

        /* Контроллер работы с карточкой товара */
        .controller('catalogCardController', ['$scope', '$state', 'product', 'eventService',
            function ($scope, $state, product, eventService) {

                var mvm = $scope.$parent.mvm;
                var vm = this;
                var gallery = null;
                var sliderNum = 0;

                vm.show = show;
                vm.showGallery = showGallery;
                vm.getTemplateUrl = getTemplateUrl;
                vm.afterInclude = afterInclude;
                vm.openSlider = openSlider;

                vm.item = angular.extend({}, product);
                vm.mainImage = vm.item.url;
                
                /**
                 * Установка изображения активным на просмотр
                 * @param id - изображение которое нужно показать как main
                 * @param event
                 */
                function show(id) {
                    // поиск выбранного в списке
                    sliderNum = vm.item.images.map(function (obj, index) {
                        if (obj.contentId === id) {
                            return index;
                        } else {
                            return null
                        }
                    }).filter(function (idx) {
                        return idx != null;
                    })[0];
                    vm.mainImage = (mvm.width < 601 ? mvm.PREVIEW_URL : mvm.VIEW_URL) + id;
                }

                /**
                 * Открываем слайдер
                 */
                function openSlider() {
                    var el = vm.item.images.map(function (elem) {
                        return {
                            src: (mvm.width < 601 ? mvm.PREVIEW_URL : mvm.VIEW_URL) + elem.contentId,
                            thumb: mvm.THUMB_URL + elem.contentId
                        }
                    });
                    // настройки для swipe
                    var options = {
                        selector: '.item',
                        dynamic: true,
                        getCaptionFromTitleOrAlt: false,
                        dynamicEl: el
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
                    console.log('onfilter',eventService.data)
                    var node = eventService.data;
                    var type;
                    // определяет тип выбранного узла
                    if (node.company) {
                        type = 'company';
                    } else {
                        type = 'category';
                    }
                    // переходим на страницу результата фильтрации
                    $state.go('catalog.type', {id: node.id, type: type}, {notify: true});
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
                }

            }]);
})();
