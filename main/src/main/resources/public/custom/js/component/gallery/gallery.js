(function(){
    angular.module('gallery',[]);
})();

(function() {
    'use strict';

    angular.module('gallery')
        .controller('galleryController',['$scope','$location','$state','$stateParams','dataResources','FileUploader','$timeout',
            function($scope, $location, $state, $stateParams, dataResources, FileUploader, $timeout){
                console.log("Enter gallery controller");

                var uploader = $scope.uploader = new FileUploader();

                $scope.images = [];

                var mCou=0;

                /* init section */
                if ($stateParams.itemId) {
                    $scope.images = dataResources.itemImage.get({itemId: $stateParams.itemId});
                    $scope.images.$promise.then(function (result) {

                        $scope.images = result;
                        $scope.images.some(function(elem){
                            if(elem.main) {
                                mCou++;
                                return true;
                            } else {
                                return false;
                            }
                        });
                        $timeout(function(){
                            if($scope.images.length>0 && mCou==0){
                                $scope.images[0].main = true;
                                $scope.images[0].show = true;
                            }

                            $('.materialboxed').materialbox();
                        },100);
                    });
                } else {
                    $scope.images = dataResources.itemImage.get();
                }

                $scope.currentImage = {};

                //toggle modal image
                $scope.toggleGallery = function () {
                    //$scope.upload = uploadModal();
                    $scope.showUpload = !$scope.showUpload;
                    //show modal for upload
                    if($scope.showUpload){
                        $('#modal1').openModal();
                    } else {
                        $('#modal1').closeModal();
                    }
                };

                $scope.deleteImage = function(id){
                    var currImage = helpers.findInArrayById($scope.images, id);
                    var idx = $scope.images.indexOf(currImage);
                    $scope.images.splice(idx, 1);
                    //remove from DB
                    dataResources.image.remove({contentId:id,itemId:$stateParams.itemId});
                    //find new main if delete one
                    if(currImage.main && $scope.images.length>0){
                        currImage = $scope.images[0];
                        currImage.main = true;
                        currImage.show = true;
                        $('.hiden-option:first > i:first').addClass("main")
                        var itemContent = {contentId:currImage.id,main:currImage.main,show:currImage.show,itemId:$stateParams.itemId};
                        dataResources.galleryMain.toggle(itemContent);
                    }
                };

                $scope.toggleMain = function(id){
                    //remove main class from all
                    $('.main').removeClass("main");
                    var currImage = helpers.findInArrayById($scope.images, id);
                    currImage.main = !currImage.main;
                    //update others
                    angular.forEach($scope.images,function(elem){
                        if(elem.id!==id){
                            elem.main = false;
                        }
                    });
                    //check if any main present
                    if(!currImage.main){
                        currImage = $scope.images[0];
                        currImage.main = true;
                        currImage.show = true;
                        $('.hiden-option:first > i:first').addClass("main")
                    } else {
                        currImage.show = true;
                    }
                    console.log(currImage);
                    var itemContent = {contentId:currImage.id,main:currImage.main,show:currImage.show,itemId:$stateParams.itemId};
                    console.log(itemContent);
                    dataResources.galleryMain.toggle(itemContent);
                };

                $scope.toggleShow = function($event,id){
                    var currImage = helpers.findInArrayById($scope.images, id);
                    currImage.show = !currImage.show;
                    if(currImage.show){
                        $($event.currentTarget).addClass("show")
                    } else {
                        $($event.currentTarget).removeClass("show")
                    }
                    console.log(currImage);
                    var itemContent = {contentId:currImage.id,show:currImage.show,itemId:$stateParams.itemId};
                    console.log(itemContent);
                    dataResources.galleryShow.toggle(itemContent);
                };

                //загрузка файлов
                $scope.uploadFiles = function () {
                    var items = uploader.getNotUploadedItems();
                    var formData = new FormData();

                    angular.forEach(items, function (item, idx) {
                        formData.append("file" + idx, item._file);
                    });

                    formData.append("itemId", $stateParams.itemId);

                    dataResources.itemContent.upload(formData, function(data){
                        angular.forEach(data, function (image) {
                            $scope.images.push(image);
                            uploader.clearQueue();
                        });
                    });
                };


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
            }]);
})();