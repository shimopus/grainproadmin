(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('SubscriptionConfigDeleteController',SubscriptionConfigDeleteController);

    SubscriptionConfigDeleteController.$inject = ['$uibModalInstance', 'entity', 'SubscriptionConfig'];

    function SubscriptionConfigDeleteController($uibModalInstance, entity, SubscriptionConfig) {
        var vm = this;

        vm.subscriptionConfig = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            SubscriptionConfig.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
