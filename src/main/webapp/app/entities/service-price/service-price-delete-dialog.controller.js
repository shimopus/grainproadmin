(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('ServicePriceDeleteController',ServicePriceDeleteController);

    ServicePriceDeleteController.$inject = ['$uibModalInstance', 'entity', 'ServicePrice'];

    function ServicePriceDeleteController($uibModalInstance, entity, ServicePrice) {
        var vm = this;

        vm.servicePrice = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ServicePrice.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
