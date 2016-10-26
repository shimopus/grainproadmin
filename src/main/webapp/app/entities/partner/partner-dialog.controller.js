(function () {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('PartnerDialogController', PartnerDialogController);

    PartnerDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Partner',
        'Bid', 'OrganisationType', 'District', 'Region', 'Locality', 'Station', 'Contact', 'ServicePrice', "$q"
    ];

    function PartnerDialogController($timeout, $scope, $stateParams, $uibModalInstance, entity, Partner,
                                     Bid, OrganisationType, District, Region, Locality, Station,
                                     Contact, ServicePrice, $q) {
        var vm = this;

        vm.partner = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.bids = Bid.query();
        vm.partners = Partner.query();
        vm.getPartnersSuggestions = getPartnersSuggestions;
        vm.organisationtypes = OrganisationType.query();
        vm.districts = District.query();
        vm.regions = Region.query();
        vm.localities = Locality.query();
        vm.stations = Station.query();
        vm.contacts = Contact.query();
        vm.serviceprices = ServicePrice.query();
        vm.formatSelection = formatSelection;
        vm.isAddChild = false;
        vm.selectedChild = null;
        vm.addChild = addChild;

        $timeout(function () {
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear() {
            $uibModalInstance.dismiss('cancel');
        }

        function save() {
            vm.isSaving = true;
            vm.partner.lastUpdate = new Date();
            if (vm.partner.id !== null) {
                Partner.update(vm.partner, onSaveSuccess, onSaveError);
            } else {
                Partner.save(vm.partner, onSaveSuccess, onSaveError);
            }

            vm.partner.ownedBies.forEach(function (child) {
                var previouseOwnerForId = child.ownerForId;
                child.ownerForId = vm.partner.id;
                Partner.update(child);

                Partner.get({id : previouseOwnerForId}).$promise.then(
                    function(previouseParent)  {
                        previouseParent.ownedBies = previouseParent.ownedBies.filter(function(parentsChild) {
                            return parentsChild.id !== child.id;
                        });
                        Partner.update(previouseParent);
                    }
                );

            });
        }

        function onSaveSuccess(result) {
            $scope.$emit('grainAdminApp:partnerUpdate', result);
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
            if (vm.selectedChild !== null && vm.partner.id) {
                vm.partner.ownedBies.push(vm.selectedChild);
                vm.isAddChild = false;
                vm.selectedChild = null;
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
    }
})();
