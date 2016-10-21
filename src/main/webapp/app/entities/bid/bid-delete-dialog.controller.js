(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('BidDeleteController',BidDeleteController);

    BidDeleteController.$inject = ['$uibModalInstance', 'entity', 'Bid'];

    function BidDeleteController($uibModalInstance, entity, Bid) {
        var vm = this;

        vm.bid = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Bid.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
