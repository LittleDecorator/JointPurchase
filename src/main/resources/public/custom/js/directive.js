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

/* build main navigation menu */
/*purchase.directive('ngMenu',function(){
    return {
        restrict: 'E',
        templateUrl:'pages/template/ng-menu.html',
        compile: function(templateElement, templateAttrs) {
            return{
                post:function ($scope, element, attrs, controller){
                    console.log($scope);
                    console.log(attrs);
                }
            }
        }

    }
});*/

/*purchase.directive('ngDropMenu',function($compile){
    return {
        restrict: 'A',
        compile: function(templateElement, templateAttrs) {
            return{
                post:function ($scope, element, attrs, controller){
                    console.log($scope);
                    *//*var item = $scope.$parent.item;
                    console.log(item)
                    var tpl = '<a class="dropdown-toggle" data-toggle="dropdown" style="cursor: pointer">'+item.title+'<b class="caret"></b></a>' +
                              '<ul class="dropdown-menu">';

                    angular.forEach(item.menu,function(elem){
                        if(elem.action){
                            tpl = tpl + '<li><a ng-click="'+elem.action+'" style="cursor: pointer">'+elem.title+'</a></li>';
                        } else if(elem.url){
                            tpl = tpl + '<li><a ui-sref="'+elem.url+'" style="cursor: pointer">'+elem.title+'</a></li>';
                        }
                    });
                    element.append(tpl);
                    $compile(element.contents())($scope);*//*
                }
            }
        }
    }
});*/

purchase.directive('ngMenu',function($compile){
    function fillMenu (data){
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

    return function($scope, element, attrs) {
        console.log($scope);
        /*Задаем функцию, которая будет вызываться при изменении переменной menu*/
        $scope.$watch(attrs.ngMenu,function(value){
            var tpl = fillMenu(value);
            element.empty();
            element.append(tpl);
            $compile(element.contents())($scope);
        });
    };

});