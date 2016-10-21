(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('OrganisationTypeDeleteController',OrganisationTypeDeleteController);

    OrganisationTypeDeleteController.$inject = ['$uibModalInstance', 'entity', 'OrganisationType'];

    function OrganisationTypeDeleteController($uibModalInstance, entity, OrganisationType) {
        var vm = this;

        vm.organisationType = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            OrganisationType.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
