(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('BidQualityPassportController',BidQualityPassportController);

    BidQualityPassportController.$inject = ['$uibModalInstance', 'entity', 'Bid'];

    function BidQualityPassportController($uibModalInstance, entity, Bid) {
        var vm = this;

        vm.bid = entity;
        vm.active = 0;
        vm.clear = clear;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();
