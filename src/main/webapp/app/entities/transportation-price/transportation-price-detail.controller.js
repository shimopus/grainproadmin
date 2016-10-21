(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('TransportationPriceDetailController', TransportationPriceDetailController);

    TransportationPriceDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'TransportationPrice', 'Station'];

    function TransportationPriceDetailController($scope, $rootScope, $stateParams, previousState, entity, TransportationPrice, Station) {
        var vm = this;

        vm.transportationPrice = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('grainAdminApp:transportationPriceUpdate', function(event, result) {
            vm.transportationPrice = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
