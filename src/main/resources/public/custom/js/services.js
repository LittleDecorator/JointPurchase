
purchase.service('loginModal', function ($modal, $rootScope) {

    function assignCurrentUser (user) {
        console.log("in assign user -> "+user);
        $rootScope.currentUser = user;
        return user;
    }

    return function() {
        var instance = $modal.open({
            templateUrl: 'pages/template/loginModal.html',
            controller: 'mainController'
            //controllerAs: 'authController'
        });
        console.log(instance);
        return instance.result.then(assignCurrentUser);
    };

});

purchase.service('authService',function($rootScope,$cookies){
    console.log($rootScope.currentUser);
    return {
        isAdmin: function () {
            //console.log("check isAdmin");
            if (typeof $rootScope.currentUser != 'undefined') {
                return $rootScope.currentUser.isAdmin;
            } else {
                return false;
            }
        },

        isAuth : function () {
            //console.log("check isAuth");
            if (typeof $cookies.get('token') == 'undefined') {
                return false;
            } else {
                //$rootScope.currentUser.name;
                return true;
            }
        }
    }

});

purchase.service('resolveService',function($q,factory){
    this.getItem = function(id){
        var deferred = $q.defer();
        factory.item.get({id:id},function(data){
            deferred.resolve(data);
        });
        return deferred.promise;
    };

    this.getPerson = function(id){
        var deferred = $q.defer();
        factory.customer.get({id:id},function(data){
            deferred.resolve(data);
        });
        return deferred.promise;
    };

    this.getOrder = function(id){
        var deferred = $q.defer();
        factory.order.get({id:id},function (data) {
            deferred.resolve(data);
        });
        return deferred.promise;
    };

    this.getCompany = function(id){
        var deferred = $q.defer();
        factory.company.get({id:id},function(data){
            deferred.resolve(data);
        });
        return deferred.promise;
    };

    this.getProduct = function(itemId){
        var deferred = $q.defer();
        factory.itemDetail.get({id: itemId},function(data){
            deferred.resolve(data);
        });
        return deferred.promise;
    }
});