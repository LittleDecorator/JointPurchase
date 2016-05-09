(function(){
    angular.module('gallery',[]);
})();

(function() {
    'use strict';

    angular.module('gallery')
        .controller('galleryController',['$scope','$location','$state','$stateParams','dataResources','$timeout','fileUploadModal','FileUploader',
            function($scope, $location, $state, $stateParams, dataResources, $timeout, fileUploadModal,FileUploader){
                console.log("Enter gallery controller");

                var uploader = $scope.uploader = new FileUploader();
                var dialog;
                $scope.images = [];

                /* callback on file select */
                uploader.onAfterAddingAll = function (addedFileItems) {
                    if(!dialog){
                        dialog = fileUploadModal({itemId:$stateParams.id},$scope);
                        dialog.closePromise.then(function(output) {
                            uploader.clearQueue();
                            dialog = null;
                            return true;
                        });
                    }
                };

                $scope.add = function(){
                    $('#uploadBtn').click();
                };

                /* init section */
                // if ($stateParams.id) {
                //     $scope.images = dataResources.itemImage.get({itemId: $stateParams.id});
                //     $scope.images.$promise.then(function (result) {
                //
                //         $scope.images = result;
                //         $scope.images.some(function(elem){
                //             if(elem.main) {
                //                 mCou++;
                //                 return true;
                //             } else {
                //                 return false;
                //             }
                //         });
                //         $timeout(function(){
                //             if($scope.images.length>0 && mCou==0){
                //                 $scope.images[0].main = true;
                //                 $scope.images[0].show = true;
                //             }
                //
                //             $('.materialboxed').materialbox();
                //         },100);
                //     });
                // } else {
                //     $scope.images = dataResources.itemImage.get();
                // }

                // $scope.currentImage = {};

                $scope.deleteImage = function(id){
                    var currImage = helpers.findInArrayById($scope.images, id);
                    var idx = $scope.images.indexOf(currImage);
                    $scope.images.splice(idx, 1);
                    //remove from DB
                    dataResources.image.remove({contentId:id,itemId:$stateParams.id});
                    //find new main if delete one
                    if(currImage.main && $scope.images.length>0){
                        currImage = $scope.images[0];
                        currImage.main = true;
                        currImage.show = true;
                        $('.hiden-option:first > i:first').addClass("main")
                        var itemContent = {contentId:currImage.id,main:currImage.main,show:currImage.show,itemId:$stateParams.id};
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
                    var itemContent = {contentId:currImage.id,main:currImage.main,show:currImage.show,itemId:$stateParams.id};
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
                    var itemContent = {contentId:currImage.id,show:currImage.show,itemId:$stateParams.id};
                    console.log(itemContent);
                    dataResources.galleryShow.toggle(itemContent);
                };

                // FILTERS
                uploader.filters.push({
                    name: 'imageFilter',
                    fn: function(item /*{File|FileLikeObject}*/, options) {
                        var type = '|' + item.type.slice(item.type.lastIndexOf('/') + 1) + '|';
                        return '|jpg|png|jpeg|bmp|gif|'.indexOf(type) !== -1;
                    }
                });


            }])
        .controller('uploadController',['$scope','dataResources','resolved','$timeout',function($scope, dataResources, resolved, $timeout){

            $scope.uploader = $scope.$parent.uploader;

            //загрузка файлов
            $scope.upload = function () {
                var items = $scope.uploader.getNotUploadedItems();
                var formData = new FormData();

                angular.forEach(items, function (item, idx) {
                    formData.append("file" + idx, item._file);
                });

                formData.append("itemId", resolved.itemId);

                dataResources.itemContent.upload(formData, function(data){
                    angular.forEach(data, function (image) {
                        $scope.images.push(image);
                        $scope.uploader.clearQueue();
                        $scope.closeThisDialog();
                    });
                });
            };

            /* удаление из очереди загрузки */
            $scope.removeFromQueue = function(item){
                if(item){
                    item.remove();
                }
                if($scope.uploader.queue.length == 0){
                    $scope.closeThisDialog();
                }
            };

            /* добавление файлов в очередь загрузки */
            $scope.add = function(){
                $('#uploadBtn').click();
            };

            /* изменение ширины модального окна */
            $timeout(function(){
                $('.ngdialog-content').css('width','50%');
            },10);


        }])
})();