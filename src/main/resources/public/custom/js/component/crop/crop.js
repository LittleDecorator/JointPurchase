(function(){
	angular.module('crop',[]);
})();

(function() {
	'use strict';

	angular.module('crop')

			.controller('cropController',['$scope','dataResources','$timeout','$stateParams', function($scope,dataResources, $timeout, $stateParams) {

				$scope.dataUrl = 'media/image/'+ $stateParams.imageId;

				var image;
				var options;
				var cropper;
				var originalImageURL;
				// var uploadedImageURL;

				$scope.loadCallback= function(){
					initCropper();
					$timeout(function(){
						cropper.destroy();
						cropper = new Cropper(image, options);
					},1);
				};

				$scope.uploadCropped = function () {
					cropper.getCroppedCanvas().toBlob(function (blob) {
						var formData = new FormData();

						formData.append('croppedImage', blob);

						// Use `jQuery.ajax` method
						// $.ajax('/path/to/upload', {
						// 	method: "POST",
						// 	data: formData,
						// 	processData: false,
						// 	contentType: false,
						// 	success: function () {
						// 		console.log('Upload success');
						// 	},
						// 	error: function () {
						// 		console.log('Upload error');
						// 	}
						// });
					});
				};

				var rebuild = function(event){
					console.log(event);
					var e = event || window.event;
					var target = e.target || e.srcElement;
					var cropBoxData;
					var canvasData;
					var isCheckbox;
					var isRadio;

					if (!cropper) {
						return;
					}

					if (target.tagName.toLowerCase() === 'label') {
						target = target.querySelector('input');
					}

					isCheckbox = target.type === 'checkbox';
					isRadio = target.type === 'radio';

					if (isCheckbox || isRadio) {
						if (isCheckbox) {
							options[target.name] = target.checked;
							cropBoxData = cropper.getCropBoxData();
							canvasData = cropper.getCanvasData();

							options.ready = function () {
								console.log('ready');
								cropper.setCropBoxData(cropBoxData).setCanvasData(canvasData);
							};
						} else {
							options[target.name] = target.value;
							options.ready = function () {
								console.log('ready');
							};
						}

						// Restart
						cropper.destroy();
						cropper = new Cropper(image, options);
					}
				};

				var initCropper = function(){
					console.log(1);
					var Cropper = window.Cropper;
					var container = document.querySelector('.img-container');
					image = container.getElementsByTagName('img').item(0);
					var download = document.getElementById('download');
					var actions = document.getElementById('actions');
					var dataX = document.getElementById('dataX');
					var dataY = document.getElementById('dataY');
					var dataHeight = document.getElementById('dataHeight');
					var dataWidth = document.getElementById('dataWidth');
					var dataRotate = document.getElementById('dataRotate');
					var dataScaleX = document.getElementById('dataScaleX');
					var dataScaleY = document.getElementById('dataScaleY');
					options = {
						aspectRatio: 16 / 9,
						preview: '.img-preview',
						ready: function (e) {
							console.log(e.type);
						},
						cropstart: function (e) {
							console.log(e.type, e.detail.action);
						},
						cropmove: function (e) {
							console.log(e.type, e.detail.action);
						},
						cropend: function (e) {
							console.log(e.type, e.detail.action);
						},
						crop: function (e) {
							var data = e.detail;
							console.log(e.type);
							dataX.value = Math.round(data.x);
							dataY.value = Math.round(data.y);
							dataHeight.value = Math.round(data.height);
							dataWidth.value = Math.round(data.width);
							dataRotate.value = typeof data.rotate !== 'undefined' ? data.rotate : '';
							dataScaleX.value = typeof data.scaleX !== 'undefined' ? data.scaleX : '';
							dataScaleY.value = typeof data.scaleY !== 'undefined' ? data.scaleY : '';
						},
						zoom: function (e) {
							console.log(e.type, e.detail.ratio);
						}
					};

					originalImageURL = image.src;
					cropper = new Cropper(image, options);

					actions.querySelector('.docs-toggles').onchange = function (event) {
						rebuild(event)
					};
				};

			}])
})();