(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('LocalityDetailController', LocalityDetailController);

    LocalityDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Locality'];

    function LocalityDetailController($scope, $rootScope, $stateParams, previousState, entity, Locality) {
        var vm = this;

        vm.locality = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('grainAdminApp:localityUpdate', function(event, result) {
            vm.locality = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
