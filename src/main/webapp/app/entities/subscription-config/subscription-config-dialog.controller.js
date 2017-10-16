(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('SubscriptionConfigDialogController', SubscriptionConfigDialogController);

    SubscriptionConfigDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q',
        'entity', 'SubscriptionConfig', 'Contact', 'Station', 'Partner', 'StationSearch'];

    function SubscriptionConfigDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q,
                                                 entity, SubscriptionConfig, Contact, Station, Partner, StationSearch) {
        var vm = this;

        vm.subscriptionConfig = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

        vm.stations = [];
        vm.refreshStationSuggestions = refreshStationSuggestions;

        vm.currentPartner = null;
        vm.subscriptionConfig.$promise.then(function() {
            if (!vm.subscriptionConfig.partnerId) {
                return $q.reject();
            }
            return Partner.get({id : vm.subscriptionConfig.partnerId}).$promise;
        }).then(function(partner) {
            vm.currentPartner = partner;

            if (vm.currentPartner.contacts.length === 1) {
                vm.subscriptionConfig.contactId = vm.currentPartner.contacts[0].id;
            }
        });

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function refreshStationSuggestions(term) {
            if (term) {
                vm.stations = StationSearch.query({query: term});
            }
            return null;
        }

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
