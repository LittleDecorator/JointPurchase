(function () {
    angular.module('category', []);
})();

(function () {
    'use strict';

    angular.module('category')
        .controller('categoryController', ['$rootScope', '$scope', '$log','$state', 'categoryNodes', '$timeout', 'dataResources','itemClssModal', '$location', 'menu',
            function ($rootScope, $scope, $log, $state, categoryNodes, $timeout, dataResources, itemClssModal, $location, menu) {
                var templatePath = "pages/fragment/category/";
                var mvm = $scope.$parent.mvm;

                var vm = this;

                vm.getCategoryItems = getCategoryItems;
                vm.clearNewNodeField = clearNewNodeField;
                vm.clearSelected = clearSelected;
                vm.treeHandler = treeHandler;
                vm.nodeTitleChange = nodeTitleChange;
                vm.newNodeTitleChange = newNodeTitleChange;
                vm.edit = edit;
                vm.deleteNode = deleteNode;
                vm.add = add;
                vm.addAsSibling = addAsSibling;
                vm.addAsRoot = addAsRoot;
                vm.removeItem = removeItem;
                vm.save = save;
                vm.showClss = showClss;
                vm.createCategory = createCategory;
                vm.isOpen = isOpen;
                vm.toggleOpen = toggleOpen;
                vm.openSection = openSection;
                vm.convertNodeToMenu = convertNodeToMenu;
                vm.getSections = getSections;
                vm.getTemplateUrl = getTemplateUrl;
                vm.init = init;

                vm.categories = categoryNodes;
                vm.tree = {};
                vm.selected = null;
                vm.selectedCopy = null;
                vm.currentNode = {title:""};
                vm.autoFocusContent = false;
                vm.menu = menu;
                vm.status = {
                    isFirstOpen: false,
                    isFirstDisabled: false
                };

                /**
                 * получение товаров выбранного узла
                 * @param categoryId
                 * @returns {Array}
                 */
                function getCategoryItems(categoryId) {
                    var items = [];
                    dataResources.categoryItems.get({id: categoryId}, function (data) {
                        angular.forEach(data, function (rec) {
                            items.push(rec);
                        });
                    });
                    return items;
                }

                /**
                 * очистка поля ввода нового узла
                 */
                function clearNewNodeField() {
                    vm.currentNode = {title:""};
                    $("#add_sibling").addClass('disabled');
                    $("#add").addClass('disabled');
                    $("#add_root").addClass('disabled');
                }

                /**
                 * очистка поля после удаления
                 */
                function clearSelected() {
                    vm.selected = null;
                    vm.selectedCopy = {title:""};
                    nodeTitleChange();
                }

                /**
                 * получение выбранного узла
                 * @param branch
                 */
                function treeHandler(branch) {                     
                    vm.selected = branch;
                    vm.selectedCopy = angular.copy(branch);
                    if (!vm.selected.items || !vm.selected.items.length > 0) {
                        vm.selected.items = vm.selectedCopy.items = getCategoryItems(vm.selectedCopy.id);
                    }
                    nodeTitleChange();
                    newNodeTitleChange();
                }

                /**
                 * слушатель изменения поля текущего узла
                 */
                function nodeTitleChange(){
                    if(vm.selectedCopy.title === ""){
                        $("#edit").addClass('disabled');
                        $("#delete").addClass('disabled');
                    } else {
                        $("#edit").removeClass('disabled');
                        $("#delete").removeClass('disabled');
                    }
                }

                /**
                 * слушатель изменения поля имени нового узла
                 */
                function newNodeTitleChange(){
                    if (vm.currentNode.title !== "") {
                        if (vm.selected) {
                            $("#add_sibling").removeClass('disabled');
                            $("#add").removeClass('disabled');
                        }
                        $("#add_root").removeClass('disabled');
                    } else {
                        $("#add_sibling").addClass('disabled');
                        $("#add").addClass('disabled');
                        $("#add_root").addClass('disabled');
                    }
                }

                /**
                 * изменение имени узла
                 */
                function edit() {
                    if(vm.selectedCopy.title !== ""){
                        vm.selected.title = vm.selectedCopy.title;

                        // проверяем узел в списке новых
                        var b = helpers.findInArrayById(newCategoryList, vm.selectedCopy.id);
                        // если не нашли, меняем имя в сохраненом
                        if (!helpers.isEmptyObject(b)) {
                            b.title = vm.selected.title;
                        } else {
                            // проверяем узел в списке измененных
                            var e = helpers.findInArrayById(editCategoryList, vm.selectedCopy.id);
                            // если нашли меняем имя
                            if (!helpers.isEmptyObject(e)) {
                                e.title = vm.selected.title;
                            } else {
                                editCategoryList.push(vm.selected);
                            }
                        }
                    }
                }

                /**
                 * удаление узла из дерева
                 */
                function deleteNode() {
                    if(vm.selectedCopy.title !== ""){
                        var selected = vm.tree.get_selected_branch();
                        var parent = vm.tree.get_parent_branch(selected);
                        if(parent){
                            var idx = parent.nodes.indexOf(selected);
                            parent.nodes.splice(idx, 1);
                        } else {
                            vm.tree.delete_branch_by_uid(selected.uid);
                        }

                        // проверяем является ли удаляемый узел свежесозданным
                        var b = helpers.findInArrayById(newCategoryList, selected.id);
                        // удаляем узел из списка созданных
                        if (!helpers.isEmptyObject(b)) {
                            var b_idx = newCategoryList.indexOf(b);
                            newCategoryList.splice(b_idx, 1);
                        } else {
                            // добавляем в список на удаление
                            deleteCategoryList.push(selected);

                            // проверяем узел в списке измененных
                            var e = helpers.findInArrayById(editCategoryList, selected.id);
                            // удаляем узел из списка измененных
                            if (!helpers.isEmptyObject(e)) {
                                var e_idx = editCategoryList.indexOf(e);
                                editCategoryList.splice(e_idx, 1);
                            }
                        }

                        // если у родителя больше не осталось детей, помечаем как листовой
                        if(parent && helpers.isEmptyObject(parent.nodes)){
                            parent.noLeaf = false;
                        }
                        clearSelected();
                    }
                }

                /**
                 * Добавление узла в дерево
                 * @param selected
                 */
                function add(selected) {
                    if (vm.selected && vm.currentNode.title !== "") {
                        var parent, id = helpers.guid();
                        if (!selected) {
                            parent = vm.tree.get_selected_branch();
                        } else {
                            parent = selected;
                        }
                        // проверяем что родитель есть и он не пустой
                        var parentId = (parent && !$.isEmptyObject(parent)) ? parent.id : null;
                        // подготавливаем узел для добавления
                        var newNode = {id: id, title: $scope.currentNode.title, nodes: [], parentId: parentId, types: []};
                        // добавляем новый узел
                        if (parentId == null) {
                            vm.tree.add_branch(null, newNode);
                        } else {
                            vm.tree.add_branch(parent, newNode);
                        }
                        // если родитель был листовым, то меняем свойство
                        if(!parent.noLeaf){
                            parent.noLeaf = true;
                        }
                        // помещаем узел в список новых
                        newCategoryList.push(newNode);
                        // очищаем поле
                        clearNewNodeField();
                    }
                }

                /**
                 * Добавление узла на том же уровне что и выбранный
                 */
                function addAsSibling() {
                    if (vm.selected && vm.currentNode.title !== "") {
                        var selected = vm.tree.get_selected_branch();
                        var parent = vm.tree.get_parent_branch(selected);
                        if(parent){
                            add(parent);
                        } else {
                            add({});
                        }
                    }
                }

                /**
                 * Добавление нового корневого узла
                 */
                function addAsRoot() {
                    if (vm.currentNode.title !== "") {
                        add({});
                    }
                }

                /**
                 * Удаление товара
                 * @param idx
                 */
                function removeItem(idx) {
                    vm.selectedCopy.items.splice(idx, 1);
                    vm.selected.items = vm.selectedCopy.items;
                    //TODO check in edit and new lists
                    //check branch in new list
                    var b = helpers.findInArrayById(newCategoryList, vm.selected.id);
                    //if found change stashed title
                    if (!helpers.isEmptyObject(b)) {
                        b.items = [].concat(vm.selected.items);
                    } else {
                        //check in edit list
                        var e = helpers.findInArrayById(editCategoryList, vm.selected.id);
                        //if found update title
                        if (!helpers.isEmptyObject(e)) {
                            e.items = [].concat(vm.selected.items);
                        } else {
                            editCategoryList.push(vm.selected);
                        }
                    }
                }

                /**
                 * Сохранение изменений
                 */
                function save() {
                    dataResources.categoryTree.post({
                        new: newCategoryList,
                        update: editCategoryList,
                        delete: deleteCategoryList
                    });
                    // очистка временных списков
                    newCategoryList = [];
                    editCategoryList = [];
                    deleteCategoryList = [];
                }

                /**
                 * показ модального окна для добавления товара
                 */
                function showClss() {
                    var dialog = itemClssModal(vm.selectedCopy.items);
                    dialog.closePromise.then(function (output) {
                        if (output.value && output.value != '$escape') {
                            angular.forEach(output.value, function (item) {
                                vm.selectedCopy.items.push(item);
                            });
                            vm.selected.items = vm.selectedCopy.items;

                            //check branch in new list
                            var b = helpers.findInArrayById(newCategoryList, vm.selected.id);
                            //if found change stashed title
                            if (!helpers.isEmptyObject(b)) {
                                b.items = [].concat(vm.selected.items);
                            } else {
                                //check in edit list
                                var e = helpers.findInArrayById(editCategoryList, vm.selected.id);
                                //if found update title
                                if (!helpers.isEmptyObject(e)) {
                                    e.items = [].concat(vm.selected.items);
                                } else {
                                    editCategoryList.push(vm.selected);
                                }
                            }
                        }
                    });
                }

                /**
                 * Создание новой категории
                 */
                function createCategory() {
                    $state.go("category.card");
                }

                function isOpen(section) {
                    return menu.isSectionSelected(section);
                }

                function toggleOpen(section) {
                    vm.menu.toggleSelectSection(section);
                }

                function openSection(section){
                    $state.go("category.card", {id: section.id});
                }

                function getSections(nodes){
                    var sections = [];
                    angular.forEach(nodes, function(node) {
                        sections.push(convertNodeToMenu(node));
                    });
                    return sections;
                }

                function convertNodeToMenu(node) {
                    var section = {name:node.title, type: node.nodes.length > 0 ? "toggle":"link", id: node.id};
                    if(node.nodes.length > 0){
                        section.pages = getSections(node.nodes)
                    }
                    return section;
                }

                function getTemplateUrl() {
                    if (mvm.width < 601) {
                        return templatePath + "category-sm.html"
                    } else if (mvm.width > 600) {
                        return templatePath + "category-lg.html"
                    }
                }

                /**
                 * инициализация
                 */
                function init(){
                    if (mvm.width < 601) {
                        vm.menu.sections = getSections(vm.categories);
                    } else {
                        var newCategoryList = [],
                            editCategoryList = [],
                            deleteCategoryList = [];
                    }
                }

                init();

        }])

        .controller('categoryCardController', ['$scope', 'dataResources', 'modal', 'rootCategories', 'category', 'categoryItems', '$timeout', '$mdToast',
            function ($scope, dataResources, modal, rootCategories, category, categoryItems, $timeout, $mdToast) {
                /* for small only */
                var toast = $mdToast.simple().position('top right').hideDelay(3000);

                var vm = this;

                vm.init = init;
                vm.save = save;
                vm.showItemModal = showItemModal;
                vm.deleteCategory = deleteCategory;
                vm.removeItem = removeItem;

                vm.showHints = true;
                vm.categoryList = rootCategories;
                vm.categoryList.unshift({id: null, name: "Выберите категорию ..."});
                vm.data = {parentCategory: null, selectedCategory: category, categoryItems: categoryItems};

                /**
                 * Инициализация
                 */
                function init() {
                    if (category) {
                        vm.data.parentCategory = category.parentId
                    } else {
                        //TODO: disable delete
                    }
                }

                /**
                 *  Сохранение изменений в категории
                 */
                function save() {
                    var items = [];

                    if (vm.data.categoryItems) {
                        items = vm.data.categoryItems.map(function (item) {
                            return item['id'];
                        })
                    }

                    var dto = {
                        name: vm.data.selectedCategory.name,
                        parentId: vm.data.parentCategory,
                        items: items,
                        id: vm.data.selectedCategory.id
                    };

                    if (category) {
                        dataResources.category.put(dto).$promise.then(function (data) {
                            $mdToast.show(toast.textContent('Категория ['+ dto.name +'] успешно обновлена').theme('success'));
                        }, function (error) {
                            $mdToast.show(toast.textContent('Неудалось сохранить изменения').theme('error'));
                        })
                    } else {
                        dataResources.category.post(dto).$promise.then(function (data) {
                            $mdToast.show(toast.textContent('Категория ['+ dto.name +'] успешно создана').theme('success'));
                        }, function (error) {
                            $mdToast.show(toast.textContent('Неудалось сохранить изменения').theme('error'));
                        })
                    }
                }

                /**
                 * Показ модально окна для выбора товара
                 */
                function showItemModal() {
                    var dialog = modal({
                        templateUrl: "pages/modal/itemModal.html",
                        className: 'ngdialog-theme-default custom-width',
                        closeByEscape: true,
                        controller: "itemClssController",
                        data: vm.data.categoryItems
                    });

                    dialog.closePromise.then(function (output) {
                        if (output.value && output.value != '$escape') {
                            vm.data.categoryItems = output.value;
                        }
                    });
                }

                /**
                 * Удаление категории
                 */
                function deleteCategory(){
                    dataResources.category.delete({id: vm.data.selectedCategory.id}).$promise.then(function (data) {
                        $mdToast.show(toast.textContent('Категория ['+ vm.data.selectedCategory.name +'] успешно удалена').theme('success'));
                    }, function (error) {
                        $mdToast.show(toast.textContent('Неудалось удалить категорию').theme('error'));
                    })
                }

                /**
                 * Удаление товара из категории
                 * @param idx
                 */
                function removeItem(idx) {
                    vm.data.categoryItems.splice(idx, 1);
                }

                init();

            }])
})();

