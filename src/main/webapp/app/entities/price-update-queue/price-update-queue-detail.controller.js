(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('PriceUpdateQueueDetailController', PriceUpdateQueueDetailController);

    PriceUpdateQueueDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'PriceUpdateQueue', 'Station'];

    function PriceUpdateQueueDetailController($scope, $rootScope, $stateParams, previousState, entity, PriceUpdateQueue, Station) {
        var vm = this;

        vm.priceUpdateQueue = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('grainAdminApp:priceUpdateQueueUpdate', function(event, result) {
            vm.priceUpdateQueue = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
