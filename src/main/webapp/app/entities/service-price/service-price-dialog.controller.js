(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('ServicePriceDialogController', ServicePriceDialogController);

    ServicePriceDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ServicePrice', 'ServiceType'];

    function ServicePriceDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ServicePrice, ServiceType) {
        var vm = this;

        vm.servicePrice = entity;
        vm.clear = clear;
        vm.save = save;
        vm.servicetypes = ServiceType.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.servicePrice.id !== null) {
                ServicePrice.update(vm.servicePrice, onSaveSuccess, onSaveError);
            } else {
                ServicePrice.save(vm.servicePrice, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('grainAdminApp:servicePriceUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
