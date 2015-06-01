    purchase.controller('mainController', function ($scope) {
        console.log("Enter main controller");
    });

    purchase.controller('aboutController', function ($scope) {
        $scope.message = 'Look! I am an about page.';
    });

    purchase.controller('contactController', function ($scope) {
        $scope.message = 'Contact us! JK. This is just a demo.';
    });

//просто заглушка
    purchase.controller('stub', function ($scope) {
        //empty
    });

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //MENU CONTROLLER//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*purchase.controller("menuController", function($scope){
     $scope.status = {
     isopen: false
     };

     $scope.toggled = function(open) {
     $log.log('Dropdown is now: ', open);
     };

     $scope.toggleDropdown = function($event) {
     $event.preventDefault();
     $event.stopPropagation();
     $scope.status.isopen = !$scope.status.isopen;
     };
     });*/
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //ORDER CONTROLLER//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    purchase.controller('orderController', function ($scope, $state, $stateParams, factory) {

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

        /* init section */
        if ($stateParams.customerId) {
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

        $scope.toggleModal = function () {
            $scope.showModal = !$scope.showModal;
        };

        $scope.toggleItems = function () {
            $scope.showAllItem = !$scope.showAllItem;
            $scope.hideModalFooter = true;
        };

        $scope.editOrder = function (id) {
            $scope.currentOrderItems = [];
            $scope.currentOrder = helpers.findInArrayById($scope.orders, id);
            $scope.selectedPerson = helpers.findInArrayById($scope.personNames, $scope.currentOrder.personId);
            //find current item index in array
            $scope.index = $scope.orders.indexOf($scope.currentOrder);
            //show modal
            $scope.toggleModal();

            /* get normalize order items */
            factory.orderItems.get({id: $scope.currentOrder.id}, function (data) {
                angular.forEach(data, function (orderItem) {
                    var item = helpers.findInArrayById($scope.itemNames, orderItem.itemId);
                    orderItem.name = item.name;
                    $scope.currentOrderItems.push(orderItem);
                });
            });
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
            $scope.currentOrder = {};
            $scope.priv.idx = -1;
            $scope.toggleModal();
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
            var respData = {
                order: $scope.currentOrder,
                items: cleanOrderItems
            };

            //call save, hide modal and update current order in array
            factory.order.save(respData, function (order) {
                $scope.toggleModal();

                var person = helpers.findInArrayById($scope.personNames, order.personId);
                order.personName = person.name;

                if ($scope.index > -1) {
                    $scope.orders.splice($scope.index, 1, order);
                } else {
                    $scope.orders.push(order);
                }
            });

        };

        $scope.cancel = function () {
            $scope.toggleModal();
        };

        //add item to order
        $scope.addItemsInOrder = function (id) {
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
    purchase.controller('personController', function ($scope, $state, $stateParams, factory) {

        console.log("Enter person controller");

        $scope.currentPerson = {};
        $scope.companyNames = factory.companyMap.get();
        $scope.selectedCompany = {};

        $scope.customers = factory.customer.query();

        $scope.toggleModal = function () {
            $scope.showModal = !$scope.showModal;
        };

        $scope.editPerson = function (id) {
            $scope.currentPerson = helpers.findInArrayById($scope.customers, id);
            if ($scope.currentPerson.companyId != null) {
                $scope.selectedCompany = helpers.findInArrayById($scope.companyNames, $scope.currentPerson.companyId);
                $scope.currentPerson.isEmployer = true;
            } else {
                $scope.currentPerson.isEmployer = false;
                $scope.selectedCompany = {};
            }
            $scope.toggleModal();
        };

        $scope.deletePerson = function (id) {
            factory.customer.delete({id: id});
            var currPerson = helpers.findInArrayById($scope.customers, id);
            var idx = $scope.customers.indexOf(currPerson);
            $scope.customers.splice(idx, 1);
        };

        $scope.addPerson = function () {
            $scope.currentPerson = {};
            $scope.index = -1;
            $scope.toggleModal();
        };

        //show customer orders, pass id via route
        $scope.showOrders = function (id) {
            $state.go(route.orders, {customerId: id});
        };

        $scope.save = function () {
            if ($scope.selectedCompany != null) {
                $scope.currentPerson.companyId = $scope.selectedCompany.id;
            }
            factory.customer.save($scope.currentPerson, function (data) {
                $scope.toggleModal();

                if ($scope.index > -1) {
                    $scope.customers.splice($scope.index, 1, data);
                } else {
                    $scope.customers.push(data);
                }
            });
        };

        $scope.cancel = function () {
            $scope.toggleModal();
        };

    });
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //COMPANY CONTROLLER//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    purchase.controller('companyController', function ($scope, $state, $stateParams, factory) {

        console.log("Enter company controller");

        console.log($stateParams);

        $scope.index = -1;

        //toggle modal view
        $scope.toggleModal = function () {
            $scope.showModal = !$scope.showModal;
        };

        if ($stateParams.company) {
            $scope.company = JSON.parse($stateParams.company);
        } else {
            //get all companies
            $scope.companies = factory.company.query();
        }

        //create new company
        $scope.addCompany = function () {
            $scope.company = {};
            $scope.index = -1;
            $scope.toggleModal();
        };

        //show company details
        $scope.showDetails = function (id) {
            $scope.companies.every(function (company) {
                if (company.id == id) {
                    $scope.company = company;
                    return false;
                } else {
                    return true;
                }
            });
            $scope.toggleModal();
        };

        /*    $scope.showEmployers = function(){
         $state.go(route.person,{companyId:$scope.company.id});
         };

         $scope.showGoods = function(){
         $state.go(route.item,{companyId:$scope.company.id});
         }*/

        $scope.save = function () {
            factory.company.save($scope.company, function (data) {
                $scope.toggleModal();

                if ($scope.index > -1) {
                    $scope.companies.splice($scope.index, 1, data);
                } else {
                    $scope.companies.push(data);
                }
                $scope.company = data;
            });
        };

        $scope.cancel = function () {
            $scope.toggleModal();
        };
    });
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //ITEM CONTROLLER//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    purchase.controller('itemController', function ($scope, $state, factory) {
        console.log("enter item controller");


        //for filter you may use % for replace other symbols

        //maps
        $scope.companyNames = factory.companyMap.get();
        $scope.categoryTypes = factory.categoryMap.get();

        //filter
        $scope.filter = {};
        $scope.filter.selectedCompany = "";
        $scope.filter.selectedCategory = "";

        //modal
        $scope.modal = {};

        //selected
        $scope.selected = {};
        $scope.selected.company = {};
        $scope.selected.category = {};

        //main
        $scope.index = null;
        $scope.items = [];
        $scope.filteredItems = [];
        $scope.currentItem = {};

        //pagination
        $scope.currPage = 1;
        $scope.maxPage = 5;
        $scope.itemsPerPage = 10;
        $scope.totalItems = null;


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

        //get sub list of items for single page display
        $scope.subList = function () {
            var begin = (($scope.currPage - 1) * $scope.itemsPerPage),
                end = begin + $scope.itemsPerPage;

            $scope.filteredItems = $scope.items.slice(begin, end);
        };

        //toggle modal view
        $scope.toggleModal = function () {
            $scope.showModal = !$scope.showModal;
        };


        //open modal for new item creation
        $scope.addItem = function () {
            $scope.modal = {};
            $scope.index = -1;
            $scope.selected.company = {};
            $scope.selected.category = {};
            $scope.toggleModal();
        };

        //edit item
        $scope.editItem = function (id) {
            //find our item in items array by id
            $scope.currentItem = helpers.findInArrayById($scope.filteredItems, id);
            //make item copy for modal
            $scope.modal = $.extend({}, $scope.currentItem);

            //find current item index in array
            $scope.index = $scope.filteredItems.indexOf($scope.currentItem);

            //find company in company list for select
            $scope.selected.company = helpers.findInArrayById($scope.companyNames, $scope.currentItem.companyId);
            //find category in category list for select
            $scope.selected.category = helpers.findInArrayById($scope.categoryTypes, $scope.currentItem.categoryId);

            //show modal
            $scope.toggleModal();

        };

        //delete current item
        $scope.deleteItem = function (id) {
            //delete item from db
            factory.items.delete({id: id});
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
            //need until edit select elements
            //fill edited item properly
            $scope.modal.companyId = $scope.selected.company.id;
            $scope.modal.companyName = $scope.selected.company.name;
            $scope.modal.categoryId = $scope.selected.category.id;
            $scope.modal.categoryType = $scope.selected.category.name;

            factory.items.save($scope.modal, function (data) {
                var company = helpers.findInArrayById($scope.companyNames, data.companyId);
                data.companyName = company.name;
                var category = helpers.findInArrayById($scope.categoryTypes, data.categoryId);
                data.categoryType = category.name;
                $scope.toggleModal();

                if ($scope.index > -1) {
                    //merge old object with new one
                    $.extend($scope.currentItem, $scope.modal);
                } else {
                    $scope.items.push(data);
                }
                $scope.subList();
            });
        };

        //modal button cancel listener
        $scope.cancel = function () {
            $scope.toggleModal();
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
            factory.items.query(function (data) {
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
            $state.go(route.gallery, {itemId: id});
        }


    });
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //GALLERY CONTROLLER//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    purchase.controller('galleryController', function ($scope, $state, $stateParams, factory, FileUploader) {
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
            var upImageArray = factory.itemContent.upload(formData);
            console.log(upImageArray);

            angular.forEach(upImageArray, function (image) {
                console.log(image);
                $scope.images.push(image);
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
    //AUTH CONTROLLER//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    purchase.controller('authController', function ($scope, $http, factory, tokenHandler) {
        console.log("Enter auth controller");

        $scope.greeting = 'Welcome to the JSON Web Token / AngularJR / Spring example!';
        $scope.token = null;
        $scope.error = null;
        $scope.roleUser = false;
        $scope.roleAdmin = false;
        $scope.roleFoo = false;

        $scope.login = function () {
            $scope.error = null;
            factory.authLogin.post({name: $scope.userName},
                function (token) {
                    $scope.token = token;
                    console.log($scope.token);
                    $http.defaults.headers.common.Authorization = 'Bearer ' + token;
                    console.log(1);
                    $scope.checkRoles();
                },
                function (error) {
                    $scope.error = error;
                    $scope.userName = '';
                });
        };

        $scope.checkRoles = function () {
            console.log("checkRoles");
            console.log(tokenHandler);
            console.log($scope.token);
            //tokenHandler.token = 'Bearer ' + $scope.token;
            //console.log(tokenHandler);
            $http.defaults.headers.common['Authorization'] = 'Bearer ' + $scope.token.token;
            $scope.roleUse = factory.authRoleCheck.get({role: 'user'});
            $scope.roleAdmin = factory.authRoleCheck.get({role: 'admin'});
            $scope.roleFoo = factory.authRoleCheck.get({role: 'foo'});
        };

        $scope.logout = function () {
            $scope.userName = '';
            $scope.token = null;
            $http.defaults.headers.common.Authorization = '';
        };

        $scope.loggedIn = function () {
            return $scope.token !== null;
        }
    });
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //PRODUCT CONTROLLER//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    purchase.controller('productController', function ($scope, $state, factory) {
        console.log("Enter Product controller");

        $scope.data = [
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
                    },
                    {
                        "id": 12,
                        "title": "node1.2",
                        "nodes": []
                    }
                ]
            },
            {
                "id": 2,
                "title": "node2",
                "nodes": [
                    {
                        "id": 21,
                        "title": "node2.1",
                        "nodes": []
                    },
                    {
                        "id": 22,
                        "title": "node2.2",
                        "nodes": []
                    }
                ]
            },
            {
                "id": 3,
                "title": "node3",
                "nodes": [
                    {
                        "id": 31,
                        "title": "node3.1",
                        "nodes": []
                    }
                ]
            },
            {
                "id": 4,
                "title": "node4",
                "nodes": [
                    {
                        "id": 41,
                        "title": "node4.1",
                        "nodes": []
                    }
                ]
            }
        ];

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

        $scope.toggle = function(scope) {
            scope.toggle();
        };

        $scope.moveLastToTheBegginig = function () {
            var a = $scope.data.pop();
            $scope.data.splice(0,0, a);
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

    });