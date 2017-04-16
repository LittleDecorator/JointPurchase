(function(){
    angular.module('search',[]);
})();

(function() {
    'use strict';

    angular.module('search')
        .controller('searchController', ['$scope', '$state', '$stateParams', 'dataResources', '$timeout', function ($scope, $state, $stateParams, dataResources, $timeout) {
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

            function createFilterFor () {
                var lowercaseQuery = angular.lowercase($scope.searchText);
                return function filterFn(state) {
                    return (state.value.indexOf(lowercaseQuery) === 0);
                };
            }

            $scope.keyPress = function(keyCode) {
                if (keyCode == 13){
                    $scope.querySearch();
                }
            };

            $scope.querySearch = function() {
                //var results = query ? $scope.states.filter( createFilterFor(query) ) : $scope.states,
                var results = $scope.searchText ? $scope.states.filter( createFilterFor() ) : [],
                    deferred;
                //if ($scope.simulateQuery) {
                //    deferred = $q.defer();
                //    $timeout(function () { deferred.resolve( results ); }, Math.random() * 1000, false);
                //    $scope.filtered = deferred.promise;
                //} else {
                    $scope.filtered = results;
                //}

            };

            /**
             * Build `states` list of key/value pairs
             */
            $scope.loadAll = function() {
                var allStates = 'Alabama, Alaska, Arizona, Arkansas, California, Colorado, Connecticut, Delaware,\
              Florida, Georgia, Hawaii, Idaho, Illinois, Indiana, Iowa, Kansas, Kentucky, Louisiana,\
              Maine, Maryland, Massachusetts, Michigan, Minnesota, Mississippi, Missouri, Montana,\
              Nebraska, Nevada, New Hampshire, New Jersey, New Mexico, New York, North Carolina,\
              North Dakota, Ohio, Oklahoma, Oregon, Pennsylvania, Rhode Island, South Carolina,\
              South Dakota, Tennessee, Texas, Utah, Vermont, Virginia, Washington, West Virginia,\
              Wisconsin, Wyoming';
                return allStates.split(/, +/g).map( function (state) {
                    return {
                        value: state.toLowerCase(),
                        display: state
                    };
                });
            };

            /**
             * Переход на карточку товара из списка поиска
             * @param id
             */
            $scope.itemView = function(id){
                $state.go("search.detail", {itemId: id});
            };

            $scope.clear = function(){
                $scope.searchText = null;
                $scope.filtered = [];
            };

            $scope.states = $scope.loadAll();

            $timeout(function(){
                $('#searchInput').focus();
            },100);

        }])
})();