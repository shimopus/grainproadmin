(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('TrackingDeleteController',TrackingDeleteController);

    TrackingDeleteController.$inject = ['$uibModalInstance', 'entity', 'Tracking'];

    function TrackingDeleteController($uibModalInstance, entity, Tracking) {
        var vm = this;

        vm.tracking = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Tracking.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
