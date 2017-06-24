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
	        vm.subscribe = subscribe;

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
		        { name: 'Акции'}
	        ];
			
			vm.forms = {};

	        function linkClick($index) {
		        //TODO: add opened socials in new tab
	        }

	        function menuClick($index) {
				//TODO: add transition to pages
	        }
			
			function subscribe(){
				//TODO: add subscribe logic
			}

        }]);
})();
