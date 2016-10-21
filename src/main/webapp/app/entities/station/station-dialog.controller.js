(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('StationDialogController', StationDialogController);

    StationDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Station', 'District', 'Region', 'Locality'];

    function StationDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Station, District, Region, Locality) {
        var vm = this;

        vm.station = entity;
        vm.clear = clear;
        vm.save = save;
        vm.districts = District.query();
        vm.regions = Region.query();
        vm.localities = Locality.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.station.id !== null) {
                Station.update(vm.station, onSaveSuccess, onSaveError);
            } else {
                Station.save(vm.station, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('grainAdminApp:stationUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
