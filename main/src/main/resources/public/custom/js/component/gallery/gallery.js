(function(){
    angular.module('gallery',[]);
})();

(function() {
    'use strict';

    angular.module('gallery')
        .controller('galleryController',['$scope','$location','$state','$stateParams','dataResources','FileUploader',
            function($scope, $location, $state, $stateParams, dataResources, FileUploader, uploadModal){
            console.log("Enter gallery controller");

            var uploader = $scope.uploader = new FileUploader();

            $scope.images = [];

            //$scope.files = [];

            /* init section */
            if ($stateParams.itemId) {
                $scope.images = dataResources.itemImage.get({itemId: $stateParams.itemId});
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

                dataResources.itemContent.upload(formData, function(data){
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
                $scope.currentImage = dataResources.image.get({contentId: id});
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

        }]);
})();