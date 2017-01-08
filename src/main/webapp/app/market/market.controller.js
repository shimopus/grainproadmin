(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('MarketController', MarketController);

    MarketController.$inject = ['$scope', '$state', 'StationSearch', 'PartnerCard', "$uibModal", "$http", "$sce"];

    function MarketController ($scope, $state, StationSearch, PartnerCard, $uibModal, $http, $sce) {
        var LOADING_STR = "Загрузка...";
        var vm = this;
        vm.refreshStationSuggestions = refreshStationSuggestions;
        vm.stations = [];
        vm.station = null;
        vm.onSelectStation = onSelectStation;
        vm.showCardDialog = showCardDialog;
        vm.splitFirstLetter = splitFirstLetter;
        vm.currentDate = new Date();
        vm.exportToHTML = exportToHTML;
        vm.marketTable = LOADING_STR;
        loadTableData();

        function refreshStationSuggestions(term) {
            if (term) {
                vm.stations = StationSearch.query({query: term});
            }
            return null;
        }

        function onSelectStation() {
            if (vm.station && vm.station.code) {
                vm.marketTable = LOADING_STR;
                loadTableData(vm.station.code);
            } else {
                vm.marketTable = LOADING_STR;
                loadTableData();
            }
        }

        function showCardDialog(partner) {
            PartnerCard.showDialog(partner);
        }

        function splitFirstLetter(str) {
            if (!str || str.length < 1) return ["", ""];
            return [
                str.substring(0, 1),
                str.substring(1, str.length)
            ]
        }

        function loadTableData(code) {
            return $http(
                {
                    url: '/pages/market-table',
                    method: "GET",
                    params: {'code': code}
                }
            ).then(function(response) {
                vm.marketTable = $sce.trustAsHtml(response.data);
            });
        }

        function exportToHTML() {
            /*alert(angular.element("#market").html());
            $uibModal.open({
                templateUrl: 'app/entities/bid/bid-quality-passport.html',
                controller: 'BidQualityPassportController',
                controllerAs: 'vm',
                backdrop: 'static',
                size: 'lg',
                resolve: {
                    entity: ['Bid', function (Bid) {
                        return Bid.get({id: $stateParams.bidId}).$promise;
                    }]
                }
            }).result.then(function () {
                    $state.go('^', {}, {reload: false});
                }, function () {
                    $state.go('^');
                });*/
        }
    }
})();
