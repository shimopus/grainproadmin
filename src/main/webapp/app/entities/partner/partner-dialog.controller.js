(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('PartnerDialogController', PartnerDialogController);

    PartnerDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Partner', 'Bid', 'OrganisationType', 'District', 'Region', 'Locality', 'Station', 'Contact', 'ServicePrice'];

    function PartnerDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Partner, Bid, OrganisationType, District, Region, Locality, Station, Contact, ServicePrice) {
        var vm = this;

        vm.partner = entity;
        vm.clear = clear;
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

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.partner.id !== null) {
                Partner.update(vm.partner, onSaveSuccess, onSaveError);
            } else {
                Partner.save(vm.partner, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('grainAdminApp:partnerUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
