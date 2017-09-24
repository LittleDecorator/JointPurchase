var hasOwnProperty = Object.prototype.hasOwnProperty;

var helpers = {

    isEmptyObject: function(obj) {

    // null and undefined are "empty"
    if (obj == null) return true;

    // Assume if it has a length property with a non-zero value
    // that that property is correct.
    if (obj.length > 0)    return false;
    if (obj.length === 0)  return true;

    // Otherwise, does it have any properties of its own?
    // Note that this doesn't handle
    // toString and valueOf enumeration bugs in IE < 9
    for (var key in obj) {
        if (hasOwnProperty.call(obj, key)) return false;
    }

    return true;
    },

    isPropertyNotEmpty : function(obj, propName){
        var result = false;
        if(obj.hasOwnProperty(propName) && obj[propName]){
            result = true;
        }
        return result;
    },

    dateTimeFormat : function(dateMilliseconds) {
        var YYYY, M, D,MM,DD, h,hh, m,mm, s,ss;
        var date = new Date(dateMilliseconds);
        YYYY = date.getFullYear();
        MM = (M=date.getMonth()+1)<10?('0'+M):M;
        DD = (D=date.getDate())<10?('0'+D):D;

        h=date.getHours();
        hh = h<10?('0'+h):h;
        mm=(m=date.getMinutes())<10?('0'+m):m;
        ss=(s=date.getSeconds())<10?('0'+s):s;

        return DD+"."+MM+"."+YYYY+" "+hh+":"+mm+":"+ss;
    },

    dateFormat : function(dateMilliseconds) {
        var YYYY, M, D,MM,DD;
        var date = new Date(dateMilliseconds);
        YYYY = date.getFullYear();
        MM = (M=date.getMonth()+1)<10?('0'+M):M;
        DD = (D=date.getDate())<10?('0'+D):D;

        return DD+"."+MM+"."+YYYY;
    },

    arrayContainById: function(array,id){
        var res = false;
        array.some(function(elem){
            if(elem.id == id){
                return res = true;
            } else {
                return res = false;
            }
        });
        return res;
    },

    findInArrayById: function(array,id){
        var res = {};
        array.some(function(elem){
            if(elem.id == id){
                res = elem;
                return true;
            } else {
                return false;
            }
        });
        return res;
    },

    findInArrayByPropertyValue: function(array,property,value){
        var res = {};
        array.some(function(elem){
            if(elem[property] == value){
                res = elem;
                return true;
            } else {
                return false;
            }
        });
        return res;
    },

    findIdxInArrayById: function(array,id){
        var res = null;
        array.some(function(elem, index){
            if(elem.id == id){
                res = index;
                return true;
            } else {
                return false;
            }
        });
        return res;
    },

    findInArrayByValue: function(array,value){
        var res = {};
        array.some(function(elem){
            if(elem.value == value){
                res = elem;
                return true;
            } else {
                return false;
            }
        });
        return res;
    },

    getFilterItem: function(){
        var filter = {};
        $(".filter-item :input:not(:button)").each(function() {
            var val = $.trim($(this).val());
            filter[$(this).attr("name")] = val;
        });
        return filter;
    },

    clearFilterItem: function(){
        $(".filter-item :input:not(:button)").each(function() {
            $.trim($(this).val(""));
        });
    },

    findRouteByName: function(name){
        var res = {};
        route.getRoutes().some(function(elem){
            if(elem.name === name){
                angular.extend(res,elem);
                return true;
            } else {
                return false;
            }
        });
        return res;
    },



    isEmpty: function(ob) {
        for (var i in ob) {
            return false;
        }
        return true;
    },

    isArray: function(obj){
        if( Object.prototype.toString.call(obj) === '[object Array]' ) {
            return true;
        }
        return false;
    },

    toArray: function(obj){
        var array = $.map(obj, function(value, index) {
            return [value];
        });
        return array;
    },

    guid : function() {
        function s4() {
            return Math.floor((1 + Math.random()) * 0x10000)
                .toString(16)
                .substring(1);
        }
        return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
            s4() + '-' + s4() + s4() + s4();
    },

    filterArray: function(src, filt) {
        //console.log("IT WORK INCORRECT. REPAIR IT!!!!!!!!!!!!!!!!!!");
        var temp = {}, i, result = [];
        // load contents of filt into object keys for faster lookup
        for (i = 0; i < filt.length; i++) {
            temp[filt[i].id] = true;
        }

        // go through src
        for (i = 0; i < src.length; i++) {
            if (!(src[i].id in temp)) {
                result.push(src[i]);
            }
        }
        return(result);
    },

    findRouteByUrl: function(url){
        var res = "home";
        route.getRoutes().some(function(route){
            if(helpers.endsWith(url,route.url)){
                res = route.name;
                return true;
            } else {
                return false;
            }
        });
        return res;
    },

    endsWith: function (str, suffix) {
        var startPos = str.length - suffix.length;
        var idx = str.indexOf(suffix,startPos);
        return idx > -1;
        //return str.indexOf(suffix, str.length - suffix.length) !== -1;
    },

    viewport: function() {
        var e = window, a = 'inner';
        if (!('innerWidth' in window )) {
            a = 'client';
            e = document.documentElement || document.body;
        }
        return {width: e[a + 'Width'], height: e[a + 'Height']};
    },

    centerItFixedWidth: function(target, outer){
        var out = $(outer);
        var tar = $(target);
        var x = out.width();
        var y = tar.outerWidth(true);
        var z = tar.index();
        var val = Math.max(0, (y * z) - (x - y)/2);
        //out.scrollLeft(val);
        out.animate({ 'scrollLeft': val},{duration: 200, easing: "linear", queue: false});
    },

    centerItVariableWidth: function(target, outer){
        var out = $(outer);
        var tar = $(target);
        var x = out.width();
        var y = tar.outerWidth(true);
        var z = tar.index();
        var q = 0;
        var m = out.find('li');
        //Just need to add up the width of all the elements before our target.
        for(var i = 0; i < z; i++){
            q+= $(m[i]).outerWidth(true);
        }
        var val = Math.max(0, q - (x - y)/2);
        out.scrollLeft(val);
    },
    // https://learn.javascript.ru/array-iteration
    groupBy: function(xs, key) {
        return xs.reduce(function(rv, x) {
            (rv[x[key]] = rv[x[key]] || []).push(x);
            return rv;
        }, {});
    }
};

