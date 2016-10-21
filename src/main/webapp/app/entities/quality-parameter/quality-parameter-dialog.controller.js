(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('QualityParameterDialogController', QualityParameterDialogController);

    QualityParameterDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'QualityParameter'];

    function QualityParameterDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, QualityParameter) {
        var vm = this;

        vm.qualityParameter = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.qualityParameter.id !== null) {
                QualityParameter.update(vm.qualityParameter, onSaveSuccess, onSaveError);
            } else {
                QualityParameter.save(vm.qualityParameter, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('grainAdminApp:qualityParameterUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
