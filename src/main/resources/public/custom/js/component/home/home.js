(function(){
        angular.module('home',[]);
})();

(function(){
    'use strict';

    angular.module('home')
        .controller('homeController',['$scope','dataResources', function ($scope, dataResources) {

	        var vm = this;

	        vm.linkClick = linkClick;
	        vm.menuClick = menuClick;
	        vm.subscribe = subscribe;
	        vm.loadImages = loadImages;
	        vm.scrollDown = scrollDown;

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
			
			vm.images = [];

	        function linkClick($index) {
		        //TODO: add opened socials in new tab
	        }

	        function menuClick($index) {
				//TODO: add transition to pages
	        }
			
			function subscribe(){
				//TODO: add subscribe logic
			}
			
			function loadImages() {
				vm.images = dataResources.instagram.image.all();
			}

			loadImages();

	        /**
	         * Скроллирование до следующей секции
	         */
			function scrollDown() {
				$('#main').animate({ scrollTop: $('#nature').offset().top - 50}, 500, 'linear');
			}
	        
        }]);
})();
