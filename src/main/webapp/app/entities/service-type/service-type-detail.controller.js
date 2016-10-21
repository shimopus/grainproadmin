(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('ServiceTypeDetailController', ServiceTypeDetailController);

    ServiceTypeDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ServiceType'];

    function ServiceTypeDetailController($scope, $rootScope, $stateParams, previousState, entity, ServiceType) {
        var vm = this;

        vm.serviceType = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('grainAdminApp:serviceTypeUpdate', function(event, result) {
            vm.serviceType = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
