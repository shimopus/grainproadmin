(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('EmailCampaignDialogController', EmailCampaignDialogController);

    EmailCampaignDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'EmailCampaign'];

    function EmailCampaignDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, EmailCampaign) {
        var vm = this;

        vm.emailCampaign = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.emailCampaign.id !== null) {
                EmailCampaign.update(vm.emailCampaign, onSaveSuccess, onSaveError);
            } else {
                EmailCampaign.save(vm.emailCampaign, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('grainAdminApp:emailCampaignUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.date = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
