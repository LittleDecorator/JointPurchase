purchase.directive('companyDetail',function(){
    return {
        restrict: 'EA', //E = element, A = attribute, C = class, M = comment
        templateUrl: 'pages/company_details.html'
    }
});

purchase.directive('modal', function () {
    return {
        templateUrl: 'pages/template/modal.html',
        restrict: 'E',
        transclude: true,
        replace: true,
        scope: true,
        link: function postLink(scope, element, attrs) {

            scope.title = attrs.title;

            scope.$watch(attrs.visible, function (value) {
                if (value == true)
                    $(element).modal('show');
                else
                    $(element).modal('hide');
            });

            scope.$watch(attrs.hideFooter, function (value) {
                if (value == true)
                    $(element).find('.modal-footer').hide();
                else
                    $(element).find('.modal-footer').show();
            });

            $(element).on('shown.bs.modal', function () {
                scope.$apply(function () {
                    scope.$parent[attrs.visible] = true;
                });
            });

            $(element).on('hidden.bs.modal', function () {
                scope.$apply(function () {
                    scope.$parent[attrs.visible] = false;
                });
            });
        }
    };
});

purchase.directive('ngThumb', ['$window', function($window) {
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
}]);

purchase.directive('backImg', function () {
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
});

purchase.directive('ngMenu',function($compile){
    function fillMenu (data){
        console.log(data);
        var tpl="";
        angular.forEach(data,function(item){
            if(item.action){
                tpl = tpl + '<li><a ng-click="'+item.action+'" style="cursor: pointer">'+item.title+'</a></li>';
            } else if(item.url){
                tpl = tpl + '<li><a ui-sref="'+item.url+'" style="cursor: pointer">'+item.title+'</a></li>';
            } else if(item.menu){
                tpl = tpl + '<li class="dropdown" ><a class="dropdown-toggle" data-toggle="dropdown" style="cursor: pointer">'+item.title+'<b class="caret"></b></a><ul class="dropdown-menu">';
                tpl = tpl + fillMenu(item.menu);
            } else {
                tpl = tpl + '<li><a style="cursor: pointer">'+item.title+'</a></li>';
            }
        });
        return tpl;
    }

    return {
        restrict: 'E',
        require: ['ngModel'],
        scope: true,
        link: function(scope, element, attrs, controllersArr) {
            console.log(scope);
            console.log(controllersArr);
            var ngModel = controllersArr[0];

            if(ngModel) {
                //вызовится при изменении $modelValue
                ngModel.$render = function() {
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
    /*return function($scope, element, attrs) {
        /!*Задаем функцию, которая будет вызываться при изменении переменной menu*!/
        $scope.$watch(attrs.ngMenu,function(value){
            var tpl = fillMenu(value);
            element.empty();
            element.append(tpl);
            $compile(element.contents())($scope);
        });
    };*/

});

/* директива переопределяет стандартную выборку */
purchase.directive('select', function($timeout){
    return {
            restrict: 'E',
            require: '?ngModel',
            link: function (scope, element, attrs, ngModel) {

                var post = function(selector){
                    angular.element($('#'+selector+' .select-wrapper')).addClass('inactive');
                    angular.element($('#'+selector+' ul li:first span')).addClass('inactive');
                }

                /* сделан упор на callback пустой модели и условие не пустого списка в элементе */
                if(ngModel) {
                    ngModel.$isEmpty = function(){
                        if(element[0].length > 1){
                            element.material_select();
                            post('company');
                            post('category');
                        }
                    }
                }
        }
    };
});

purchase.directive("menu", function() {
    return {
        restrict: "E",
        template: "<div ng-class='{ show: visible, left: alignment === \"left\", right: alignment === \"right\" }' ng-transclude></div>",
        transclude: true,
        scope: {
            visible: "=",
            alignment: "@"
        }
    };
});

purchase.directive("menuItem", function() {
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
});
