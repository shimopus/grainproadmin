(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('OrganisationTypeDialogController', OrganisationTypeDialogController);

    OrganisationTypeDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'OrganisationType'];

    function OrganisationTypeDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, OrganisationType) {
        var vm = this;

        vm.organisationType = entity;
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
            if (vm.organisationType.id !== null) {
                OrganisationType.update(vm.organisationType, onSaveSuccess, onSaveError);
            } else {
                OrganisationType.save(vm.organisationType, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('grainAdminApp:organisationTypeUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
