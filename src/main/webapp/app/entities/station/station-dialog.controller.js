(function () {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('StationDialogController', StationDialogController);

    StationDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Station',
        'DistrictSearch', 'RegionSearch', 'LocalitySearch'];

    function StationDialogController($timeout, $scope, $stateParams, $uibModalInstance, entity, Station,
                                     DistrictSearch, RegionSearch, LocalitySearch) {
        var vm = this;

        vm.station = entity;
        vm.clear = clear;
        vm.save = save;
        vm.refreshDistrictSuggestions = refreshDistrictSuggestions;
        vm.refreshRegionSuggestions = refreshRegionSuggestions;
        vm.refreshLocalitySuggestions = refreshLocalitySuggestions;
        vm.districts = [];
        vm.regions = [];
        vm.localities = [];

        $timeout(function () {
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear() {
            $uibModalInstance.dismiss('cancel');
        }

        function save() {
            vm.isSaving = true;
            if (vm.station.id !== null) {
                Station.update(vm.station, onSaveSuccess, onSaveError);
            } else {
                Station.save(vm.station, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess(result) {
            $scope.$emit('grainAdminApp:stationUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError() {
            vm.isSaving = false;
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
