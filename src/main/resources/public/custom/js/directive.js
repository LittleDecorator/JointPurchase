(function(){
    angular.module('purchase.directives',[]);
})();

(function(){
    'use strict';

    angular.module('purchase.directives')

        .directive('ngThumb', ['$window', function($window) {
            var helper = {
                support: !!($window.FileReader && $window.CanvasRenderingContext2D),
                isFile: function(item) {
                    return angular.isObject(item) && item instanceof $window.File;
                },
                isImage: function(file) {
                    var type =  '|' + file.type.slice(file.type.lastIndexOf('/') + 1) + '|';
                    return '|jpg|png|jpeg|bmp|gif|'.indexOf(type) !== -1;
                }
            };

            return {
                restrict: 'A',
                //template: '<canvas/>',
                link: function(scope, element, attributes) {
                    if (!helper.support) return;

                    var params = scope.$eval(attributes.ngThumb);

                    if (!helper.isFile(params.file)) return;
                    if (!helper.isImage(params.file)) return;

                    var canvas = element.find('canvas');
                    console.log(canvas);
                    var reader = new FileReader();

                    reader.onload = onLoadFile;
                    reader.readAsDataURL(params.file);

                    function onLoadFile(event) {
                        var img = new Image();
                        img.onload = onLoadImage;
                        img.src = event.target.result;
                    }

                    function onLoadImage() {
                        console.log(this.width);
                        console.log(this.height);
                        var width = this.width;
                        var height = this.height;
                        if(params.width && params.height){
                            console.log('with params!');
                            width = params.width || this.width / this.height * params.height;
                            height = params.height || this.height / this.width * params.width;
                        }
                        canvas.attr({ width: width, height: height });
                        canvas[0].getContext('2d').drawImage(this, 0, 0, width, height);
                    }
                }
            };
        }])

        .directive('imgTitle',function(){
            return{
                restrict: 'E',
                templateUrl:'pages/template/imageTitle.html',
                link: function (scope, el, attrs) {
                    el.replaceWith(el.children());
                }
            }
        })

        // .directive('syncFocusWith', function($timeout, $rootScope) {
        //     return {
        //         restrict: 'A',
        //         scope: {
        //             focusValue: "=syncFocusWith"
        //         },
        //         link: function($scope, $element, attrs) {
        //             $scope.$watch("focusValue", function(currentValue, previousValue) {
        //                 if (currentValue === true && !previousValue) {
        //                     $element[0].focus();
        //                 } else if (currentValue === false && previousValue) {
        //                     $element[0].blur();
        //                 }
        //             })
        //         }
        //     }
        // })

        // .directive('itemSwipe', ['$swipe', '$document', '$window', '$timeout',
        //     function($swipe, $document, $window, $timeout) {
        //         return {
        //             transclude: true,
        //             template: '<div class="item-swipe-wrapper" style="position: relative"><div class="swiper" ng-style="swiperStyle" ng-transclude style="position: relative"></div></div>',
        //             link: {
        //                 post: function postLink(scope, iElement, iAttrs, controller) {
        //                     var startCoords, threeD, $swiper;
        //                     $swiper = angular.element('.swiper', iElement);
        //                     scope.proceed = false;
        //                     scope.undoStyle = {
        //                         opacity: 0,
        //                         width: '100%',
        //                         position: 'absolute',
        //                         left: '70%',
        //                         top: '0',
        //                     };
        //
        //                     function fullSwipe(coords){
        //                         return coords.x - startCoords.x > $swiper.width()*(1/3) ? true : false;
        //                     }
        //
        //                     function cssPrefix(property, value){
        //                         var vendors = ['', '-o-','-moz-','-ms-','-khtml-','-webkit-'];
        //                         var styles = {};
        //                         for (var i = vendors.length - 1; i >= 0; i--) {
        //                             styles[vendors[i] + property] = value;
        //                         }
        //                         return styles;
        //                     }
        //
        //                     function updateElementPosition(pos){
        //                         if('threeD' in iAttrs){
        //                             $swiper.css(cssPrefix('transform', 'translate(' + pos + 'px)'));
        //                         }else{
        //                             $swiper.css('left', pos);
        //                         }
        //                     }
        //
        //                     scope.$watch('proceed', function(val){
        //                         if(val){
        //                             scope.undoStyle.opacity = 1;
        //                             scope.eliminateItem = $timeout(function() {
        //                                 scope.proceed = false;
        //                                 return scope.$eval(iAttrs.onRemove);
        //                             }, 1220);
        //                         }else{
        //                             scope.undoStyle.opacity = 0;
        //                             $timeout.cancel(scope.eliminateItem);
        //                             updateElementPosition(0);
        //                         }
        //                     });
        //
        //                     $swipe.bind($swiper, {
        //                         'start': function(coords) {
        //                             startCoords = coords;
        //                             scope.swiperStyle = {opacity: 0.5};
        //                             scope.$apply();
        //                         },
        //                         'cancel': function() {
        //                             scope.swiperStyle = cssPrefix('transition', 'all 0.2s ease-in-out');
        //                             scope.swiperStyle.opacity = 1;
        //                             scope.$apply();
        //                         },
        //                         'move': function(coords) {
        //                             updateElementPosition(coords.x - startCoords.x);
        //                         },
        //                         'end': function(endCoords) {
        //                             if (fullSwipe(endCoords)) {
        //                                 scope.proceed = true;
        //                                 updateElementPosition($document.width());
        //                             }else {
        //                                 scope.proceed = false;
        //                                 updateElementPosition(0);
        //                             }
        //                             scope.swiperStyle = cssPrefix('transition', 'all 0.2s ease-in-out');
        //                             scope.swiperStyle.opacity = 1;
        //                             scope.$apply();
        //                         }
        //                     });
        //                 }
        //             }
        //         };
        //     }])
})();
