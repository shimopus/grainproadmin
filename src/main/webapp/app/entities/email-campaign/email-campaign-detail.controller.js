(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('EmailCampaignDetailController', EmailCampaignDetailController);

    EmailCampaignDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'EmailCampaign'];

    function EmailCampaignDetailController($scope, $rootScope, $stateParams, previousState, entity, EmailCampaign) {
        var vm = this;

        vm.emailCampaign = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('grainAdminApp:emailCampaignUpdate', function(event, result) {
            vm.emailCampaign = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
