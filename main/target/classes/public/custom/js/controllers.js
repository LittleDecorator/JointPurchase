//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //MAIN CONTROLLER//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    purchase.controller('mainController', function ($scope,$rootScope,$cookies, $state, loginModal,authService, factory,jwtHelper) {
        console.log("Enter main controller");

        //нажата кнопка меню login
        $scope.login = function(){
            loginModal()
                .then(function () {
                    $scope.refreshMenu();
                    //return $state.transitionTo(toState.name, toParams);
                })
                .catch(function () {
                    //return $state.go('welcome');
                    console.log("direct login failed");
                    //return $state.transitionTo('home');
                });

        };

        //явный logout через меню
        $scope.logout = function(){
            $cookies.remove('token');
            $rootScope.currentUser = {};
            $scope.refreshMenu();
            $state.transitionTo("home");
        };

        //обновление меню после login/logout
        $scope.refreshMenu = function(){
            $scope.menu = [];
            angular.forEach(menu.getMenu(),function(item){
                if(item.displayCondition){
                    if(item.displayCondition.admin && item.displayCondition.auth && authService.isAuth()){
                        $scope.menu.push(item);
                    } else if(item.displayCondition.auth && authService.isAuth()){
                        $scope.menu.push(item);
                    } else if(!item.displayCondition.auth && !authService.isAuth()){
                        $scope.menu.push(item);
                    }
                } else {
                    $scope.menu.push(item);
                }
                if($rootScope.currentUser){

                }
            })
        };

        //удаление всех promises для login из сервиса, отмена login'а
        $scope.cancel = $scope.$dismiss;

        //подтверждение аутентификации, получение token'а
        $scope.submit = function (email, password) {
            console.log("submit user");

            factory.authLogin.post({name: email,password:password},
                function (response) {
                    if(response && response.token){
                        var token = response.token;
                        $cookies.put('token',token);
                        console.log(token);
                        var decodedToken = jwtHelper.decodeToken(token);
                        $rootScope.currentUser.name = decodedToken.user;
                        $rootScope.currentUser.roles = decodedToken.roles;
                        //set current user promises
                        //$scope.$close(email);
                        $scope.$close(email);
                        $scope.refreshMenu();
                    }
                }, function(){
                    console.log("some error");
                    $scope.$dismiss;
                    //$scope.$close("fla");
                });

        };

        $scope.mainMenu = $scope.refreshMenu();
        $scope.auth = authService;
    });
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //ABOUT CONTROLLER//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    purchase.controller('aboutController', function ($scope) {
        $scope.message = 'Look! I am an about page.';
    });
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //CONTACT CONTROLLER//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    purchase.controller('contactController', function ($scope) {
        $scope.message = 'Contact us! JK. This is just a demo.';
    });
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //ORDER CONTROLLER//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    purchase.controller('orderController', function ($scope, $state, order,$stateParams, factory) {
        //TODO: Написать сервис подсчета товаров при заказах
        //TODO: Определиться со статусами заказа (можно ли редактироватьб и когда, или же это будет работать автоматом)
        console.log("Enter order controller");

        /* array of orders*/
        $scope.orders = [];
        /* current order */
        $scope.currentOrder = {};
        /* items of current order */
        $scope.currentOrderItems = [];
        /* map of item names */
        $scope.itemNames = [];
        /* inner private object */
        $scope.index = -1;

        /* map of person names */
        $scope.personNames = factory.personMap.get();

        /*items for selection*/
        $scope.itemNames = factory.itemMap.get();

        /* flag hide/show modal footer */
        $scope.hideModalFooter = false;

        $scope.selectedPerson = {};

        if (order) {
            $scope.currentOrder = order;
            $scope.selectedPerson = helpers.findInArrayById($scope.personNames, $scope.currentOrder.personId);

            factory.orderItems.get({id: order.id}, function (data) {
                angular.forEach(data, function (orderItem) {
                    var item = helpers.findInArrayById($scope.itemNames, orderItem.itemId);
                    orderItem.name = item.name;
                    $scope.currentOrderItems.push(orderItem);
                });
            });
        } else if ($stateParams.customerId) {
            $scope.orders = factory.orderByCustomerId.get({id: $stateParams.customerId}, function (data) {
                angular.forEach(data, function (order) {
                    var person = helpers.findInArrayById($scope.personNames, order.personId);
                    order.personName = person.name;
                });
            });
        } else {
            $scope.orders = factory.order.query(function (data) {
                angular.forEach(data, function (order) {
                    var person = helpers.findInArrayById($scope.personNames, order.personId);
                    order.personName = person.name;
                });
            });
        }

        $scope.toggleItems = function () {
            $scope.showAllItem = !$scope.showAllItem;
            $scope.hideModalFooter = true;
        };

        $scope.editOrder = function (id) {
            $state.transitionTo("order.detail",{id:id});
        };

        $scope.deleteOrder = function (id) {
            factory.order.delete({id: id});

            //find item in array
            var currOrder = helpers.findInArrayById($scope.orders, id);

            //find item index in array
            var idx = $scope.orders.indexOf(currOrder);
            //remove item from array
            $scope.orders.splice(idx, 1);
        };

        //create order
        $scope.addOrder = function () {
            $state.transitionTo("order.detail");
        };

        $scope.save = function () {
            //iterate over order items array, and create new clean array foe mapping
            var cleanOrderItems = [];
            $scope.currentOrderItems.forEach(function (item) {
                cleanOrderItems.push({
                    id: item.id,
                    orderId: item.orderId,
                    itemId: item.itemId,
                    cou: item.cou
                })
            });

            //prepare response object
            $scope.currentOrder.personId = $scope.selectedPerson.id;
            var respData = {
                order: $scope.currentOrder,
                items: cleanOrderItems
            };

            factory.order.save(respData);
        };

        //add item to order
        $scope.addItemsInOrder = function (id) {
            //TODO: Проверять что товар уже в заказе, и либо ничего не делать, либо увеличивать позицию на 1, либо убирать дублируемый товар из списка возможного
            //TODO: Нужна множественная выборка?????
            var selectedItem = helpers.findInArrayById($scope.itemNames, id);
            $scope.currentOrderItems.push({
                orderId: $scope.currentOrder.id,
                itemId: id,
                name: selectedItem.name,
                cou: 1
            });
            $scope.toggleItems();
        };

        //remove item from order
        $scope.remItemsFromOrder = function (item) {
            var idx = $scope.currentOrderItems.indexOf(item);
            if (idx > -1) {
                factory.orderedItem.delete({orderId: $scope.currentOrder.id, itemId: item.id});
                $scope.currentOrderItems.splice(idx, 1);
            }
        };

        //increment item cou in order
        $scope.incrementCou = function (id) {
            angular.forEach($scope.currentOrderItems, function (item) {
                if (item.id == id) {
                    item.cou++;
                    return true;
                } else {
                    return false;
                }
            })
        };

        //decrement item cou in order
        $scope.decrementCou = function (id) {
            angular.forEach($scope.currentOrderItems, function (item) {
                if (item.id == id) {
                    if (item.cou > 0) {
                        item.cou--;
                    }
                    return true;
                } else {
                    return false;
                }
            })
        }

    });
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //PERSON CONTROLLER//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    purchase.controller('personController', function ($scope, $state, person, factory) {
        console.log("Enter person controller");
        //TODO: делать неактивным кнопку показа заказов клиента, если их нет
        //TODO: возможно стоит показывать в таблице кол-во заказов

        $scope.current = {};
        $scope.companyNames = factory.companyMap.get();
        $scope.selectedCompany = {};
        if(person){
            $scope.current = person;
            if ($scope.current.companyId != null) {
                $scope.selectedCompany = helpers.findInArrayById($scope.companyNames, $scope.current.companyId);
                $scope.current.isEmployer = true;
            } else {
                $scope.current.isEmployer = false;
            }
        } else {
            $scope.customers = factory.customer.query();
        }

        $scope.editPerson = function (id) {
            $state.transitionTo("person.detail",{id:id});
        };

        $scope.deletePerson = function (id) {
            factory.customer.delete({id: id});
            var currPerson = helpers.findInArrayById($scope.customers, id);
            var idx = $scope.customers.indexOf(currPerson);
            $scope.customers.splice(idx, 1);
        };

        $scope.addPerson = function () {
            $state.transitionTo("person.detail");
        };

        //show customer orders, pass id via route
        $scope.showOrders = function (id) {
            $state.transitionTo("order", {customerId: id});
        };

        $scope.save = function () {
            if ($scope.selectedCompany != null) {
                $scope.current.companyId = $scope.selectedCompany.id;
            }
            factory.customer.save($scope.current);
        };

    });
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //COMPANY CONTROLLER//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    purchase.controller('companyController', function ($scope, $state, company, factory) {
        console.log("Enter company controller");

        $scope.company = {};

        if (company) {
            $scope.company = company;
        } else {
            $scope.companies = factory.company.query();
        }

        //create new company
        $scope.addCompany = function () {
            $state.transitionTo("company.detail");
        };

        //show company details
        $scope.edit = function (id) {
            $state.transitionTo("company.detail",{id:id});
        };

        $scope.save = function () {
            factory.company.save($scope.company);
        };

        $scope.delete = function (id) {
            factory.company.delete({id: id});
            var company = helpers.findInArrayById($scope.companies, id);
            var idx = $scope.companies.indexOf(company);
            $scope.companies.splice(idx, 1);
        };

    });
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //ITEM CONTROLLER//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    purchase.controller('itemController', function ($scope, $state, item, factory) {
        console.log("enter item controller");
        //for filter you may use % for replace other symbols

        //maps
        $scope.companyNames = factory.companyMap.get();
        $scope.categoryTypes = factory.categoryMap.get();

        //filter
        $scope.filter = {};
        $scope.filter.selectedCompany = "";
        $scope.filter.selectedCategory = "";

        //selected
        $scope.selected = {};
        $scope.selected.company = {};
        $scope.selected.category = {};

        //main
        $scope.index = null;
        $scope.items = [];
        $scope.filteredItems = [];

        //pagination
        $scope.currPage = 1;
        $scope.maxPage = 5;
        $scope.itemsPerPage = 10;
        $scope.totalItems = null;

        console.log(item);

        if(item){
            $scope.selected = item;
            //find company in company list for select
            $scope.selected.company = helpers.findInArrayById($scope.companyNames, $scope.selected.companyId);
            //find category in category list for select
            $scope.selected.category = helpers.findInArrayById($scope.categoryTypes, $scope.selected.categoryId);
        } else {
            console.log("get all");
            //get all items
            factory.item.query(function (data) {
                angular.forEach(data, function (item) {
                    var company = helpers.findInArrayById($scope.companyNames, item.companyId);
                    item.companyName = company.name;
                    var category = helpers.findInArrayById($scope.categoryTypes, item.categoryId);
                    item.categoryType = category.name;
                    $scope.items.push(item);
                });
                //get total items cou, for pagination
                $scope.totalItems = $scope.items.length;

                //listener for some scope variables
                $scope.$watch('currPage + itemsPerPage', function () {
                    $scope.subList();
                });
            });
        }

        //get sub list of items for single page display
        $scope.subList = function () {
            var begin = (($scope.currPage - 1) * $scope.itemsPerPage),
                end = begin + $scope.itemsPerPage;

            $scope.filteredItems = $scope.items.slice(begin, end);
        };

        //open modal for new item creation
        $scope.addItem = function () {
            this.clearSelected();
            $state.transitionTo("item.detail");
        };

        $scope.clearSelected = function(){
            $scope.selected = {};
            $scope.selected.company = {};
            $scope.selected.category = {};
        };

        //edit item
        $scope.editItem = function (id) {
            $state.transitionTo("item.detail", {id: id});

        };

        //delete current item
        $scope.deleteItem = function (id) {
            //TODO: обрабатывать ситуацию, когда на странице был удален последняя запись
            //TODO: обрабатывать ситуацию, когда страница одна
            //delete item from db
            factory.item.delete({id: id});
            //find item in array
            var currItem = helpers.findInArrayById($scope.items, id);
            //find item index in array
            var idx = $scope.items.indexOf(currItem);
            //remove item from array
            $scope.items.splice(idx, 1);
            $scope.subList();
        };

        //modal button save listener
        $scope.save = function () {
            $scope.selected.companyId = $scope.selected.company.id;
            $scope.selected.categoryId = $scope.selected.category.id;
            factory.item.save($scope.selected);
        };

        //filter table
        $scope.filter.apply = function () {
            $scope.items = [];
            factory.itemFilter.apply(helpers.getFilterItem(), function (data) {
                angular.forEach(data, function (item) {
                    var company = helpers.findInArrayById($scope.companyNames, item.companyId);
                    item.companyName = company.name;
                    var category = helpers.findInArrayById($scope.categoryTypes, item.categoryId);
                    item.categoryType = category.name;
                    $scope.items.push(item);
                });
                $scope.currPage = 1;
                $scope.subList();
            })
        };

        //clear filter
        $scope.filter.clear = function () {
            helpers.clearFilterItem();
            $scope.items = [];
            factory.item.query(function (data) {
                angular.forEach(data, function (item) {
                    var company = helpers.findInArrayById($scope.companyNames, item.companyId);
                    item.companyName = company.name;
                    var category = helpers.findInArrayById($scope.categoryTypes, item.categoryId);
                    item.categoryType = category.name;
                    $scope.items.push(item);
                });
                $scope.currPage = 1;
                $scope.subList();
            });
        };

        $scope.showGallery = function (id) {
            $scope.currentItem = helpers.findInArrayById($scope.filteredItems, id);
            $state.go("item.gallery", {itemId: id});
        }


    });
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //GALLERY CONTROLLER//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    purchase.controller('galleryController', function ($scope, $location, $state, $stateParams, factory, FileUploader) {
        console.log("Enter gallery controller");

        var uploader = $scope.uploader = new FileUploader();

        $scope.images = [];

        //$scope.files = [];

        /* init section */
        if ($stateParams.itemId) {
            $scope.images = factory.itemImage.get({itemId: $stateParams.itemId});
        } else {
            $scope.images = factory.itemImage.get();
        }

        $scope.currentImage = {};

        //toggle modal image
        $scope.toggleGallery = function () {
            $scope.showUpload = !$scope.showUpload;
        };

        $scope.toggleView = function () {
            $scope.showImage = !$scope.showImage;
        };

        $scope.uploadFiles = function () {
            var items = uploader.getNotUploadedItems();
            var formData = new FormData();

            angular.forEach(items, function (item, idx) {
                formData.append("file" + idx, item._file);
            });

            formData.append("itemId", $stateParams.itemId);

            factory.itemContent.upload(formData, function(data){
                angular.forEach(data, function (image) {
                    //var path = $location.absUrl().substring(0,$location.absUrl().indexOf("#"));
                    //image.url = path + image.url;
                    $scope.images.push(image);
                    uploader.clearQueue();
                });
            });
            $scope.toggleGallery();
        };

        $scope.imageView = function (id) {
            $scope.currentImage = factory.image.get({contentId: id});
            $scope.toggleView();
        };

        // CALLBACKS

        /*uploader.onWhenAddingFileFailed = function(item */
        /*{File|FileLikeObject}*/
        /*, filter, options) {
         console.info('onWhenAddingFileFailed', item, filter, options);
         };
         uploader.onAfterAddingFile = function(fileItem) {
         console.info('onAfterAddingFile', fileItem);
         };*/
        uploader.onAfterAddingAll = function (addedFileItems) {
            console.info('onAfterAddingAll', addedFileItems);
            $scope.toggleGallery();
        };
        /*uploader.onBeforeUploadItem = function(item) {
         console.info('onBeforeUploadItem', item);
         };
         uploader.onProgressItem = function(fileItem, progress) {
         console.info('onProgressItem', fileItem, progress);
         };
         uploader.onProgressAll = function(progress) {
         console.info('onProgressAll', progress);
         };
         uploader.onSuccessItem = function(fileItem, response, status, headers) {
         console.info('onSuccessItem', fileItem, response, status, headers);
         };
         uploader.onErrorItem = function(fileItem, response, status, headers) {
         console.info('onErrorItem', fileItem, response, status, headers);
         };
         uploader.onCancelItem = function(fileItem, response, status, headers) {
         console.info('onCancelItem', fileItem, response, status, headers);
         };
         uploader.onCompleteItem = function(fileItem, response, status, headers) {
         console.info('onCompleteItem', fileItem, response, status, headers);
         };
         uploader.onCompleteAll = function() {
         console.info('onCompleteAll');
         };

         console.info('uploader', uploader);*/

        //if use file reader api raw
        /*$scope.handleFile = function(files){

         var formData = new FormData();
         for (var i = 0, f; f = files[i]; i++) {
         formData.append("file"+i,f);
         formData.append("itemId",$stateParams.itemId);
         factory.itemFiles.upload(formData);
         }

         }*/

    });
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //PRODUCT CONTROLLER//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    purchase.controller('productController', function ($scope, $state, factory) {
        console.log("Enter Product controller");

        $scope.data = [];

        factory.categoryTree.get(function(content){
            $scope.data.push(content);
        });

        /*$scope.data = [
            {
                "id": 1,
                "title": "node1",
                "nodes": [
                    {
                        "id": 11,
                        "title": "node1.1",
                        "nodes": [
                            {
                                "id": 111,
                                "title": "node1.1.1",
                                "nodes": []
                            }
                        ]
                    }
                ]
            }
        ];*/

        //maps
        $scope.companyNames = factory.companyMap.get();
        $scope.categoryTypes = factory.categoryMap.get();

        $scope.items = [];

        //pagination
        $scope.currPage = 1;
        $scope.maxPage = 5;
        $scope.itemsPerPage = 10;
        $scope.totalItems = null;

        //get all items
        factory.previewItems.get(function (data) {
            angular.forEach(data, function (item) {
                $scope.items.push(item);
            });
            //get total items cou, for pagination
            $scope.totalItems = $scope.items.length;

            //listener for some scope variables
            $scope.$watch('currPage + itemsPerPage', function () {
                $scope.subList();
            });
        });

        $scope.toggle = function(scope) {
            scope.toggle();
        };

        var getRootNodesScope = function() {
            return angular.element(document.getElementById("tree-root")).scope();
        };

        $scope.collapseAll = function() {
            var scope = getRootNodesScope();
            scope.collapseAll();
        };

        $scope.expandAll = function() {
            var scope = getRootNodesScope();
            scope.expandAll();
        };

        $scope.options = {};

        //get sub list of items for single page display
        $scope.subList = function () {
            var begin = (($scope.currPage - 1) * $scope.itemsPerPage),
                end = begin + $scope.itemsPerPage;

            $scope.filteredItems = $scope.items.slice(begin, end);
        };

        $scope.itemView = function(id){
            $state.transitionTo("product.detail", {itemId: id});
        };

        $scope.filterByCategory = function(categoryId){
            console.log(categoryId);
            factory.previewItems.filter({categoryId:categoryId},function(data){
                console.log(data);
                $scope.items = [];
                angular.forEach(data, function (item) {
                    $scope.items.push(item);
                });
                //get total items cou, for pagination
                $scope.totalItems = $scope.items.length;

                //listener for some scope variables
                $scope.$watch('currPage + itemsPerPage', function () {
                    $scope.subList();
                });
            })
        }

    });
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //ITEM DETAIL CONTROLLER//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    purchase.controller('detailController', function ($scope, $state, product) {
        $scope.item = product;
        //console.log($scope.item);
    });
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //REGISTRATION CONTROLLER//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    purchase.controller('registrationController',function($scope,$state,factory){
        $scope.register = function(){
            console.log($scope);
        }
    });