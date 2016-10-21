(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('LocalityDeleteController',LocalityDeleteController);

    LocalityDeleteController.$inject = ['$uibModalInstance', 'entity', 'Locality'];

    function LocalityDeleteController($uibModalInstance, entity, Locality) {
        var vm = this;

        vm.locality = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Locality.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
