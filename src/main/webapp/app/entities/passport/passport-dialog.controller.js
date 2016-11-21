(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('PassportDialogController', PassportDialogController);

    PassportDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Passport'];

    function PassportDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Passport) {
        var vm = this;

        vm.passport = entity;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.passport.id !== null) {
                Passport.update(vm.passport, onSaveSuccess, onSaveError);
            } else {
                Passport.save(vm.passport, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('grainAdminApp:passportUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


        vm.setImage = function ($file, passport) {
            if ($file && $file.$error === 'pattern') {
                return;
            }
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        passport.image = base64Data;
                        passport.imageContentType = $file.type;
                    });
                });
            }
        };

    }
})();
