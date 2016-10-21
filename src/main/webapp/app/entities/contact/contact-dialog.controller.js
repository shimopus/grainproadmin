(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('ContactDialogController', ContactDialogController);

    ContactDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Contact', 'Email'];

    function ContactDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Contact, Email) {
        var vm = this;

        vm.contact = entity;
        vm.clear = clear;
        vm.save = save;
        vm.emails = Email.query({filter: 'contact-is-null'});
        $q.all([vm.contact.$promise, vm.emails.$promise]).then(function() {
            if (!vm.contact.emailId) {
                return $q.reject();
            }
            return Email.get({id : vm.contact.emailId}).$promise;
        }).then(function(email) {
            vm.emails.push(email);
        });

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.contact.id !== null) {
                Contact.update(vm.contact, onSaveSuccess, onSaveError);
            } else {
                Contact.save(vm.contact, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('grainAdminApp:contactUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
