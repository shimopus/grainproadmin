(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('OrganisationTypeDetailController', OrganisationTypeDetailController);

    OrganisationTypeDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'OrganisationType'];

    function OrganisationTypeDetailController($scope, $rootScope, $stateParams, previousState, entity, OrganisationType) {
        var vm = this;

        vm.organisationType = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('grainAdminApp:organisationTypeUpdate', function(event, result) {
            vm.organisationType = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
