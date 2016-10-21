(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('QualityValueDialogController', QualityValueDialogController);

    QualityValueDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'QualityValue', 'QualityParameter'];

    function QualityValueDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, QualityValue, QualityParameter) {
        var vm = this;

        vm.qualityValue = entity;
        vm.clear = clear;
        vm.save = save;
        vm.qualityparameters = QualityParameter.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.qualityValue.id !== null) {
                QualityValue.update(vm.qualityValue, onSaveSuccess, onSaveError);
            } else {
                QualityValue.save(vm.qualityValue, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('grainAdminApp:qualityValueUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
