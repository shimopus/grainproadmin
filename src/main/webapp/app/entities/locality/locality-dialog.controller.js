(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('LocalityDialogController', LocalityDialogController);

    LocalityDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Locality'];

    function LocalityDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Locality) {
        var vm = this;

        vm.locality = entity;
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
            if (vm.locality.id !== null) {
                Locality.update(vm.locality, onSaveSuccess, onSaveError);
            } else {
                Locality.save(vm.locality, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('grainAdminApp:localityUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
