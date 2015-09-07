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
                            if(elem.isMain) {
                                mCou++;
                                return true;
                            } else {
                                return false;
                            }
                        });
                        $timeout(function(){
                            if(mCou==0){
                                $scope.images[0].isMain = true;
                                $scope.images[0].isShow = true;
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
                    dataResources.image.remove({id:id});
                };

                $scope.toggleMain = function($event,id){
                    //remove main class from all
                    $('.main').removeClass("main");
                    var currImage = helpers.findInArrayById($scope.images, id);
                    currImage.isMain = !currImage.isMain;
                    //update others
                    angular.forEach($scope.images,function(elem){
                        if(elem.id!==id){
                            elem.isMain = false;
                            console.log(angular.element(elem));
                        }
                    });
                    //check if any main present
                    if(!currImage.isMain){
                        $scope.images[0].isMain = true;
                        $scope.images[0].isShow = true;
                        $('.hiden-option:first > i:first').addClass("main")
                    } else {
                        currImage.isShow = true;
                        //$($event.currentTarget).addClass("main")
                    }
                };

                $scope.toggleShow = function($event,id){
                    console.log("toggle show");
                    var currImage = helpers.findInArrayById($scope.images, id);
                    console.log(currImage.isShow);
                    currImage.isShow = !currImage.isShow;
                    console.log(currImage.isShow);
                    if(currImage.isShow){
                        $($event.currentTarget).addClass("show")
                    } else {
                        $($event.currentTarget).removeClass("show")
                    }
                };

                /*$scope.toggleView = function () {
                    $scope.showImage = !$scope.showImage;
                };*/

                $scope.uploadFiles = function () {
                    var items = uploader.getNotUploadedItems();
                    var formData = new FormData();

                    angular.forEach(items, function (item, idx) {
                        formData.append("file" + idx, item._file);
                    });

                    formData.append("itemId", $stateParams.itemId);

                    dataResources.itemContent.upload(formData, function(data){
                        angular.forEach(data, function (image) {
                            //var path = $location.absUrl().substring(0,$location.absUrl().indexOf("#"));
                            //image.url = path + image.url;
                            $scope.images.push(image);
                            uploader.clearQueue();
                        });
                    });
                    //$scope.toggleGallery();
                };


                /*$scope.imageView = function (id) {
                 $scope.currentImage = dataResources.image.get({contentId: id});
                 $scope.toggleView();
                 };*/

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


            }]);
})();