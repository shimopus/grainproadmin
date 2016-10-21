(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('QualityParameterDeleteController',QualityParameterDeleteController);

    QualityParameterDeleteController.$inject = ['$uibModalInstance', 'entity', 'QualityParameter'];

    function QualityParameterDeleteController($uibModalInstance, entity, QualityParameter) {
        var vm = this;

        vm.qualityParameter = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            QualityParameter.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
