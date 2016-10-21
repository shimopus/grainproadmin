(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('TransportationPriceDialogController', TransportationPriceDialogController);

    TransportationPriceDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'TransportationPrice', 'Station'];

    function TransportationPriceDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, TransportationPrice, Station) {
        var vm = this;

        vm.transportationPrice = entity;
        vm.clear = clear;
        vm.save = save;
        vm.stationfroms = Station.query({filter: 'transportationprice-is-null'});
        $q.all([vm.transportationPrice.$promise, vm.stationfroms.$promise]).then(function() {
            if (!vm.transportationPrice.stationFromId) {
                return $q.reject();
            }
            return Station.get({id : vm.transportationPrice.stationFromId}).$promise;
        }).then(function(stationFrom) {
            vm.stationfroms.push(stationFrom);
        });
        vm.stationtos = Station.query({filter: 'transportationprice-is-null'});
        $q.all([vm.transportationPrice.$promise, vm.stationtos.$promise]).then(function() {
            if (!vm.transportationPrice.stationToId) {
                return $q.reject();
            }
            return Station.get({id : vm.transportationPrice.stationToId}).$promise;
        }).then(function(stationTo) {
            vm.stationtos.push(stationTo);
        });

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


    }
})();
