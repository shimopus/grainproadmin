(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('TransportationPriceDeleteController',TransportationPriceDeleteController);

    TransportationPriceDeleteController.$inject = ['$uibModalInstance', 'entity', 'TransportationPrice'];

    function TransportationPriceDeleteController($uibModalInstance, entity, TransportationPrice) {
        var vm = this;

        vm.transportationPrice = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            TransportationPrice.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
