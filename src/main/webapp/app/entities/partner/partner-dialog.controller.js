(function () {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('PartnerDialogController', PartnerDialogController);

    PartnerDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Partner',
        'OrganisationType', 'RegionSearch', 'LocalitySearch', 'StationSearch', 'DistrictSearch', 'ServiceType',
        'PartnerSearch'
    ];

    function PartnerDialogController($timeout, $scope, $stateParams, $uibModalInstance, entity, Partner,
                                     OrganisationType, RegionSearch, LocalitySearch, StationSearch, DistrictSearch,
                                     ServiceType, PartnerSearch) {
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
        vm.getPartnersSuggestions = getPartnersSuggestions;
        vm.refreshStationSuggestions = refreshStationSuggestions;
        vm.refreshDistrictSuggestions = refreshDistrictSuggestions;
        vm.refreshRegionSuggestions = refreshRegionSuggestions;
        vm.refreshLocalitySuggestions = refreshLocalitySuggestions;
        vm.organisationtypes = OrganisationType.query();
        vm.districts = [];
        vm.regions = [];
        vm.localities = [];
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

        vm.doShowChildren = vm.partner.id !== null && vm.partner.ownedBies.length > 0 ? true : false; //just do not watch this formula
        vm.doShowParent = vm.partner.id !== null && vm.partner.ownerForId ? true : false; //just do not watch this formula
        vm.isAddChild = vm.children.length > 0 && vm.children[0].obj;
        vm.addChild = addChild;
        vm.onSelectChild = onSelectChild;
        vm.onSelectParent = onSelectParent;
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
            checkServicePricesAndDelete(partner);
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

        function checkServicePricesAndDelete(partner) {
            if (!partner.stationId) {
                partner.servicePrices = null;
            }
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

        function addChild() {
            vm.children.push({obj: null});
            vm.isAddChild = false;
        }

        function onSelectChild($model) {
            if ($model !== null) {
                vm.isAddChild = vm.children.length <= 1;
            }
        }

        function onSelectParent($model) {
            if ($model !== null) {
                vm.partner.ownerForId = $model.id;
            } else {
                vm.partner.ownerForId = null;
            }
        }

        function getPartnersSuggestions(partnerName) {
            return PartnerSearch.query({
                query: partnerName,
                page: 0,
                size: 20,
                sort: 'asc'
            }).$promise;
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

        function refreshDistrictSuggestions(term) {
            if (term) {
                vm.districts = DistrictSearch.query({query: term});
            }
            return null;
        }

        function refreshRegionSuggestions(term) {
            if (term) {
                vm.regions = RegionSearch.query({query: term});
            }
            return null;
        }

        function refreshLocalitySuggestions(term) {
            if (term) {
                vm.localities = LocalitySearch.query({query: term});
            }
            return null;
        }
    }
})();
