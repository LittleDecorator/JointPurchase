
/**
 * @license Angular UI Tree v2.0.13
 * (c) 2010-2014. https://github.com/JimLiu/angular-ui-tree
 * License: MIT
 */
(function () {
    'use strict';

    angular.module('ngTree', [])
        .constant('treeConfig', {
            treeClass: 'angular-ui-tree',
            emptyTreeClass: 'angular-ui-tree-empty',
            hiddenClass: 'angular-ui-tree-hidden',
            nodeClass: 'angular-ui-tree-node',
        });

})();

(function () {
    'use strict';

    angular.module('ngTree')
        .factory('$treeHelper', function ($document, $window) {
                return {

                    /**
                     * A hashtable used to storage data of nodes
                     * @type {Object}
                     */
                    nodesData: {
                    },

                    setNodeAttribute: function(scope, attrName, val) {
                        var data = this.nodesData[scope.$modelValue.$$hashKey];
                        if (!data) {
                            data = {};
                            this.nodesData[scope.$modelValue.$$hashKey] = data;
                        }
                        data[attrName] = val;
                    },

                    getNodeAttribute: function(scope, attrName) {
                        var data = this.nodesData[scope.$modelValue.$$hashKey];
                        if (data) {
                            return data[attrName];
                        }
                        return null;
                    },

                    /**
                     * get the event object for touchs
                     * @param  {[type]} e [description]
                     * @return {[type]}   [description]
                     */
                    eventObj: function(e) {
                        var obj = e;
                        if (e.targetTouches !== undefined) {
                            obj = e.targetTouches.item(0);
                        } else if (e.originalEvent !== undefined && e.originalEvent.targetTouches !== undefined) {
                            obj = e.originalEvent.targetTouches.item(0);
                        }
                        return obj;
                    },

                    /**
                     * @ngdoc method
                     * @name hippo.theme#height
                     * @methodOf ui.tree.service:$helper
                     *
                     * @description
                     * Get the height of an element.
                     *
                     * @param {Object} element Angular element.
                     * @returns {String} Height
                     */
                    height: function (element) {
                        return element.prop('scrollHeight');
                    },

                    /**
                     * @ngdoc method
                     * @name hippo.theme#width
                     * @methodOf ui.tree.service:$helper
                     *
                     * @description
                     * Get the width of an element.
                     *
                     * @param {Object} element Angular element.
                     * @returns {String} Width
                     */
                    width: function (element) {
                        return element.prop('scrollWidth');
                    },

                    /**
                     * @ngdoc method
                     * @name hippo.theme#offset
                     * @methodOf ui.nestedSortable.service:$helper
                     *
                     * @description
                     * Get the offset values of an element.
                     *
                     * @param {Object} element Angular element.
                     * @returns {Object} Object with properties width, height, top and left
                     */
                    offset: function (element) {
                        var boundingClientRect = element[0].getBoundingClientRect();

                        return {
                            width: element.prop('offsetWidth'),
                            height: element.prop('offsetHeight'),
                            top: boundingClientRect.top + ($window.pageYOffset || $document[0].body.scrollTop || $document[0].documentElement.scrollTop),
                            left: boundingClientRect.left + ($window.pageXOffset || $document[0].body.scrollLeft  || $document[0].documentElement.scrollLeft)
                        };
                    },

                    /**
                     * @ngdoc method
                     * @name hippo.theme#positionStarted
                     * @methodOf ui.tree.service:$helper
                     *
                     * @description
                     * Get the start position of the target element according to the provided event properties.
                     *
                     * @param {Object} e Event
                     * @param {Object} target Target element
                     * @returns {Object} Object with properties offsetX, offsetY, startX, startY, nowX and dirX.
                     */
                    positionStarted: function (e, target) {
                        var pos = {};
                        pos.offsetX = e.pageX - this.offset(target).left;
                        pos.offsetY = e.pageY - this.offset(target).top;
                        pos.startX = pos.lastX = e.pageX;
                        pos.startY = pos.lastY = e.pageY;
                        pos.nowX = pos.nowY = pos.distX = pos.distY = pos.dirAx = 0;
                        pos.dirX = pos.dirY = pos.lastDirX = pos.lastDirY = pos.distAxX = pos.distAxY = 0;
                        return pos;
                    },

                    positionMoved: function (e, pos, firstMoving) {
                        // mouse position last events
                        pos.lastX = pos.nowX;
                        pos.lastY = pos.nowY;

                        // mouse position this events
                        pos.nowX  = e.pageX;
                        pos.nowY  = e.pageY;

                        // distance mouse moved between events
                        pos.distX = pos.nowX - pos.lastX;
                        pos.distY = pos.nowY - pos.lastY;

                        // direction mouse was moving
                        pos.lastDirX = pos.dirX;
                        pos.lastDirY = pos.dirY;

                        // direction mouse is now moving (on both axis)
                        pos.dirX = pos.distX === 0 ? 0 : pos.distX > 0 ? 1 : -1;
                        pos.dirY = pos.distY === 0 ? 0 : pos.distY > 0 ? 1 : -1;

                        // axis mouse is now moving on
                        var newAx   = Math.abs(pos.distX) > Math.abs(pos.distY) ? 1 : 0;

                        // do nothing on first move
                        if (firstMoving) {
                            pos.dirAx  = newAx;
                            pos.moving = true;
                            return;
                        }

                        // calc distance moved on this axis (and direction)
                        if (pos.dirAx !== newAx) {
                            pos.distAxX = 0;
                            pos.distAxY = 0;
                        } else {
                            pos.distAxX += Math.abs(pos.distX);
                            if (pos.dirX !== 0 && pos.dirX !== pos.lastDirX) {
                                pos.distAxX = 0;
                            }

                            pos.distAxY += Math.abs(pos.distY);
                            if (pos.dirY !== 0 && pos.dirY !== pos.lastDirY) {
                                pos.distAxY = 0;
                            }
                        }

                        pos.dirAx = newAx;
                    }
                };
            }
        );

})();
(function () {
    'use strict';

    angular.module('ngTree')
        .controller('TreeController', function ($scope, $element, $attrs, treeConfig) {
            console.log("Enter Tree Controller");

                this.scope = $scope;

                $scope.$element = $element;
                $scope.$nodesScope = null; // root nodes
                $scope.$emptyElm = null;
                $scope.$callbacks = null;

                // Check if it's a empty tree
                $scope.isEmpty = function() {
                    return ($scope.$nodesScope && $scope.$nodesScope.$modelValue && $scope.$nodesScope.$modelValue.length === 0);
                };

                var collapseOrExpand = function(scope, collapsed) {
                    var nodes = scope.childNodes();
                    for (var i = 0; i < nodes.length; i++) {
                        collapsed ? nodes[i].collapse() : nodes[i].expand();
                        var subScope = nodes[i].$childNodesScope;
                        if (subScope) {
                            collapseOrExpand(subScope, collapsed);
                        }
                    }
                };

                $scope.collapseAll = function() {
                    collapseOrExpand($scope.$nodesScope, true);
                };

                $scope.expandAll = function() {
                    collapseOrExpand($scope.$nodesScope, false);
                };

            }
        );
})();


