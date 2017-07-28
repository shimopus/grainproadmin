(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('PartnerDetailController', PartnerDetailController);

    PartnerDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Partner',
        'Bid', 'OrganisationType', 'District', 'Region', 'Locality', 'Station', 'Contact', 'ServicePrice', '$http',
        '$timeout', 'pagingParamsBuy', 'pagingParamsSell'];

    function PartnerDetailController($scope, $rootScope, $stateParams, previousState, entity, Partner,
                                     Bid, OrganisationType, District, Region, Locality, Station, Contact, ServicePrice,
                                     $http, $timeout, pagingParamsBuy, pagingParamsSell) {
        var vm = this;

        vm.partner = entity;
        vm.previousState = previousState.name;
        vm.isPartnerDetailsOpened = false;
        vm.isArrowClicked = false;

        vm.predicateBuy = pagingParamsBuy.predicate;
        vm.reverseBuy = pagingParamsBuy.ascending;
        vm.transitionBuy = transitionBuy;

        vm.predicateSell = pagingParamsSell.predicate;
        vm.reverseSell = pagingParamsSell.ascending;
        vm.transitionSell = transitionSell;


        vm.notArchivedBidsSell = getBids(false, 'SELL', sortSell());
        vm.notArchivedBidsBuy = getBids(false, 'BUY', sortBuy());
        vm.archivedBids = getBids(true);
        vm.getContact = getContact;
        vm.arrowClick = arrowClick;
        vm.clickOutside = clickOutside;
        vm.openCard = openCard;

        var unsubscribe = $rootScope.$on('grainAdminApp:partnerUpdate', function(event, result) {
            vm.partner = result;
        });
        $scope.$on('$destroy', unsubscribe);

        var unsubscribeBid = $rootScope.$on('grainAdminApp:bidUpdate', function(event, data) {
            if (data.initialBid) { //if update
                vm.notArchivedBidsSell.some(function (bid, index) {
                    updateBidIfEquals(bid, index, vm.notArchivedBidsSell, data.newBid, data.initialBid);
                });
                vm.notArchivedBidsBuy.some(function (bid, index) {
                    updateBidIfEquals(bid, index, vm.notArchivedBidsBuy, data.newBid, data.initialBid);
                });
                vm.archivedBids.unshift(data.initialBid)
            } else { //Create
                var bid = data.newBid;
                if (bid.bidType === "SELL") {
                    vm.notArchivedBidsSell.unshift(bid);
                } else {
                    vm.notArchivedBidsBuy.unshift(bid);
                }
            }
        });

        $scope.$on('$destroy', unsubscribeBid);

        function updateBidIfEquals (bidItem, index, array, newBid, initialBid) {
            if (bidItem.id === initialBid.id) {
                array[index] = newBid;
                return true;
            }
        }

        function getBids(isArchived, bidType, sort) {
            return Bid.queryByPartner({
                partnerId: vm.partner.id,
                bidType: bidType,
                isArchived: isArchived || false,
                sort: sort
            });
        }

        function getContact(bid) {
            if (vm.partner.contacts.length > 0) {
                return vm.partner.contacts.find(function (contact) {
                    return contact.id === bid.agentContactId;
                });
            }
        }

        function arrowClick() {
            vm.isPartnerDetailsOpened = !vm.isPartnerDetailsOpened;
            vm.isArrowClicked = true;
        }

        function openCard() {
            if (!vm.isArrowClicked) {
                vm.isPartnerDetailsOpened = true;
            }
            vm.isArrowClicked = false;
        }

        function clickOutside() {
            if (!vm.isArrowClicked) {
                vm.isPartnerDetailsOpened = false;
            }
            vm.isArrowClicked = false;
        }

        var unsubscribeArchive = $rootScope.$on('grainAdminApp:bidArchived', archiveBid);
        $scope.$on('$destroy', unsubscribeArchive);

        function archiveBid(event, bid) {
            var archivedBid;

            var bidsToFilter = bid.bidType === 'SELL' ? vm.notArchivedBidsSell : vm.notArchivedBidsBuy;

            bidsToFilter.filter(function (bidItem, index) {
                if (bidItem.id === bid.id) {
                    archivedBid = bidItem;
                    bidsToFilter = bidsToFilter.splice(index, 1);
                    return false;
                }
                return true;
            });

            //delay for remove animation
            $timeout(function () {
                vm.archivedBids.unshift(archivedBid);
            }, 1000);
        }

        function transitionBuy() {
            vm.notArchivedBidsBuy = getBids(false, 'BUY', sortBuy());
        }

        function sortBuy() {
            return [vm.predicateBuy + ',' + (vm.reverseBuy ? 'asc' : 'desc')];
        }


        function transitionSell() {
            vm.notArchivedBidsSell = getBids(false, 'SELL', sortSell());
        }

        function sortSell() {
            return [vm.predicateSell + ',' + (vm.reverseSell ? 'asc' : 'desc')];
        }
    }
})();
