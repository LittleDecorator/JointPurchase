(function(){
    angular.module('purchase.validators',[]);
})();

(function() {
    'use strict';

    angular.module('purchase.validators')
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

        .directive('onlyNumber', function() {
            return {
                require: 'ngModel',
                link: function (scope, element, attr, ngModelCtrl) {

                    function fromUser(text) {
                        console.log(text);
                        if (text) {
                            console.log('text present');
                            var transformedInput = text.replace(/[^0-9]/g,'');
                            transformedInput = transformedInput.replace(/\s+/g,'');
                            console.log(transformedInput);
                            if (transformedInput !== text) {
                                ngModelCtrl.$setViewValue(transformedInput);
                                ngModelCtrl.$render();
                            }
                            return transformedInput;
                        }
                        console.log('no text');
                        return undefined;
                    }
                    ngModelCtrl.$parsers.unshift(fromUser);
                }
            };
        })

        .directive('currencyMask',[ '$filter', function($filter){
            return {
                require: 'ngModel',
                link: function (scope, element, attr, ngModelCtrl) {

                    function format(amount) {
                        if (amount) {
                            console.log('amount present');
                            var value = amount.replace(/[^0-9]/g,'');
                            value = $filter('number')(value);
                            if(amount !== value){
                                ngModelCtrl.$setViewValue(value);
                                ngModelCtrl.$render();
                            }
                            return value;
                        }
                        return undefined;
                    }

                    ngModelCtrl.$parsers.push(format);
                }
            };
        }])

})();