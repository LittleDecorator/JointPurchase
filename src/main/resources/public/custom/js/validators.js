(function(){
    angular.module('purchase.validators',[]);
})();

(function() {
    'use strict';

    angular.module('purchase.validators')

        .directive('passwordValidate', function() {
            return {
                restrict:'A',
                require: 'ngModel',
                link: function (scope, elm, attrs, ctrl) {
                    ctrl.$parsers.push(function(value) {

                        ctrl.$setValidity('uppercaseValidator',/[A-Z]/.test(value));
                        ctrl.$setValidity('numberValidator', /[0-9]/.test(value));
                        ctrl.$setValidity('sixCharactersValidator', value.length === 6);

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
                        if (text) {
                            var transformedInput = text.replace(/[^0-9]/g,'');
                            if (transformedInput !== text) {
                                ngModelCtrl.$setViewValue(transformedInput);
                                ngModelCtrl.$render();
                            }
                            return transformedInput;
                        }
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

        .directive('match', ['$parse',function($parse) {
            return {
                require: '?ngModel',
                restrict: 'A',
                link: function (scope, elem, attrs, ctrl) {
                    if (!ctrl || !attrs.match) {
                        return;
                    }
                    var matchGetter = $parse(attrs.match);
                    var caselessGetter = $parse(attrs.matchCaseless);
                    var noMatchGetter = $parse(attrs.notMatch);
                    var matchIgnoreEmptyGetter = $parse(attrs.matchIgnoreEmpty);

                    scope.$watch(getMatchValue, function () {
                        ctrl.$$parseAndValidate();
                    });

                    ctrl.$validators.match = function (modelValue, viewValue) {
                        var matcher = modelValue || viewValue;
                        var match = getMatchValue();
                        var notMatch = noMatchGetter(scope);
                        var value;

                        if (matchIgnoreEmptyGetter(scope) && !viewValue) {
                            return true;
                        }

                        if (caselessGetter(scope)) {
                            value = angular.lowercase(matcher) === angular.lowercase(match);
                        } else {
                            value = matcher === match;
                        }
                        /*jslint bitwise: true */
                        value ^= notMatch;
                        /*jslint bitwise: false */
                        return !!value;
                    };

                    function getMatchValue() {
                        var match = matchGetter(scope);
                        if (angular.isObject(match) && match.hasOwnProperty('$viewValue')) {
                            match = match.$viewValue;
                        }
                        return match;
                    }
                }
            }
        }])

        .directive('postValidate', function() {
            return {
                restrict:'A',
                require: 'ngModel',
                link: function (scope, elm, attrs, ctrl) {
                    ctrl.$parsers.push(function(value) {

                        ctrl.$setValidity('length', value.replace(" ","").length === 6);

                        return value;

                    });
                }
            };
        })

})();