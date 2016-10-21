(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('StationDeleteController',StationDeleteController);

    StationDeleteController.$inject = ['$uibModalInstance', 'entity', 'Station'];

    function StationDeleteController($uibModalInstance, entity, Station) {
        var vm = this;

        vm.station = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Station.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
