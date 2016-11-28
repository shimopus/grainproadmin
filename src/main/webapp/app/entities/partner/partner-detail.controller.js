(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('PartnerDetailController', PartnerDetailController);

    PartnerDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Partner',
        'Bid', 'OrganisationType', 'District', 'Region', 'Locality', 'Station', 'Contact', 'ServicePrice', '$http',
        '$timeout'];

    function PartnerDetailController($scope, $rootScope, $stateParams, previousState, entity, Partner,
                                     Bid, OrganisationType, District, Region, Locality, Station, Contact, ServicePrice,
                                     $http, $timeout) {
        var vm = this;

        vm.partner = entity;
        vm.previousState = previousState.name;
        vm.isPartnerDetailsOpened = false;
        vm.isArrowClicked = false;
        vm.notArchivedBids = getBids(false);
        vm.archivedBids = getBids(true);
        vm.getContact = getContact;
        vm.arrowClick = arrowClick;
        vm.clickOutside = clickOutside;
        vm.openCard = openCard;

        var unsubscribe = $rootScope.$on('grainAdminApp:partnerUpdate', function(event, result) {
            vm.partner = result;
        });
        $scope.$on('$destroy', unsubscribe);

        var unsubscribeBid = $rootScope.$on('grainAdminApp:bidUpdate', function(event, result) {
            vm.notArchivedBids = getBids(false);
            vm.archivedBids = getBids(true);
        });
        $scope.$on('$destroy', unsubscribeBid);

        function getBids(isArchived) {
            return Bid.queryByPartner({
                partnerId: vm.partner.id,
                isArchived: isArchived || false
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

            vm.notArchivedBids = vm.notArchivedBids.filter(function (bidItem) {
                if (bidItem.id === bid.id) {
                    archivedBid = bidItem;
                    return false;
                }
                return true;
            });

            //delay for remove animation
            $timeout(function () {
                vm.archivedBids.unshift(archivedBid);
            }, 1000);
        }
    }
})();
