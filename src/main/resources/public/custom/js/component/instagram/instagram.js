(function(){
    angular.module('instagram',[]);
})();

(function() {
    'use strict';

    angular.module('instagram')
        .controller('instagramController',['$scope','$state','$stateParams','dataResources','$timeout', function ($scope, $state, $stateParams, dataResources, $timeout){

            var mvm = $scope.$parent.mvm;
            var vm = this;
            var showCount = 15;

            vm.init = init;
            vm.remove = remove;
            vm.save = save;
            vm.countShown = countShown;

            vm.posts = [];
            vm.toMainIsLocked = false;

            function init(){
              dataResources.instagram.posts.all({all:true}).$promise.then(function (result) {
                angular.forEach(result, function(value, key){
                  countShown(value, true);
                  vm.posts.push(value);
                });
              });
            }

            function save(){
              dataResources.instagram.posts.put(vm.posts);
            }

            function remove(id){
              dataResources.instagram.posts.delete({id:id}).$promise.then(function (result) {
                var orig = helpers.findInArrayById(vm.posts, id);
                var idx = vm.posts.indexOf(orig);
                vm.posts.splice(idx,1);
              })
            }

            function countShown(post, onlyNegativ){
              if(post.showOnMain){
                showCount--;
              } else if(!onlyNegativ){
                showCount++;
              }
              vm.toMainIsLocked = showCount === 0;
            }

            init();

        }])
})();