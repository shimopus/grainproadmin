(function () {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('PartnerDialogController', PartnerDialogController);

    PartnerDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Partner',
        'OrganisationType', 'District', 'Region', 'Locality', 'StationSearch', 'Contact', 'ServiceType'
    ];

    function PartnerDialogController($timeout, $scope, $stateParams, $uibModalInstance, entity, Partner,
                                     OrganisationType, District, Region, Locality, StationSearch,
                                     Contact, ServiceType) {
        var vm = this;

        vm.partner = entity;

        //initialize partner
        vm.emptyContact = {
            personName: null,
            status: null,
            phone: null,
            skype: null,
            emailEmail: null,
            emailId: null
        };

        if (vm.partner.contacts === null || vm.partner.contacts.length === 0) {
            vm.partner.contacts.push(angular.copy(vm.emptyContact));
        }

        //if it is a new partner - just create new child
        vm.children = !vm.partner.ownedBies || vm.partner.ownedBies.length === 0
            ? [{obj: null}]
            : vm.partner.ownedBies.map(function (child) {
                return {
                    obj: child
                };
            });
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.partners = Partner.query();
        vm.getPartnersSuggestions = getPartnersSuggestions;
        vm.refreshStationSuggestions = refreshStationSuggestions;
        vm.organisationtypes = OrganisationType.query();
        vm.districts = District.query();
        vm.regions = Region.query();
        vm.localities = Locality.query();
        vm.contacts = Contact.query();
        vm.stations = [];
        vm.servicePriceTypes = ServiceType.query();

        vm.servicePriceTypes.$promise.then(function (serviceTypes) {
            if (vm.partner && vm.partner.servicePrices && !vm.partner.servicePrices[0] && serviceTypes && serviceTypes[0]) {
                vm.partner.servicePrices.push({
                    id: null,
                    price: null,
                    serviceTypeId: serviceTypes[0].id
                });
            }
        });

        vm.formatSelection = formatSelection;
        vm.doShowChildren = vm.partner.id !== null && vm.partner.ownedBies.length > 0 ? true : false; //just do not watch this formula
        vm.doShowParent = vm.partner.id !== null && vm.partner.ownerForId ? true : false; //just do not watch this formula
        vm.isAddChild = vm.children.length > 0 && vm.children[0].obj;
        vm.addChild = addChild;
        vm.onSelectChild = onSelectChild;
        vm.isAddServicePrice = false;
        vm.selectedServicePriceType = null;
        vm.selectedservicePriceValue = null;
        vm.addServicePrice = addServicePrice;
        vm.cancelAddServicePrice = cancelAddServicePrice;
        vm.addContact = addContact;
        vm.deleteContact = deleteContact;
        vm.selectedNDS = {
            "INCLUDED": vm.partner.nds === 'INCLUDED' || vm.partner.nds === 'BOTH',
            'EXCLUDED': vm.partner.nds === 'EXCLUDED' || vm.partner.nds === 'BOTH'
        };
        vm.partnerPostUpdateNeeded = false;

        $timeout(function () {
            angular.element('.form-group:eq(0)>input').focus();
        });

        function clear() {
            $uibModalInstance.dismiss('cancel');
        }

        function save() {
            vm.isSaving = true;
            var partner = vm.partner;
            partner.lastUpdate = new Date();
            partner.nds = vm.selectedNDS.INCLUDED ? (vm.selectedNDS.EXCLUDED ? 'BOTH' : 'INCLUDED') : 'EXCLUDED';
            if (partner.id !== null) {
                updateAllRelatedPartnersOnUpdate(partner);
                Partner.update(partner, onSaveSuccess, onSaveError);
            } else {
                vm.partnerPostUpdateNeeded = true;
                Partner.save(partner, onSaveSuccess, onSaveError);
            }
        }

        function updateAllRelatedPartnersOnUpdate(partner) {
            //if any child has ben added/removed
            vm.children.forEach(function (child, index) {
                if (!child.obj) {
                    child = partner.ownedBies[index];

                    //if this is new undefined field
                    if (!child) return;

                    child.ownerForId = null;
                    Partner.update(child);
                } else {
                    if (child.obj.ownerForId !== partner.id) {
                        child.obj.ownerForId = partner.id;
                        Partner.update(child.obj);
                    }
                }
            });
        }

        function onSaveSuccess(result) {
            $scope.$emit('grainAdminApp:partnerUpdate', result);
            if (vm.partnerPostUpdateNeeded) {
                updateAllRelatedPartnersOnUpdate(result);
            }

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
            vm.children.push({obj: null});
            vm.isAddChild = false;
        }

        function onSelectChild($model) {
            if ($model !== null) {
                vm.isAddChild = vm.children.length <= 1;
            }
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
            if (vm.selectedServicePriceType !== null && vm.selectedservicePriceValue !== null) {
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

        function addContact() {
            vm.partner.contacts.push(angular.copy(vm.emptyContact));
        }

        function deleteContact() {
            vm.partner.contacts.pop();
        }

        function refreshStationSuggestions(term) {
            if (term) {
                vm.stations = StationSearch.query({query: term});
            }
            return null;
        }
    }
})();
