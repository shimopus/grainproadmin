(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('ServiceTypeDialogController', ServiceTypeDialogController);

    ServiceTypeDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ServiceType'];

    function ServiceTypeDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ServiceType) {
        var vm = this;

        vm.serviceType = entity;
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
            if (vm.serviceType.id !== null) {
                ServiceType.update(vm.serviceType, onSaveSuccess, onSaveError);
            } else {
                ServiceType.save(vm.serviceType, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('grainAdminApp:serviceTypeUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
