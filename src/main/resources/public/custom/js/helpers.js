var helpers = {

    dateFormat : function(dateMilliseconds) {
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

    findInArrayById: function(array,id){
        var res = {};
        array.forEach(function(elem){
            if(elem.id === id){
                res = elem;
                return false;
            } else {
                return true;
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
        route.getRoutes().forEach(function(elem){
            if(elem.name === name){
                angular.extend(res,elem);
                return false;
            } else {
                return true;
            }
        });
        return res;
    }

};
