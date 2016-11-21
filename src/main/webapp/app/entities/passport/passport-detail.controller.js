(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('PassportDetailController', PassportDetailController);

    PassportDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'Passport'];

    function PassportDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, Passport) {
        var vm = this;

        vm.passport = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('grainAdminApp:passportUpdate', function(event, result) {
            vm.passport = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
