(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('BidArchiveController', BidArchiveController);

    BidArchiveController.$inject = ['$uibModalInstance', 'entity', 'Bid', '$scope'];

    function BidArchiveController($uibModalInstance, entity, Bid, $scope) {
        var vm = this;

        vm.bid = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (bid) {
            bid.archiveDate = new Date();

            Bid.update(bid, function (bid) {
                bid.archiveDate = new Date();
                $scope.$emit('grainAdminApp:bidArchived', bid);
                $uibModalInstance.close(true);
            });
        }
    }
})();
