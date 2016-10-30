(function () {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('PartnerDialogController', PartnerDialogController);

    PartnerDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Partner',
        'OrganisationType', 'District', 'Region', 'Locality', 'Station', 'Contact', 'ServicePrice', 'ServiceType',
        '$q'
    ];

    function PartnerDialogController($timeout, $scope, $stateParams, $uibModalInstance, entity, Partner,
                                     OrganisationType, District, Region, Locality, Station,
                                     Contact, ServicePrice, ServiceType, $q) {
        var vm = this;

        vm.partner = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        //vm.bids = Bid.query();
        vm.partners = Partner.query();
        vm.getPartnersSuggestions = getPartnersSuggestions;
        vm.organisationtypes = OrganisationType.query();
        vm.districts = District.query();
        vm.regions = Region.query();
        vm.localities = Locality.query();
        vm.stations = Station.query();
        vm.contacts = Contact.query();
        vm.servicePriceTypes = ServiceType.query();
        vm.formatSelection = formatSelection;
        vm.isAddChild = false;
        vm.selectedChild = null;
        vm.addChild = addChild;
        vm.cancelAddChild = cancelAddChild;
        vm.isAddServicePrice = false;
        vm.selectedServicePriceType = null;
        vm.selectedservicePriceValue = null;
        vm.addServicePrice = addServicePrice;
        vm.cancelAddServicePrice = cancelAddServicePrice;

        $timeout(function () {
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear() {
            $uibModalInstance.dismiss('cancel');
        }

        function save() {
            vm.isSaving = true;
            updateAllRelatedServicePricesOnUpdate(vm.partner).then(function(partner){
                partner.lastUpdate = new Date();
                if (partner.id !== null) {
                    updateAllRelatedPartnersOnUpdate(partner);
                    Partner.update(partner, onSaveSuccess, onSaveError);
                } else {
                    Partner.save(partner, onSaveSuccess, onSaveError);
                }
            });
        }

        function updateAllRelatedPartnersOnUpdate(partner) {
            //if any child has ben added/removed
            partner.ownedBies.forEach(function (child) {
                var previousOwnerForId = child.ownerForId;
                if (child.ownerForId !== partner.id) {
                    child.ownerForId = partner.id;
                    Partner.update(child);
                }
            });
        }

        function updateAllRelatedServicePricesOnUpdate(partner) {
            var returnDeferred = $q.defer();
            var promises = [];
            var returnPartner = angular.copy(partner);
            returnPartner.servicePrices = [];
            partner.servicePrices.forEach(function (servicePrice) {
                if (!servicePrice.id) {
                    promises.push(
                        ServicePrice.save(servicePrice, function(updatedServicePrice) {
                            returnPartner.servicePrices.push(updatedServicePrice);
                        }).$promise
                    );
                } else {
                    returnPartner.servicePrices.push(servicePrice);
                }
            });

            $q.all(promises).then(function(){
               returnDeferred.resolve(returnPartner);
            }, function(reason) {
                returnDeferred.reject(reason);
            });

            return returnDeferred.promise;
        }

        function onSaveSuccess(result) {
            $scope.$emit('grainAdminApp:partnerUpdate', result);
            updateAllRelatedPartnersOnUpdate(result);

            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError() {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.lastUpdate = false;

        function openCalendar(date) {
            vm.datePickerOpenStatus[date] = true;
        }

        function formatSelection(selectedValue, objects, parameterName) {
            if (!selectedValue) return "";

            return objects.find(function (object) {
                return object.id === selectedValue;
            })[parameterName];
        }

        function addChild() {
            if (vm.selectedChild !== null) {
                vm.partner.ownedBies.push(vm.selectedChild);
                vm.isAddChild = false;
                vm.selectedChild = null;
            }
        }

        function cancelAddChild() {
            vm.selectedChild = null;
            vm.isAddChild = false;
        }

        function getPartnersSuggestions() {
            return vm.partners.filter(function (partner) {
                return partner.id !== vm.partner.id &&
                    partner.id !== vm.partner.ownerForId &&
                    vm.partner.ownedBies.filter(
                        function (child) {
                            return child.id === partner.id;
                        }
                    ).length <= 0;
            });
        }

        function addServicePrice() {
            if (vm.selectedServicePriceType != null && vm.selectedservicePriceValue != null) {
                vm.partner.servicePrices.push(
                    {
                        serviceTypeId: angular.copy(vm.selectedServicePriceType.id),
                        serviceTypeName: angular.copy(vm.selectedServicePriceType.name),
                        price: angular.copy(vm.selectedservicePriceValue)
                    }
                );
                vm.isAddServicePrice = false;
                vm.selectedServicePriceType = null;
                vm.selectedservicePriceValue = null;
            }
        }

        function cancelAddServicePrice() {
            vm.selectedServicePriceType = null;
            vm.selectedservicePriceValue = null;
            vm.isAddServicePrice = false;
        }
    }
})();
