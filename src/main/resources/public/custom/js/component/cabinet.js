(function(){
    angular.module('cabinet',[]);
})();

(function() {
    'use strict';

    angular.module('cabinet')
        .controller('cabinetController',['$scope','$state','$stateParams','dataResources','$timeout','statusMap','deliveryMap', function ($scope, $state, $stateParams, dataResources, $timeout, statusMap, deliveryMap) {

            var templatePath = "pages/fragment/cabinet/";


            $scope.statuses = statusMap;
            $scope.deliveries = deliveryMap;

            $scope.getTemplateUrl = function(){
                if($scope.width < 601){
                    return templatePath + "cabinet-sm.html"
                }
                if($scope.width > 600){
                    if($scope.width < 1025){
                        return templatePath + "cabinet-md.html"
                    }
                    return templatePath + "cabinet-lg.html"
                }
            };

        }])


        .controller('cabinetHistoryController',['$scope','$state','$stateParams','dataResources','$timeout', function ($scope, $state, $stateParams, dataResources, $timeout) {

            $scope.filter = {dateFrom:null, dateTo:null, status:null, limit:30, offset:0};
            $scope.history = [];

            var confirmedFilter = angular.copy($scope.filter);
            var busy = false;
            var portion = 0;
            $scope.stopLoad=false;
            $scope.allDataLoaded = false;


            $scope.loadData = function(isClean){
                if(!$scope.stopLoad && !busy){
                    busy = true;

                    dataResources.orderHistory.all(confirmedFilter).$promise.then(function(data){

                        if(data.length < confirmedFilter.limit){
                            $scope.stopLoad = true;
                        }

                        if(isClean){
                            $scope.history = [];
                        }

                        $scope.history = data;

                        portion++;
                        confirmedFilter.offset = portion * confirmedFilter.limit;
                        $scope.allDataLoaded = true;
                        busy = false;
                    });
                }
            };

            //clear filter
            $scope.clear = function () {
                portion = 0;
                $scope.filter = {dateFrom:null, dateTo:null, status:null, limit:30, offset:0};
                confirmedFilter = angular.copy($scope.filter);
                localStorage.removeItem($state.current.name);
                $scope.stopLoad = false;
                $scope.loadData(true);
            };

            /* apply filter */
            $scope.apply = function () {
                portion = 0;
                confirmedFilter = angular.copy($scope.filter);
                localStorage.setItem($state.current.name,angular.toJson(confirmedFilter));
                $scope.stopLoad = false;
                $scope.loadData(true);
            };

            $scope.apply();

        }])

        .controller('cabinetPrivateController',['$scope','$state','$stateParams','dataResources','$timeout', function ($scope, $state, $stateParams, dataResources, $timeout) {

        }])

})();