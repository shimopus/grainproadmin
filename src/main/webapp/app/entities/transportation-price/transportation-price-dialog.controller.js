(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('TransportationPriceDialogController', TransportationPriceDialogController);

    TransportationPriceDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q',
        'entity', 'TransportationPrice', 'StationSearch'];

    function TransportationPriceDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q,
                                                  entity, TransportationPrice, StationSearch) {
        var vm = this;

        vm.transportationPrice = entity;
        vm.clear = clear;
        vm.save = save;
        vm.stationfroms = [];
        vm.stationtos = [];
        vm.refreshStationFromSuggestions = refreshStationFromSuggestions;
        vm.refreshStationToSuggestions = refreshStationToSuggestions;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.transportationPrice.id !== null) {
                TransportationPrice.update(vm.transportationPrice, onSaveSuccess, onSaveError);
            } else {
                TransportationPrice.save(vm.transportationPrice, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('grainAdminApp:transportationPriceUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        function refreshStationFromSuggestions(term) {
            if (term) {
                vm.stationfroms = StationSearch.query({query: term});
            }
            return null;
        }

        function refreshStationToSuggestions(term) {
            if (term) {
                vm.stationtos = StationSearch.query({query: term});
            }
            return null;
        }
    }
})();
