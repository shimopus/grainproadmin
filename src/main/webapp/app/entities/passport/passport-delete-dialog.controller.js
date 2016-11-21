(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('PassportDeleteController',PassportDeleteController);

    PassportDeleteController.$inject = ['$uibModalInstance', 'entity', 'Passport'];

    function PassportDeleteController($uibModalInstance, entity, Passport) {
        var vm = this;

        vm.passport = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Passport.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
