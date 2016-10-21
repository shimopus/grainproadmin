(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('ServicePriceDetailController', ServicePriceDetailController);

    ServicePriceDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ServicePrice', 'ServiceType'];

    function ServicePriceDetailController($scope, $rootScope, $stateParams, previousState, entity, ServicePrice, ServiceType) {
        var vm = this;

        vm.servicePrice = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('grainAdminApp:servicePriceUpdate', function(event, result) {
            vm.servicePrice = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
