(function(){
        angular.module('home',[]);
})();

(function(){
    'use strict';

    angular.module('home')
        .controller('homeController',['$scope', function ($scope) {

	        var vm = this;

	        vm.linkClick = linkClick;
	        vm.menuClick = menuClick;

	        vm.links = [
		        { icon: 'mail' },
		        { icon: 'message' },
		        { icon: 'facebook' },
		        { icon: 'vk' },
		        { icon: 'instagram' },
	        ];

	        vm.menus = [
		        { name: 'Каталог'},
		        { name: 'О нас'},
		        { name: 'Контакты'},
		        { name: 'Доставка'},
		        { name: 'Акции'},
	        ];

	        function linkClick($index) {
		        // var clickedLink = vm.links[$index];
	        }

	        function menuClick($index) {

	        }

        }]);
})();
