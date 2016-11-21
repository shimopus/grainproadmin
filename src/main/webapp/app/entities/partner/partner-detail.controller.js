(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('PartnerDetailController', PartnerDetailController);

    PartnerDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Partner',
        'Bid', 'OrganisationType', 'District', 'Region', 'Locality', 'Station', 'Contact', 'ServicePrice', '$http'];

    function PartnerDetailController($scope, $rootScope, $stateParams, previousState, entity, Partner,
                                     Bid, OrganisationType, District, Region, Locality, Station, Contact, ServicePrice, $http) {
        var vm = this;

        vm.partner = entity;
        vm.previousState = previousState.name;
        vm.isPartnerDetailsOpened = false;
        vm.isArrowClicked = false;
        vm.bids = getBids();
        vm.getContact = getContact;
        vm.arrowClick = arrowClick;
        vm.clickOutside = clickOutside;
        vm.openCard = openCard;

        var unsubscribe = $rootScope.$on('grainAdminApp:partnerUpdate', function(event, result) {
            vm.partner = result;
        });
        $scope.$on('$destroy', unsubscribe);

        var unsubscribeBid = $rootScope.$on('grainAdminApp:bidUpdate', function(event, result) {
            vm.bids = getBids();
        });
        $scope.$on('$destroy', unsubscribeBid);

        function getBids() {
            return Bid.queryByPartner({
                partnerId: vm.partner.id
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
    }
})();
