// create the module and name it purchase
// also include ngRoute for all our routing needs
var purchase = angular.module('purchase', ['ngRoute','ui.bootstrap']);

// configure our routes
purchase.config(function($routeProvider) {

    $routeProvider

        // route for the home page
        .when('/', {
            templateUrl : 'pages/home.html',
            controller  : 'mainController'
        })

        // route for the about page
        .when('/about', {
            templateUrl : 'pages/about.html',
            controller  : 'aboutController'
        })

        // route for the order page
        .when('/orders', {
            templateUrl : 'pages/orders.html',
            controller  : 'orderController'
        })

        // route for the about page
        .when('/people', {
            templateUrl : 'pages/people.html',
            controller  : 'customerController'
        })

        // route for the about page
        .when('/company', {
            templateUrl : 'pages/company.html',
            controller  : 'stub'
        })

        // route for the contact page
        .when('/contact', {
            templateUrl : 'pages/contact.html',
            controller  : 'contactController'
        });
});

// create the controller and inject Angular's $scope and $http
purchase.controller('mainController', ['$scope', '$http', function($scope,$http) {
    purchase.service.home($scope,$http);
}]);

purchase.controller('aboutController', function($scope) {
    $scope.message = 'Look! I am an about page.';
});

purchase.controller('contactController', function($scope) {
    $scope.message = 'Contact us! JK. This is just a demo.';
});

purchase.controller('orderController', ['$scope', '$http', function($scope,$http){
    purchase.service.getOrders($scope,$http);
}]);

purchase.controller('customerController',function($scope,$modal,$log) {

    $scope.items = ['item1', 'item2', 'item3'];

    $scope.add = function(size){

        console.log("in add");
        var modalInstance = $modal.open({
            templateUrl: 'pages/customer.html',
            controller: 'modalController',
            size: size,
            resolve: {
                items: function () {
                    return $scope.items;
                }
            }
        });

        modalInstance.result.then(function (selectedItem) {
            $scope.selected = selectedItem;
        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
        });
    }
});

purchase.controller('modalController',function($scope,$modalInstance, items){
    $scope.items = items;
    $scope.selected = {
        item: $scope.items[0]
    };

    $scope.ok = function () {
        $modalInstance.close($scope.selected.item);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});

purchase.controller('stub',function($scope){
    //empty
});

