(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('QualityValueDeleteController',QualityValueDeleteController);

    QualityValueDeleteController.$inject = ['$uibModalInstance', 'entity', 'QualityValue'];

    function QualityValueDeleteController($uibModalInstance, entity, QualityValue) {
        var vm = this;

        vm.qualityValue = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            QualityValue.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
