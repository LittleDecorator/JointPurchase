(function(){
    angular.module('contact',[]);
})();

(function() {
    'use strict';

    angular.module('contact')

        .controller('contactController',['$rootScope','$scope','dataResources','$timeout', '$mdToast',
            function($rootScope, $scope, dataResources, $timeout, $mdToast){

                var templatePath = "pages/fragment/contact/";
                var mvm = $scope.$parent.mvm;
                var vm = this;
                
                vm.send = send;
                vm.load = load;
                vm.getTemplateUrl = getTemplateUrl;
                vm.afterInclude = afterInclude;
                
                vm.forms = {};
                vm.request = { phone:null, message:null };
                vm.showHints = true;

                /**
                 * Отправка сообщения от клиента админам
                 */
                function send(){
                    vm.showHints = false;
                    if(vm.forms.contactForm.phone.$valid){
                        dataResources.contactCallback.post(vm.request, function(data){
                            $mdToast.show($rootScope.toast.textContent('Ваше сообщение успешно отправлено').theme('success'));
                            vm.showHints = true;
                        }, function(error){
                            $mdToast.show($rootScope.toast.textContent('Неудалось отправить сообщение').theme('error'));
                        });
                        vm.request = {phone:null,message:null};
                    }
                }

                /**
                 * Загрузка карты
                 */
                function load(){
                    var map;

                    DG.then(function () {
                        map = DG.map('map', {
                            center: [55.794430, 37.392815],
                            zoom: 15
                        });

                        DG.marker([55.794388, 37.392799]).addTo(map).bindPopup('Вы нашли нас!');
                    });

                }

                /**
                 * Получение шаблона
                 * @returns {string}
                 */
                function getTemplateUrl(){
                    if(mvm.width < 601){
                        return templatePath + "contact-sm.html";
                    }
                    if(mvm.width > 600){
                        if(mvm.width < 1025){
                            return templatePath + "contact-md.html"
                        }
                        return templatePath + "contact-lg.html";
                    }
                }

                /**
                 * События после загрузки шаблона
                 */
                function afterInclude(){
                    $timeout(function() {
                        load();
                    }, 500);
                }
        }])
})();