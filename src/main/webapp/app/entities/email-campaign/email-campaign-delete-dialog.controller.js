(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('EmailCampaignDeleteController',EmailCampaignDeleteController);

    EmailCampaignDeleteController.$inject = ['$uibModalInstance', 'entity', 'EmailCampaign'];

    function EmailCampaignDeleteController($uibModalInstance, entity, EmailCampaign) {
        var vm = this;

        vm.emailCampaign = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            EmailCampaign.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
