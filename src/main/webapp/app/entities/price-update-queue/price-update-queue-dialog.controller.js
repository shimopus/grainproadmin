(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('PriceUpdateQueueDialogController', PriceUpdateQueueDialogController);

    PriceUpdateQueueDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'PriceUpdateQueue', 'Station'];

    function PriceUpdateQueueDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, PriceUpdateQueue, Station) {
        var vm = this;

        vm.priceUpdateQueue = entity;
        vm.clear = clear;
        vm.save = save;
        vm.stations = Station.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.priceUpdateQueue.id !== null) {
                PriceUpdateQueue.update(vm.priceUpdateQueue, onSaveSuccess, onSaveError);
            } else {
                PriceUpdateQueue.save(vm.priceUpdateQueue, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('grainAdminApp:priceUpdateQueueUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
