(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('MarketController', MarketController);

    MarketController.$inject = ['$scope', '$state', 'bids'];

    function MarketController ($scope, $state, bids) {
        var vm = this;
        vm.bids = bids;
        vm.getFinalPrice = getFinalPrice;

        function getFinalPrice(bid) {
            var price = parseFloat(bid.price);
            var loadPrice = parseFloat(bid.servicePrices[0].price);
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
    }
})();
