(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('EmailDetailController', EmailDetailController);

    EmailDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Email'];

    function EmailDetailController($scope, $rootScope, $stateParams, previousState, entity, Email) {
        var vm = this;

        vm.email = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('grainAdminApp:emailUpdate', function(event, result) {
            vm.email = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
