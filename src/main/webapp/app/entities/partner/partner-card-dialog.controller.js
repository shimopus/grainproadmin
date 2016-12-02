(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('PartnerCardDialogController', PartnerCardDialogController);

    PartnerCardDialogController.$inject = ['$uibModalInstance', 'partner'];

    function PartnerCardDialogController($uibModalInstance, partner) {
        var vm = this;

        vm.partner = partner;
        vm.lines = splitCard();
        vm.clear = clear;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function splitCard() {
            if (vm.partner.card) {
                return vm.partner.card.split("\n");
            }

            return ["Нет информации"];
        }
    }
})();
