(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('PriceUpdateQueueDeleteController',PriceUpdateQueueDeleteController);

    PriceUpdateQueueDeleteController.$inject = ['$uibModalInstance', 'entity', 'PriceUpdateQueue'];

    function PriceUpdateQueueDeleteController($uibModalInstance, entity, PriceUpdateQueue) {
        var vm = this;

        vm.priceUpdateQueue = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            PriceUpdateQueue.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
