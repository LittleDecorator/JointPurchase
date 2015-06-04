//person service
purchase.service('loginModal', function ($modal, $rootScope) {

    function assignCurrentUser (user) {
        $rootScope.currentUser = user;
        return user;
    }

    return function() {
        var instance = $modal.open({
            templateUrl: 'pages/template/loginModal.html',
            controller: 'authController',
            controllerAs: 'authController'
        });
        console.log(instance);
        return instance.result.then(assignCurrentUser);
    };

});