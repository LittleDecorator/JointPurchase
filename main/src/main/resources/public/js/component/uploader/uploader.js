/*
(function(){
    angular.module('uploader',[]);
})();

(function(){
    angular.module('uploader')
        .controller('uploaderController', ['$scope','eventService','uploadModal','$rootScope','FileUploader','dataResources', function ($scope,eventService,uploadModal,$rootScope,FileUploader,dataResources) {
            console.log("enter uploader controller");
            var uploader = $scope.uploader = new FileUploader();

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

            uploader.onAfterAddingAll = function (addedFileItems) {
                console.info('onAfterAddingAll', addedFileItems);
                $scope.toggleGallery();
            };
        }])
})();*/