(function () {
    'use strict';

    angular.module('ngTree')
        .controller('NodeController', function ($scope, $element, $attrs, treeConfig) {
                this.scope = $scope;

                $scope.$element = $element;
                $scope.$modelValue = null; // Model value for node;
                $scope.$parentNodeScope = null; // uiTreeNode Scope of parent node;
                $scope.$childNodesScope = null; // uiTreeNodes Scope of child nodes.
                $scope.$treeScope = null; // uiTree scope
                $scope.collapsed = true;

                /*$scope.init = function(controllersArr) {
                    var treeNodesCtrl = controllersArr[0];
                    $scope.$treeScope = controllersArr[1] ? controllersArr[1].scope : null;

                    // find the scope of it's parent node
                    $scope.$parentNodeScope = treeNodesCtrl.scope.$nodeScope;
                    // modelValue for current node
                    $scope.$modelValue = treeNodesCtrl.scope.$modelValue[$scope.$index];
                    treeNodesCtrl.scope.initSubNode($scope); // init sub nodes
                };*/

                $scope.index = function() {
                    return $scope.$parentNodeScope.$modelValue.indexOf($scope.$modelValue);
                };

                $scope.isChild = function(targetNode) {
                    var nodes = $scope.childNodes();
                    return nodes && nodes.indexOf(targetNode) > -1;
                };

                $scope.siblings = function() {
                    return $scope.$parentNodeScope.childNodes();
                };

                $scope.childNodesCount = function() {
                    return $scope.childNodes() ? $scope.childNodes().length : 0;
                };

                $scope.hasChild = function() {
                    return $scope.childNodesCount() > 0;
                };

                $scope.childNodes = function() {
                    return $scope.$childNodesScope && $scope.$childNodesScope.$modelValue ?
                        $scope.$childNodesScope.childNodes() :
                        null;
                };

                $scope.toggle = function() {
                    $scope.collapsed = !$scope.collapsed;
                };

                $scope.collapse = function() {
                    $scope.collapsed = true;
                };

                $scope.expand = function() {
                    $scope.collapsed = false;
                };

            }
        );
})();

(function () {
    'use strict';

    angular.module('ngTree')
        .directive('tree', function(treeConfig, $window) {
                return {
                    restrict: 'A',
                    scope: true,
                    controller: 'TreeController',
                    link: function($scope, element, attrs) {
                        console.log($scope);
                        /*var callbacks = {
                            accept: null
                        };*/

                        var config = {};
                        angular.extend(config, treeConfig);
                        if (config.treeClass) {
                            element.addClass(config.treeClass);
                        }

                        $scope.$emptyElm = angular.element($window.document.createElement('div'));
                        if (config.emptyTreeClass) {
                            $scope.$emptyElm.addClass(config.emptyTreeClass);
                        }

                        /*$scope.$watch(attrs.tree, function(newVal, oldVal){
                            angular.forEach(newVal, function(value, key){
                                if (callbacks[key]) {
                                    if (typeof value === "function") {
                                        callbacks[key] = value;
                                    }
                                }
                            });

                            $scope.$callbacks = callbacks;
                        }, true);*/


                    }
                };
            }
        );
})();

(function () {
    'use strict';

    angular.module('ngTree')

        .directive('node', function (treeConfig, $treeHelper, $window, $document, $timeout) {
            return {
                require: ['^tree'],
                restrict: 'A',
                controller: 'NodeController',
                link: function($scope, element, attrs, controllersArr) {
                    console.log($scope);
                    var config = {};
                    angular.extend(config, treeConfig);
                    if (config.nodeClass) {
                        element.addClass(config.nodeClass);
                    }
                    console.log(controllersArr);
                    //scope.init(controllersArr);
                    console.log($treeHelper);
                    //scope.collapsed = !!$treeHelper.getNodeAttribute(scope, 'collapsed');
                    attrs.$observe('collapsed', function(val) {
                        var collapsed = $scope.$eval(val);
                        if((typeof collapsed) == "boolean") {
                            $scope.collapsed = collapsed;
                        }
                    });

                    //scope.$watch('collapsed', function(val) {
                    //    $treeHelper.setNodeAttribute(scope, 'collapsed', val);
                    //    attrs.$set('collapsed', val);
                    //});

                }
            };
        });

})();
