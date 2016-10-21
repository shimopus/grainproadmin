(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('DistrictDeleteController',DistrictDeleteController);

    DistrictDeleteController.$inject = ['$uibModalInstance', 'entity', 'District'];

    function DistrictDeleteController($uibModalInstance, entity, District) {
        var vm = this;

        vm.district = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            District.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
