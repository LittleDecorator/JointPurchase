(function () {
    angular.module('category', []);
})();

(function () {
    'use strict';

    angular.module('category')
        .controller('categoryController', ['$scope', '$state', 'categoryNodes', '$timeout', 'dataResources','itemClssModal','eventService',
            function ($scope, $state, categoryNodes, $timeout, dataResources, itemClssModal) {

            var templatePath = "pages/fragment/category/";

            /* for big one (TEMPORARY COMMENTED)*/
            var newCategoryList = [],
                editCategoryList = [],
                deleteCategoryList = [];

            $scope.categories = categoryNodes;
            $scope.tree = {};
            $scope.selected = null;
            $scope.selectedCopy = null;
            $scope.currentNode = {title:""};

            // получение товаров выбранного узла
            function getCategoryItems(categoryId) {
                var items = [];
                dataResources.categoryItems.get({id: categoryId}, function (data) {
                    angular.forEach(data, function (rec) {
                        items.push(rec);
                    });
                });
                return items;
            }
            
            /* очистка поля ввода нового узла */
            function clearNewNodeField() {
                $scope.currentNode = {title:""};
                $("#add_sibling").addClass('disabled');
                $("#add").addClass('disabled');
                $("#add_root").addClass('disabled');
            }

            /* очистка поля после удаления */
            function clearSelected() {
                $scope.selected = null;
                $scope.selectedCopy = {title:""};
                $scope.nodeTitleChange();
            }

            // получение выбранного узла
            $scope.treeHandler = function (branch) {
                console.log(branch)
                // если работаем с мобилы, то будет переход на другую страницу
                if ($scope.width < 601) {
                    $state.go("category.card", {id: branch.id});
                } else {
                    $scope.selected = branch;
                    $scope.selectedCopy = angular.copy(branch);
                    if (!$scope.selected.items || !$scope.selected.items.length > 0) {
                        console.log($scope.selectedCopy)
                        $scope.selected.items = $scope.selectedCopy.items = getCategoryItems($scope.selectedCopy.id);
                    }
                    $scope.nodeTitleChange();
                    $scope.newNodeTitleChange();
                }
            };

            /* слушатель изменения поля текущего узла */
            $scope.nodeTitleChange = function(){
                if($scope.selectedCopy.title === ""){
                    $("#edit").addClass('disabled');
                    $("#delete").addClass('disabled');
                } else {
                    $("#edit").removeClass('disabled');
                    $("#delete").removeClass('disabled');
                }
            };
            
            /* слушатель изменения поля имени нового узла */
            $scope.newNodeTitleChange = function(){
                if ($scope.currentNode.title !== "") {
                    if ($scope.selected) {
                        $("#add_sibling").removeClass('disabled');
                        $("#add").removeClass('disabled');
                    }
                    $("#add_root").removeClass('disabled');
                } else {
                    $("#add_sibling").addClass('disabled');
                    $("#add").addClass('disabled');
                    $("#add_root").addClass('disabled');
                }
            };

            // /* toggle listener */
            // $scope.toggleHandler = function (branch) {
            //     console.log(branch);
            //     if (branch.nodes.length == 0 && branch.noLeaf) {
            //         console.log("bla");
            //         // dataResources.settings.tournamentLevel.get({},branch).$promise.then(function(data){
            //         //     angular.forEach(data, function(node){
            //         //         $scope.tree.add_branch(branch,node);
            //         //     });
            //         // })
            //     }
            // };

            // изменение имени узла
            $scope.edit = function () {
                if($scope.selectedCopy.title !== ""){
                    $scope.selected.title = $scope.selectedCopy.title;

                    // проверяем узел в списке новых
                    var b = helpers.findInArrayById(newCategoryList, $scope.selectedCopy.id);
                    // если не нашли, меняем имя в сохраненом
                    if (!helpers.isEmptyObject(b)) {
                        b.title = $scope.selected.title;
                    } else {
                        // проверяем узел в списке измененных
                        var e = helpers.findInArrayById(editCategoryList, $scope.selectedCopy.id);
                        // если нашли меняем имя
                        if (!helpers.isEmptyObject(e)) {
                            e.title = $scope.selected.title;
                        } else {
                            editCategoryList.push($scope.selected);
                        }
                    }
                }
            };

            /* удаление узла из дерева */
            $scope.delete = function () {
                if($scope.selectedCopy.title !== ""){
                    var selected = $scope.tree.get_selected_branch();
                    var parent = $scope.tree.get_parent_branch(selected);
                    if(parent){
                        var idx = parent.nodes.indexOf(selected);
                        parent.nodes.splice(idx, 1);
                    } else {
                        $scope.tree.delete_branch_by_uid(selected.uid);
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
            };

            /* Добавление узла в дерево */
            $scope.add = function (selected) {
                if ($scope.selected && $scope.currentNode.title !== "") {
                    var parent, id = helpers.guid();
                    if (!selected) {
                        parent = $scope.tree.get_selected_branch();
                    } else {
                        parent = selected;
                    }
                    // проверяем что родитель есть и он не пустой
                    var parentId = (parent && !$.isEmptyObject(parent)) ? parent.id : null;
                    // подготавливаем узел для добавления
                    var newNode = {id: id, title: $scope.currentNode.title, nodes: [], parentId: parentId, types: []};
                    console.log(newNode);
                    // добавляем новый узел
                    if (parentId == null) {
                        $scope.tree.add_branch(null, newNode);
                    } else {
                        $scope.tree.add_branch(parent, newNode);
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
            };

            /* Добавление узла на том же уровне что и выбранный */
            $scope.addAsSibling = function () {
                console.log($scope.selected);
                if ($scope.selected && $scope.currentNode.title !== "") {
                    var selected = $scope.tree.get_selected_branch();
                    console.log(selected);
                    var parent = $scope.tree.get_parent_branch(selected);
                    if(parent){
                        $scope.add(parent);
                    } else {
                        $scope.add({});
                    }

                }
            };

            /* Добавление нового корневого узла */
            $scope.addAsRoot = function () {
                if ($scope.currentNode.title !== "") {
                    $scope.add({});
                }
            };

            /* Удаление товара */
            $scope.removeItem = function (idx) {
                $scope.selectedCopy.items.splice(idx, 1);
                $scope.selected.items = $scope.selectedCopy.items;
                //TODO check in edit and new lists
                //check branch in new list
                var b = helpers.findInArrayById(newCategoryList, $scope.selected.id);
                //if found change stashed title
                if (!helpers.isEmptyObject(b)) {
                    b.items = [].concat($scope.selected.items);
                } else {
                    //check in edit list
                    var e = helpers.findInArrayById(editCategoryList, $scope.selected.id);
                    //if found update title
                    if (!helpers.isEmptyObject(e)) {
                        e.items = [].concat($scope.selected.items);
                    } else {
                        editCategoryList.push($scope.selected);
                    }
                }
            };

            /* Сохранение изменений */
            $scope.save = function () {
                console.log({new: newCategoryList, update: editCategoryList, delete: deleteCategoryList});
                dataResources.categoryTree.post({
                    new: newCategoryList,
                    update: editCategoryList,
                    delete: deleteCategoryList
                });
                // очистка временных списков
                newCategoryList = [];
                editCategoryList = [];
                deleteCategoryList = [];
            };

            /* показ модального окна для добавления товара */
            $scope.showClss = function () {
                console.log($scope.selectedCopy.items);
                var dialog = itemClssModal($scope.selectedCopy.items);
                dialog.closePromise.then(function (output) {
                    if (output.value && output.value != '$escape') {
                        angular.forEach(output.value, function (item) {
                            $scope.selectedCopy.items.push(item);
                        });
                        $scope.selected.items = $scope.selectedCopy.items;

                        //check branch in new list
                        var b = helpers.findInArrayById(newCategoryList, $scope.selected.id);
                        //if found change stashed title
                        if (!helpers.isEmptyObject(b)) {
                            b.items = [].concat($scope.selected.items);
                        } else {
                            //check in edit list
                            var e = helpers.findInArrayById(editCategoryList, $scope.selected.id);
                            //if found update title
                            if (!helpers.isEmptyObject(e)) {
                                e.items = [].concat($scope.selected.items);
                            } else {
                                editCategoryList.push($scope.selected);
                            }
                        }
                    }
                });
            };

            /* Создание новой категории */
            $scope.createCategory = function () {
                $state.go("category.card");
            };

            $scope.getTemplateUrl = function () {
                if ($scope.width < 601) {
                    return templatePath + "category-sm.html"
                }
                if ($scope.width > 600) {
                    return templatePath + "category-lg.html"
                }
            };

        }])

        .controller('categoryCardController', ['$scope', 'dataResources', 'modal', 'rootCategories', 'category', 'categoryItems', '$timeout', function ($scope, dataResources, modal, rootCategories, category, categoryItems, $timeout) {
            /* for small only */
            console.log(rootCategories)
            console.log(category)
            console.log(categoryItems)
            $scope.categoryList = rootCategories;
            $scope.categoryList.unshift({id: null, name: "Выберите категорию ..."});
            $scope.data = {parentCategory: null, selectedCategory: category, categoryItems: categoryItems};

            if (category) {
                $scope.data.parentCategory = category.parentId
            } else {
                //TODO: disable delete
            }

            $scope.save = function () {
                var items = [];
                if ($scope.data.categoryItems) {
                    items = $scope.data.categoryItems.map(function (item) {
                        return item['id'];
                    })
                }
                var dto = {
                    name: $scope.data.selectedCategory.name,
                    parentId: $scope.data.parentCategory,
                    items: items,
                    id: $scope.data.selectedCategory.id
                };

                if (category) {
                    dataResources.category.put(dto).$promise.then(function (data) {
                        Materialize.toast('Category UPDATE success', 3000);
                    }, function (error) {
                        Materialize.toast('Category UPDATE failed', 3000);
                    })
                } else {
                    dataResources.category.post(dto).$promise.then(function (data) {
                        Materialize.toast('Category CREATE success', 3000);
                    }, function (error) {
                        Materialize.toast('Category CREATE failed', 3000);
                    })
                }
            };

            $scope.showItemModal = function () {
                var dialog = modal({
                    templateUrl: "pages/modal/itemModal.html",
                    className: 'ngdialog-theme-default custom-width',
                    closeByEscape: true,
                    controller: "itemClssController",
                    data: $scope.data.categoryItems
                });
                dialog.closePromise.then(function (output) {
                    if (output.value && output.value != '$escape') {
                        $scope.data.categoryItems = output.value;
                    }
                });
            };

            $scope.delete = function () {
                dataResources.category.delete({id: $scope.data.selectedCategory.id}).$promise.then(function (data) {
                    Materialize.toast('Category DELETE success', 3000);
                }, function (error) {
                    Materialize.toast('Category DELETE failed', 3000);
                })
            };

            $scope.removeItem = function (idx) {
                $scope.data.categoryItems.splice(idx, 1);
            };

            $timeout(function () {
                // $('.toc-wrapper').pushpin({ offset: 100});
                $('select').material_select();
            }, 10);

        }])

        .controller('categoryListController', [ '$rootScope', '$log', '$state', '$timeout', '$location', 'menu', 'categoryNodes',
            function ($rootScope, $log, $state, $timeout, $location, menu, categoryNodes) {

                var vm = this;
                var templatePath = "pages/fragment/category/";

                //functions for menu-link and menu-toggle
                vm.getTemplateUrl = getTemplateUrl;
                vm.isOpen = isOpen;
                vm.toggleOpen = toggleOpen;
                vm.convertNodeToMenu = convertNodeToMenu;
                vm.getSections = getSections;
                vm.openSection = openSection;
                vm.autoFocusContent = false;
                vm.menu = menu;
                vm.categories = categoryNodes;

                vm.status = {
                    isFirstOpen: false,
                    isFirstDisabled: false
                };

                function isOpen(section) {
                    return menu.isSectionSelected(section);
                }

                function toggleOpen(section) {
                    menu.toggleSelectSection(section);
                }
                
                function openSection(section){
                    $state.go("category.card", {id: section.id});
                }

                function getTemplateUrl() {
                    return templatePath + "category-sm.html"
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

                vm.menu.sections = getSections(vm.categories);

                console.log(vm.menu);

            }])
})();

