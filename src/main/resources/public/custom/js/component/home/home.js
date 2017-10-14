(function(){
        angular.module('home',[]);
})();

(function(){
    'use strict';

    angular.module('home')
        .controller('homeController',['$scope','dataResources','$state', function ($scope, dataResources, $state) {

	        var vm = this;

	        vm.linkClick = linkClick;
	        vm.menuClick = menuClick;
	        vm.subscribe = subscribe;
	        vm.loadImages = loadImages;
	        vm.scrollDown = scrollDown;

	        vm.links = [
		        // { icon: 'mail' },
		        // { icon: 'message' },
		        { icon: 'facebook', url:''},
		        { icon: 'vk', url: 'https://vk.com/club68247236'},
		        { icon: 'instagram', url: ''}
	        ];

	        vm.menus = [
		        { name: 'Каталог', ref: 'catalog'},
		        { name: 'О нас', ref:'about'},
		        { name: 'Контакты', ref:'contact'},
		        { name: 'Доставка', ref:'delivery'},
		        { name: 'Акции' , ref:'stock'}
	        ];
			
			vm.forms = {};
			
			vm.images = [];

	        function linkClick($index) {
		        //TODO: add opened socials in new tab
	        }

	        function menuClick($index) {
		        $state.go(vm.menus[$index].ref);
	        }
			
			function subscribe(){
				//TODO: add subscribe logic
			}
			
			function loadImages() {
				// TODO: Выключим, пока не придумаем легкий запрос для получения instagram изображений
				// vm.images = dataResources.instagram.image.all();
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
