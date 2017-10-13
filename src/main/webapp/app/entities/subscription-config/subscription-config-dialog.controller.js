(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('SubscriptionConfigDialogController', SubscriptionConfigDialogController);

    SubscriptionConfigDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'SubscriptionConfig', 'Contact', 'Station'];

    function SubscriptionConfigDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, SubscriptionConfig, Contact, Station) {
        var vm = this;

        vm.subscriptionConfig = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.contacts = Contact.query({filter: 'subscriptionconfig-is-null'});
        $q.all([vm.subscriptionConfig.$promise, vm.contacts.$promise]).then(function() {
            if (!vm.subscriptionConfig.contactId) {
                return $q.reject();
            }
            return Contact.get({id : vm.subscriptionConfig.contactId}).$promise;
        }).then(function(contact) {
            vm.contacts.push(contact);
        });
        vm.stations = Station.query({filter: 'subscriptionconfig-is-null'});
        $q.all([vm.subscriptionConfig.$promise, vm.stations.$promise]).then(function() {
            if (!vm.subscriptionConfig.stationId) {
                return $q.reject();
            }
            return Station.get({id : vm.subscriptionConfig.stationId}).$promise;
        }).then(function(station) {
            vm.stations.push(station);
        });

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.subscriptionConfig.id !== null) {
                SubscriptionConfig.update(vm.subscriptionConfig, onSaveSuccess, onSaveError);
            } else {
                SubscriptionConfig.save(vm.subscriptionConfig, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('grainAdminApp:subscriptionConfigUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.creationDate = false;
        vm.datePickerOpenStatus.lastUpdateDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
