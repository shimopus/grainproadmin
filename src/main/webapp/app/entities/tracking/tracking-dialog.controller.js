(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('TrackingDialogController', TrackingDialogController);

    TrackingDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Tracking', 'Partner'];

    function TrackingDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Tracking, Partner) {
        var vm = this;

        vm.tracking = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.partners = Partner.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.tracking.id !== null) {
                Tracking.update(vm.tracking, onSaveSuccess, onSaveError);
            } else {
                Tracking.save(vm.tracking, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('grainAdminApp:trackingUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.mailDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
