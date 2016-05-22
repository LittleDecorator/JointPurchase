(function(){
    angular.module('purchase.filters',[]);
})();

(function() {
    'use strict';

    angular.module('purchase.filters')
        .filter('sumByKey', function() {
            return function(data) {
                if (typeof(data) === 'undefined') {
                    return 0;
                }

                var sum = 0;
                for (var i = data.length - 1; i >= 0; i--) {
                    var item = data[i];
                    sum += parseInt(item.cou * item.price);
                }

                return sum;
            };
        })

        .filter('asZero', function() {
            return function(data) {
                if (typeof(data) === 'undefined' || !data) {
                    return 0;
                } else {
                    return data;
                }

            };
        })

        .filter('nvl', function() {
            return function (value, param) {
                if (value) {
                    return value;
                } else {
                    return param;
                }
            }
        })
})();