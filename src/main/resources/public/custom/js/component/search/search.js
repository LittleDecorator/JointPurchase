(function(){
    angular.module('search',[]);
})();

(function() {
    'use strict';

    angular.module('search')
        .controller('searchController', ['$scope', '$state', '$stateParams', 'dataResources', '$timeout','$q', function ($scope, $state, $stateParams, dataResources, $timeout, $q) {
            $scope.simulateQuery = false;
            $scope.isDisabled    = false;
            $scope.searchFilter = {category:null, company:null, criteria:null, offset:0, limit:30};
            $scope.searchResult = [];

            if($stateParams.criteria){
                $scope.searchFilter.criteria = $stateParams.criteria;
                /*
                 * Событие поиска.
                 * Пока учитавается только текст
                 */
                dataResources.catalog.search.get({criteria: $scope.searchFilter.criteria}).$promise.then(
                        function (data) {
                            $scope.searchResult = data;
                            $scope.showResults = true;
                        },
                        function (error) {
                            console.log(error);
                        }
                )
            }

            /* поиск в БД */
            $scope.querySearch = function() {
                if($scope.searchText){
                    dataResources.catalog.search.get({criteria: $scope.searchText}).$promise.then(function(data){
                        $scope.filtered = data;
                    })
                }
            };

            // нажатие кнопки поиск
            $scope.searchItem = function(){
                if($scope.searchText){
                    /* refresh state because name can be changed */
                    if($state.current == 'search'){
                        $state.go('search', {criteria: $scope.searchText},{notify:false}).then(function(){
                            // $scope.item.id = data.result;
                            $stateParams.criteria = $scope.searchText;
                            $rootScope.$broadcast('$refreshBreadcrumbs',$state);
                        });
                    } else {
                        $state.go('search', {criteria: $scope.searchText});
                    }
                }
            };
            
            /* переход на результат поиска */
            $scope.keyPress = function(keyCode) {
                if (keyCode == 13) {
                    $scope.closeThisDialog();
                    $scope.searchItem();
                }
            };

            /**
             * Переход на карточку товара из списка поиска
             * @param id
             */
            $scope.itemView = function(id){
                $state.go("catalog.detail", {itemId: id});
            };

            $scope.clear = function(){
                $scope.searchText = null;
                $scope.filtered = [];
            };

            $timeout(function(){
                $('#searchInput').focus();
            },100);

        }])
})();