//(function(){
//    angular.module('purchase.listeners',[]);
//})();
//
//(function(){
//    'use strict';
//
//    angular.module('purchase.listeners')
//
//        .directive('changeTimeout',['$timeout',function($timeout) {
//            return {
//                scope:{
//                    onChange:'&'
//                },
//                require: 'ngModel',
//                link: function(scope, elem, attr, ctrl) {
//                    //if (!attr.ngChange) {
//                    //    throw new TypeError('ng-change directive not present');
//                    //}
//                    console.log("FLA");
//                    console.log(ctrl.$viewChangeListeners);
//                    ctrl.$viewChangeListeners.push(function() {
//                        $timeout( function() {
//
//                            console.log(ctrl.$viewChangeListeners);
//
//
//                            console.log(attr.changeTimeout);
//                            console.log(ctrl.$viewValue);
//                            console.log(ctrl.$modelValue);
//                            //scope.$apply(attr.ngChange);
//                            scope.onChange();
//                        }, attr.changeTimeout || 0)
//
//                    //angular.forEach(ctrl.$viewChangeListeners, function(listener, index) {
//                    //    ctrl.$viewChangeListeners[index] = $timeout(function() {
//                    //        scope.$apply(attr.ngChange);
//                    //    }, attr.changeTimeout || 0);
//                });
//            }
//        }
//    }]);
//
//
//})();