if (!String.format) {
    String.format = function(format) {
        var args = Array.prototype.slice.call(arguments, 1);
        return format.replace(/{(\d+)}/g, function(match, number) {
            return typeof args[number] != 'undefined'
                ? args[number]
                : match
                ;
        });
    };
}
// https://stackoverflow.com/questions/14446511/what-is-the-most-efficient-method-to-groupby-on-a-javascript-array-of-objects
var DataGrouper = (function() {
    var has = function(obj, target) {
        return _.any(obj, function(value) {
            return _.isEqual(value, target);
        });
    };

    var keys = function(data, names) {
        return _.reduce(data, function(memo, item) {
            var key = _.pick(item, names);
            if (!has(memo, key)) {
                memo.push(key);
            }
            return memo;
        }, []);
    };

    var group = function(data, names) {
        var stems = keys(data, names);
        return _.map(stems, function(stem) {
            return {
                key: stem,
                vals:_.map(_.where(data, stem), function(item) {
                    return _.omit(item, names);
                })
            };
        });
    };

    group.register = function(name, converter) {
        return group[name] = function(data, names) {
            return _.map(group(data, names), converter);
        };
    };

    return group;
}());

DataGrouper.register("sum", function(item) {
    return _.extend({}, item.key, {Value: _.reduce(item.vals, function(memo, node) {
        return memo + Number(node.Value);
    }, 0)});
});

Array.prototype.move = function (old_index, new_index) {
    if (new_index >= this.length) {
        var k = new_index - this.length;
        while ((k--) + 1) {
            this.push(undefined);
        }
    }
    this.splice(new_index, 0, this.splice(old_index, 1)[0]);
    return this; // for testing purposes
};