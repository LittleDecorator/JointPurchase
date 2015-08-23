
(function () {
    'use strict';

    angular.module('ngTree', [])
        .constant('treeConfig', {
            treeClass: 'angular-ui-tree',
            hiddenClass: 'angular-ui-tree-hidden',
            nodeClass: 'angular-ui-tree-node'
        });

})();

(function () {
    'use strict';
    angular.module('ngTree').controller('TreeController', function ($scope) {
            $scope.$modelValue = null;

            // Check if it's a empty tree
            $scope.isEmpty = function() {
                return ($scope.$modelValue && $scope.$modelValue.length === 0);
            };

        }
    );
})();

(function () {
    'use strict';

    angular.module('ngTree').controller('TreeNodeController', function ($scope) {
            $scope.$modelValue = null; // Model value for node;
            $scope.collapsed = true;

            $scope.hasChild = function() {
                return $scope.$modelValue && $scope.$modelValue.nodes && $scope.$modelValue.nodes.length > 0;
            };

            //по сути делегирует в nodes для текущего node scope
            $scope.childNodes = function() {
                //создание $childNodesScope для node происходит в директиве nodes
                return $scope.hasChild() ?
                    $scope.$modelValue.nodes :
                    null;
            };

            $scope.toggle = function() {
                $scope.collapsed = !$scope.collapsed;
            };

            $scope.findProduct = function(){
                console.log("bla");
                $scope.filterByCategory($scope.$modelValue.id);
            }

        }
    );
})();

(function () {
    'use strict';
    angular.module('ngTree').directive('tree', function(treeConfig) {
            return {
                restrict: 'E',
                require: ['ngModel'],
                scope: true,
                controller: 'TreeController',
                link: function(scope, element, attrs, controllersArr) {
                    var ngModel = controllersArr[0];
                    var config = {};
                    angular.extend(config, treeConfig);
                    if (config.treeClass) {
                        element.addClass(config.treeClass);
                    }

                    if(ngModel) {
                        //вызовится при изменении $modelValue
                        ngModel.$render = function() {
                            if (!ngModel.$modelValue || !angular.isArray(ngModel.$modelValue)) {
                                scope.$modelValue = [];
                            }
                            scope.$modelValue = ngModel.$modelValue;
                        };
                    }
                }
            };
        }
    );
})();

(function () {
    'use strict';
    angular.module('ngTree').directive('node', function (treeConfig) {
            return {
                require: ['ngModel'],
                restrict: 'E',
                scope:true,
                controller: 'TreeNodeController',
                link: function(scope, element, attrs, controllersArr) {
                    var ngModel = controllersArr[0];

                    var config = {};
                    angular.extend(config, treeConfig);
                    if (config.nodeClass) {
                        element.addClass(config.nodeClass);
                    }

                    if(ngModel) {
                        //вызовится при изменении $modelValue
                        ngModel.$render = function() {
                            //console.log(ngModel);
                            if (!ngModel.$modelValue || !angular.isArray(ngModel.$modelValue)) {
                                scope.$modelValue = [];
                            }
                            scope.$modelValue = ngModel.$modelValue;
                        };
                    }
                }
            };
        }
    );
})();


