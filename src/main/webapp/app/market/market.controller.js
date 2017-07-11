(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('MarketController', MarketController);

    MarketController.$inject = ['$scope', '$state', 'StationSearch', 'PartnerCard', "$http", "$sce", "$filter"];

    function MarketController ($scope, $state, StationSearch, PartnerCard, $http, $sce, $filter) {
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
        vm.currentBidType = 'SELL';
        vm.changeBidType = changeBidType;
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

        function changeBidType(bidType) {
            vm.currentBidType = bidType;
            onSelectStation();
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
                    url: '/pages/market-table/admin',
                    method: "GET",
                    params: {'code': code,
                            'bidType': vm.currentBidType}
                }
            ).then(function(response) {
                vm.marketTable = $sce.trustAsHtml(response.data);
            });
        }

        function exportToHTML(code) {
            $http(
                {
                    url: '/pages/market-table/download',
                    method: "GET",
                    params: {'code': code,
                        'bidType': vm.currentBidType}
                }
            )
                .success(function(data, status, headers, config) {
                    var anchor = angular.element('<a/>');
                    anchor.css({display: 'none'}); // Make sure it's not visible
                    angular.element(document.body).append(anchor); // Attach to document

                    anchor.attr({
                        href: 'data:attachment/html;charset=utf-8,' + encodeURI(data),
                        download: $filter('date')(new Date(), "yyyyMMdd") +
                        " пшеница" +
                        (vm.currentBidType === 'SELL' ?
                            " продавцы" :
                            " покупатели") +
                        (vm.station ?
                            " " + vm.station.name +
                            " (" + vm.station.code + ")"
                            : "") +
                        ".html"
                    })[0].click();

                    anchor.remove(); // Clean it up afterwards
                });
        }
    }
})();
