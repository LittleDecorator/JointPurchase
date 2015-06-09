
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