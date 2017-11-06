(function(){
    angular.module('gallery',[]);
})();

(function() {
    'use strict';

    angular.module('gallery')
        /* Контроллер галереи */
        .controller('galleryController',['$scope','$location','$state','$stateParams','dataResources','$timeout','fileUploadModal','FileUploader',
            function($scope, $location, $state, $stateParams, dataResources, $timeout, fileUploadModal,FileUploader){

                // console.log($stateParams)
                // console.log($location)
                var uploader = $scope.uploader = new FileUploader();
                var mCou=0;

                var mvm = $scope.$parent.mvm;
                var vm = this;

                vm.upload = upload;
                vm.add = add;
                vm.init = init;
                vm.deleteImage = deleteImage;
                vm.toggleMain = toggleMain;
                vm.toggleShow = toggleShow;

                vm.images = [];
                vm.currentImage = {};

                vm.isCompany = $location.$$path.indexOf('company') !== -1;
                vm.isItem = $location.$$path.indexOf('item') !== -1;
                vm.isCategory = $location.$$path.indexOf('category') !== -1;
                vm.onlyDelete = !vm.isItem;

                /**
                 * загрузка файлов
                 */
                function upload() {
                    //var items = $scope.uploader.getNotUploadedItems();
                    var items = uploader.getNotUploadedItems();
                    var formData = new FormData();

                    angular.forEach(items, function (item, idx) {
                        formData.append("file" + idx, item._file);
                    });

                    if(vm.isItem){
                        //formData.append("itemId", resolved.itemId);
                        formData.append("itemId", $stateParams.id);

                        dataResources.itemContent.upload(formData, function(data){
                            angular.forEach(data, function (image) {
                            vm.images.push(image);
                            uploader.clearQueue();
                            });
                        });
                    }
                    if(vm.isCompany){
                        formData.append("companyId", $stateParams.id);

                        dataResources.companyContent.upload(formData, function(data){
                            console.log(data)
                            angular.forEach(data, function (image) {
                                // vm.images.push(image);
                                uploader.clearQueue();
                            });
                        });
                    }

                }

                /**
                 * proxy добаление
                 */
                function add(){
                    if(vm.images.length > 0 && !vm.isItem){
                      return;
                    }
                    $('#uploadBtn').click();
                }

                /**
                 * init section
                 */
                function init(){
                    var imageResource, param;
                    if(vm.isCompany){
                        imageResource = dataResources.companyImage;
                        param = {id:$stateParams.id}
                    } else if(vm.isItem){
                        imageResource = dataResources.itemImage
                        param = {itemId:$stateParams.id}
                    } else {
                        console.log("Add here category resource")
                    }

                    if ($stateParams.id) {
                        imageResource.get(param).$promise.then(function (result) {
                            if(helpers.isArray(result)){
                                vm.images = result;
                            } else {
                                if(helpers.isPropertyNotEmpty(result,"contentId")){
                                  vm.images.push(result);
                                } else {
                                    return;
                                }
                            }
                            vm.images.some(function(elem){
                                if(elem.main) {
                                    mCou++;
                                    return true;
                                } else {
                                    return false;
                                }
                            });

                            $timeout(function(){
                                if(vm.images.length > 0 && mCou===0){
                                    vm.images[0].main = true;
                                    vm.images[0].show = true;
                                }

                                // настройки слайдера
                                var options = {
                                    selector: '.item',
                                    getCaptionFromTitleOrAlt: false
                                };

                                if(mvm.width < 601){
                                    options.controls=false;
                                    options.thumbnail=false;
                                }

                                // инициализируем галлерею
                                $(".wrap").lightGallery(options);
                            },100);
                        });
                    } else {
                        console.log("get")
                        vm.images = imageResource.get();
                    }
                }

                function deleteImage(image){
                    if(vm.isItem){
                        var idx = vm.images.indexOf(image);
                        vm.images.splice(idx, 1);
                        //remove from DB
                        dataResources.image.remove({id:image.id});
                        //find new main if delete one
                        if(image.main && vm.images.length>0){
                            image = vm.images[0];
                            image.main = true;
                            image.show = true;
                            $('.hiden-option:first > i:first').addClass("main")
                            var itemContent = {contentId:image.id,main:image.main,show:image.show,itemId:$stateParams.id};
                            dataResources.galleryMain.toggle(itemContent);
                        }
                    } else {
                        vm.images = [];
                        if(vm.isCompany){
                            dataResources.companyImage.remove({id:$stateParams.id, contentId: image.contentId});
                        }
                    }

                }

                function toggleMain(id){
                    //remove main class from all
                    $('.main').removeClass("main");
                    var currImage = helpers.findInArrayById(vm.images, id);
                    currImage.main = !currImage.main;
                    //update others
                    angular.forEach(vm.images,function(elem){
                        if(elem.id!==id){
                            elem.main = false;
                        }
                    });
                    //check if any main present
                    if(!currImage.main){
                        currImage = vm.images[0];
                        currImage.main = true;
                        currImage.show = true;
                        $('.hiden-option:first > i:first').addClass("main")
                    } else {
                        currImage.show = true;
                    }
                    var itemContent = {contentId:currImage.contentId,main:currImage.main,show:currImage.show,itemId:$stateParams.id};
                    dataResources.galleryMain.toggle(itemContent);
                }

                function toggleShow($event,id){
                    var currImage = helpers.findInArrayById(vm.images, id);
                    currImage.show = !currImage.show;
                    if(currImage.show){
                        $($event.currentTarget).addClass("show")
                    } else {
                        $($event.currentTarget).removeClass("show")
                    }
                    var itemContent = {contentId:currImage.id,show:currImage.show,itemId:$stateParams.id};
                    dataResources.galleryShow.toggle(itemContent);
                }

                /**
                 * callback on file select
                 */
                uploader.onAfterAddingAll = function () {
                    upload();
                };

                // FILTERS
                uploader.filters.push({
                    name: 'imageFilter',
                    fn: function(item /*{File|FileLikeObject}*/, options) {
                        var type = '|' + item.type.slice(item.type.lastIndexOf('/') + 1) + '|';
                        return '|webp|jpg|png|jpeg|bmp|gif|'.indexOf(type) !== -1;
                    }
                });

                init();

            }])
})();