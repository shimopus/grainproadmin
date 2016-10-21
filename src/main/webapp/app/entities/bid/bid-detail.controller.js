(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('BidDetailController', BidDetailController);

    BidDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Bid', 'Contact', 'QualityValue', 'Partner'];

    function BidDetailController($scope, $rootScope, $stateParams, previousState, entity, Bid, Contact, QualityValue, Partner) {
        var vm = this;

        vm.bid = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('grainAdminApp:bidUpdate', function(event, result) {
            vm.bid = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
