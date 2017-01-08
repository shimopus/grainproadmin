(function() {
    'use strict';
    angular
        .module('grainAdminApp')
        .factory('PartnerCard', PartnerCard);

    PartnerCard.$inject = ['$uibModal'];

    function PartnerCard($uibModal) {
        var service = {
            showDialog: showDialog
        };

        return service;

        function showDialog(partner) {
            return $uibModal.open({
                templateUrl: 'app/entities/partner/partner-card-dialog.html',
                controller: 'PartnerCardDialogController',
                controllerAs: 'vm',
                backdrop: 'static',
                size: 'lg',
                resolve: {
                    partner: function(){ return partner; }
                }
            });
        }
    }
})();
