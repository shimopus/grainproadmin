(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('MarketController', MarketController);

    MarketController.$inject = ['$scope', '$state', 'bids', 'StationSearch', 'Market'];

    function MarketController ($scope, $state, bids, StationSearch, Market) {
        var vm = this;
        vm.bids = bids;
        vm.getFinalPrice = getFinalPrice;
        vm.refreshStationSuggestions = refreshStationSuggestions;
        vm.stations = [];
        vm.station = null;
        vm.onSelectStation = onSelectStation;

        function getFinalPrice(bid) {
            var price = parseFloat(bid.price);
            var loadPrice = parseFloat(bid.elevator.servicePrices[0].price);
            var transpPrice = parseFloat(bid.transportationPricePrice);

            var result = 0;

            if (price) {
                result += price;
            }

            if (loadPrice) {
                result += loadPrice;
            }

            if (transpPrice) {
                result += transpPrice;
            }

            return result;
        }

        function refreshStationSuggestions(term) {
            if (term) {
                vm.stations = StationSearch.query({query: term});
            }
            return null;
        }

        function onSelectStation() {
            if (vm.station.code) {
                vm.bids = Market.query({code: vm.station.code});
            }
        }
    }
})();
