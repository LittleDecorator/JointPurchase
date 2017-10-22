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

        .filter('nospace', function () {
            return function (value) {
                return (!value) ? '' : value.replace(/ /g, '');
            };
        })

            //replace uppercase to regular case
        .filter('humanizeDoc', function () {
            return function (doc) {
                if (!doc) return;
                if (doc.type === 'directive') {
                    return doc.name.replace(/([A-Z])/g, function ($1) {
                        return '-' + $1.toLowerCase();
                    });
                }

                return doc.label || doc.name;
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
                if (value && value.toString().trim().length > 0) {
                    return value;
                } else {
                    return param;
                }
            }
        })

        .filter('tel', function () {
            return function (tel) {
                if (!tel) {
                    return '';
                }

                var value = tel.toString().trim().replace(/^\+/, '');

                if (value.match(/[^0-9]/)) {
                    return tel;
                }

                var country, city, number;

                switch (value.length) {
                    case 10: // +1PPP####### -> C (PPP) ###-####
                        country = 1;
                        city = value.slice(0, 3);
                        number = value.slice(3);
                        break;

                    case 11: // +CPPP####### -> CCC (PP) ###-####
                        country = value[0];
                        city = value.slice(1, 4);
                        number = value.slice(4);
                        break;

                    case 12: // +CCCPP####### -> CCC (PP) ###-####
                        country = value.slice(0, 3);
                        city = value.slice(3, 5);
                        number = value.slice(5);
                        break;

                    default:
                        return tel;
                }

                if (country == 1) {
                    country = "";
                }

                number = number.slice(0, 3) + '-' + number.slice(3,5) + '-' + number.slice(5);

                return (country + " (" + city + ") " + number).trim();
            };
        })

        .filter('lines', function () {
            return function(text) {
                return text.replace(/(\\r)?\\n/g, '<br />');
            }
    });
})();