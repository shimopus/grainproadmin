(function () {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('PartnerDialogController', PartnerDialogController);

    PartnerDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Partner',
        'Bid', 'OrganisationType', 'District', 'Region', 'Locality', 'Station', 'Contact', 'ServicePrice', 'PartnerChildren'
    ];

    function PartnerDialogController($timeout, $scope, $stateParams, $uibModalInstance, entity, Partner,
                                     Bid, OrganisationType, District, Region, Locality, Station,
                                     Contact, ServicePrice, PartnerChildren) {
        var vm = this;

        vm.partner = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.bids = Bid.query();
        vm.partners = Partner.query();
        vm.organisationtypes = OrganisationType.query();
        vm.districts = District.query();
        vm.regions = Region.query();
        vm.localities = Locality.query();
        vm.stations = Station.query();
        vm.contacts = Contact.query();
        vm.serviceprices = ServicePrice.query();
        vm.children = PartnerChildren.query(entity);
        vm.formatSelection = formatSelection;

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
    }
})();
