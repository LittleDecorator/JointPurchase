(function(){
    angular.module('instagram',[]);
})();

(function() {
    'use strict';

    angular.module('instagram')
        .controller('instagramController',['$scope','$state','$stateParams','dataResources','$timeout', '$mdToast',
          function ($scope, $state, $stateParams, dataResources, $timeout, $mdToast){

            var toast = $mdToast.simple().position('top right').hideDelay(3000);
            var mvm = $scope.$parent.mvm;
            var vm = this;
            var showCount = 15;

            vm.init = init;
            vm.remove = remove;
            vm.save = save;
            vm.countShown = countShown;
            vm.refresh = refresh;

            vm.posts = [];
            vm.toMainIsLocked = false;

            function init(){
              mvm.showLoader = true;
              dataResources.instagram.posts.all({all:true}).$promise.then(function (result) {
                angular.forEach(result, function(value, key){
                  countShown(value, true);
                  vm.posts.push(value);
                });
                vm.posts.sort(desc)
                mvm.showLoader = false;
              });
            }

            function save(){
              dataResources.instagram.posts.put(vm.posts);
            }

            function refresh(){
              mvm.showLoader = true;
              console.log(mvm.showLoader)
              dataResources.instagram.refresh.all().$promise.then(function (result){
                vm.posts = [];
                angular.forEach(result, function(value, key){
                  countShown(value, true);
                  vm.posts.push(value);
                });
                vm.posts.sort(desc);
                mvm.showLoader = false;
              }, function(error){
                $mdToast.show(toast.textContent('Неудалось обновить список постов инстаграм\n'+ error.message).theme('error'));
                mvm.showLoader = false;
              });
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
              // console.log(showCount)
              vm.toMainIsLocked = showCount === 0;
            }

          function desc(x, y){
            return y.createdTime - x.createdTime
          }

            init();

        }])
})();