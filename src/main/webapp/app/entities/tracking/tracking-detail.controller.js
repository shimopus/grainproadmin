(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('TrackingDetailController', TrackingDetailController);

    TrackingDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Tracking', 'Partner'];

    function TrackingDetailController($scope, $rootScope, $stateParams, previousState, entity, Tracking, Partner) {
        var vm = this;

        vm.tracking = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('grainAdminApp:trackingUpdate', function(event, result) {
            vm.tracking = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
