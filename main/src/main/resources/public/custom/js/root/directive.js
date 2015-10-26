(function(){
    angular.module('purchase.directives',[]);
})();

(function(){
    'use strict';

    angular.module('purchase.directives')
        .directive('companyDetail',function(){
            return {
                restrict: 'EA', //E = element, A = attribute, C = class, M = comment
                templateUrl: 'pages/company_details.html'
            }
        })

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
                template: '<canvas/>',
                link: function(scope, element, attributes) {
                    if (!helper.support) return;
                    var params = scope.$eval(attributes.ngThumb);

                    if (!helper.isFile(params.file)) return;
                    if (!helper.isImage(params.file)) return;

                    var canvas = element.find('canvas');
                    var reader = new FileReader();

                    reader.onload = onLoadFile;
                    reader.readAsDataURL(params.file);

                    function onLoadFile(event) {
                        var img = new Image();
                        img.onload = onLoadImage;
                        img.src = event.target.result;
                    }

                    function onLoadImage() {
                        var width = params.width || this.width / this.height * params.height;
                        var height = params.height || this.height / this.width * params.width;
                        canvas.attr({ width: width, height: height });
                        canvas[0].getContext('2d').drawImage(this, 0, 0, width, height);
                    }
                }
            };
        }])

        .directive('backImg', function () {
        return {
            restrict: 'A',
            link: function(scope, element, attrs){
                //element имеет доступ к текущему объекту через scope
                console.log(scope);
                console.log(element);
                console.log(attrs);
                element.css({"background-image": "url(" + attrs.backImg +")"});
            }
        };
    })

        .directive('ngMenu',function($compile) {
            function fillMenu(data) {
                console.log(data);
                var tpl = "";
                angular.forEach(data, function (item) {
                    if (item.action) {
                        tpl = tpl + '<li><a ng-click="' + item.action + '" style="cursor: pointer">' + item.title + '</a></li>';
                    } else if (item.url) {
                        tpl = tpl + '<li><a ui-sref="' + item.url + '" style="cursor: pointer">' + item.title + '</a></li>';
                    } else if (item.menu) {
                        tpl = tpl + '<li class="dropdown" ><a class="dropdown-toggle" data-toggle="dropdown" style="cursor: pointer">' + item.title + '<b class="caret"></b></a><ul class="dropdown-menu">';
                        tpl = tpl + fillMenu(item.menu);
                    } else {
                        tpl = tpl + '<li><a style="cursor: pointer">' + item.title + '</a></li>';
                    }
                });
                return tpl;
            }

            return {
                restrict: 'E',
                require: ['ngModel'],
                scope: true,
                link: function (scope, element, attrs, controllersArr) {
                    console.log(scope);
                    console.log(controllersArr);
                    var ngModel = controllersArr[0];

                    if (ngModel) {
                        //вызовится при изменении $modelValue
                        ngModel.$render = function () {
                            if (!ngModel.$modelValue || !angular.isArray(ngModel.$modelValue)) {
                                scope.$modelValue = [];
                            }
                            scope.$modelValue = ngModel.$modelValue;
                            var tpl = fillMenu(scope.$modelValue);
                            element.empty();
                            element.append(tpl);
                            $compile(element.contents())(scope);
                        };
                    }
                }
            }
        })

        .directive('select', function(){
            return {
                restrict: 'E',
                require: '?ngModel',
                link: function (scope, element, attrs, ngModel) {

                    var post = function(selector){
                        angular.element($('#'+selector+' .select-wrapper')).addClass('inactive');
                        angular.element($('#'+selector+' ul li:first span')).addClass('inactive');
                    };

                    /* сделан упор на callback пустой модели и условие не пустого списка в элементе */
                    if(ngModel) {
                        ngModel.$isEmpty = function(){
                            if(element[0].length > 1  && !ngModel.$pristine){
                                element.material_select();
                                post('company');
                                post('category');
                                if(attrs.postInit){
                                    scope[attrs.postInit]();        //вызов метода через имя указанное в атрибуте post-init
                                }
                            }
                        }
                    }
                }
            };
        })

        .directive("menu", function() {
            return {
                restrict: "E",
                template: "<div ng-class='{ show: visible, left: alignment === \"left\", right: alignment === \"right\" }' ng-transclude></div>",
                transclude: true,
                scope: {
                    visible: "=",
                    alignment: "@"
                }
            };
        })

        .directive("menuItem", function() {
            return {
                restrict: "E",
                template: "<div ng-click='navigate()' ng-transclude></div>",
                transclude: true,
                scope: {
                    hash: "@"
                },
                link: function($scope) {
                    $scope.navigate = function() {
                        window.location.hash = $scope.hash;
                    }
                }
            }
        })

        .directive('passwordValidate', function() {
            return {
                restrict:'A',
                require: 'ngModel',
                link: function (scope, elm, attrs, ctrl) {
                    ctrl.$parsers.push(function(value) {

                        if(!scope.$validation){
                            scope.$validation = {};
                        }

                        var valid = true;
                        var errText = null;

                        if (/[A-Z]/.test(value)) {
                            ctrl.$setValidity('uppercaseValidator', true);
                        } else {
                            ctrl.$setValidity('uppercaseValidator', false);
                            valid = false;
                            errText = "Strong secret has to contain at least 1 uppercase";
                        }

                        if (/[0-9]/.test(value)) {
                            ctrl.$setValidity('numberValidator', true);
                        } else {
                            ctrl.$setValidity('numberValidator', false);
                            valid = false;
                            errText = "Strong secret has to contain at least 1 number";
                        }

                        if (value.length === 6) {
                            ctrl.$setValidity('sixCharactersValidator', true);
                        } else {
                            ctrl.$setValidity('sixCharactersValidator', false);
                            valid = false;
                            errText = "Strong secret has to be exactly 6 characters long";
                        }

                        scope.$validation[attrs.name] = {valid:valid,errorText:errText};
                        console.log(scope);

                        return value;

                    });
                }
            };
        })

        .directive('emailValidate', function() {
        return {
            restrict:'A',
            require: 'ngModel',
            link: function (scope, elm, attrs, ctrl) {
                console.log(ctrl);
                ctrl.$parsers.push(function(value) {
                    var re = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
                    if (re.test(value)) {
                        ctrl.$setValidity('emailValidator', true);
                    } else {
                        ctrl.$setValidity('emailValidator', false);

                    }
                    console.log(scope);

                    return value;

                });
            }
        };
    })

        //TODO: phone validation and string limit
        .directive('phoneValidate', function() {
            return {
                restrict:'A',
                require: 'ngModel',
                link: function (scope, elm, attrs, ctrl) {
                    ctrl.$parsers.push(function(value) {
                        var re = /^(\+\d{1,2})?\(?\d{3}\)?[\s.-]?\d{3}[\s.-]?\d{4}$/i;
                        if (re.test(value)) {
                            ctrl.$setValidity('phoneValidator', true);
                        } else {
                            ctrl.$setValidity('phoneValidator', false);

                        }
                        console.log(scope);

                        return value;

                    });
                }
            };
        })

        .directive('emptyValidate', function() {
            return {
                restrict:'A',
                require: 'ngModel',
                link: function (scope, elm, attrs, ctrl) {
                    ctrl.$parsers.push(function(value) {
                        console.log(value);
                        console.log(typeof value);
                        if (value){
                            if(typeof value == 'object' && !helpers.isEmpty(value)) {
                                ctrl.$setValidity('emptyValidator', true);
                            } else if(value.length == 0){
                                ctrl.$setValidity('emptyValidator', true);
                            } else {
                                ctrl.$setValidity('emptyValidator', false);
                            }
                        } else {
                            ctrl.$setValidity('emptyValidator', false);

                        }
                        console.log(scope);

                        return value;

                    });
                }
            };
        })

        .directive('ngSide',function(){
            return {
                restrict: 'E',
                require: ['ngModel'],
                scope: true,
                //controller: 'sideController',
                link: function(scope, element, attrs, controllersArr) {
                    var ngModel = controllersArr[0];


                    if(ngModel) {
                        //вызовится при изменении $modelValue
                        ngModel.$render = function() {
                            console.log(ngModel);
                            if (!ngModel.$modelValue || !angular.isArray(ngModel.$modelValue)) {
                                scope.$modelValue = [];
                            }
                            scope.$modelValue = ngModel.$modelValue;
                        };
                    }
                }
            };
        })
})();
