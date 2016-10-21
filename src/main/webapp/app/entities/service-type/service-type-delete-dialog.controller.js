(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('ServiceTypeDeleteController',ServiceTypeDeleteController);

    ServiceTypeDeleteController.$inject = ['$uibModalInstance', 'entity', 'ServiceType'];

    function ServiceTypeDeleteController($uibModalInstance, entity, ServiceType) {
        var vm = this;

        vm.serviceType = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ServiceType.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
