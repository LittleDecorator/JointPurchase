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

        .directive('includeCallback', function () {
            return {
                restrict: 'A',
                scope: {
                    onInclude:'&includeCallback'
                },
                link: function (scope, el, attrs) {
                    scope.onInclude();
                }
            };
        })

        .directive('minItems', function minItems() {
            return {
                require: 'ngModel',
                restrict: 'A',
                link: function (scope, element, attr, ctrl) {
                    var minItems = 0;

                    attr.$observe('minItems', function (value) {
                        minItems = parseInt(value, 10) || 0;
                        ctrl.$validate();
                    });

                    ctrl.$validators['min-items'] = function (modelValue, viewValue) {
                        return viewValue.length >= minItems;
                    };

                }
            };
        })

        .directive('menuToggle', function () {
            return {
                scope: {
                    section: '='
                },
                templateUrl: 'pages/template/menu-toggle.tmpl.html',
                link: function (scope, element) {
                    var controller = element.parent().controller();

                    scope.isOpen = function () {
                        return controller.isOpen(scope.section);
                    };
                    scope.toggle = function () {
                        controller.toggleOpen(scope.section);
                    };

                    var parentNode = element[0].parentNode.parentNode.parentNode;
                    if (parentNode.classList.contains('parent-list-item')) {
                        var heading = parentNode.querySelector('h2');
                        element[0].firstChild.setAttribute('aria-describedby', heading.id);
                    }
                }
            }
        })

        .directive('menuLink', function () {
            return {
                scope: {
                    section: '='
                },
                templateUrl: 'pages/template/menu-link.tmpl.html',
                link: function ($scope, $element) {
                    var controller = $element.parent().controller();
                    $scope.focusSection = function () {
                        // set flag to be used later when
                        // $locationChangeSuccess calls openPage()
                        controller.autoFocusContent = true;
                    };
                    
                    $scope.showCard = function(){
                        controller.openSection($scope.section)
                    }
                }
            };
        })

        .directive("docsScrollClass", function () {
            return {
                restrict: "A", link: function (e, t, a) {
                    function n() {
                        var e = 0 !== o[0].scrollTop;
                        e !== l && t.toggleClass(a.docsScrollClass, e), l = e
                    }

                    var o = t.parent(), l = !1;
                    n(), o.on("scroll", n)
                }
            }
        })

        .directive('preventScroll', function () {
            return {
                restrict: 'A',
                link: function(scope, element){
                    element.bind('mousewheel DOMMouseScroll', function(event) {
                        var delta = event.originalEvent.wheelDelta;
                        if ((this.scrollTop + this.offsetHeight >= this.scrollHeight && delta<0) || (this.scrollTop == 0 && delta>0)){
                            event.preventDefault();
                        }
                    });
                }
            }
        })

        .directive('autoFocus', function($timeout) {
            return {
                restrict: 'AC',
                link: function(_scope, _element) {
                    $timeout(function(){
                        _element[0].focus();
                    }, 0);
                }
            };
        });

})();
