(function(){
    angular.module('contact',[]);
})();

(function() {
    'use strict';

    angular.module('contact')

        .controller('contactController',['$scope','dataResources','$timeout', 
            function($scope,dataResources, $timeout){

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
                    if(vm.forms.contactForm.phone.$valid){
                        console.log(" тут должна быть отправка сообщения ")
                        vm.showHints = true;
                    } else {
                        vm.showHints = false;
                    }
                //    $scope.showHints = false;
                //} else {
                //    dataResources.contactCallback.post($scope.request,
                //        function(response){
                //            console.log("success");
                //        },
                //        function(response){
                //            console.log("failed");
                //        });
                //    $scope.request = {phone:null,message:null};
                //    $scope.showHints = true;
                //}
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
                    }, 300);
                }
        }])
})();