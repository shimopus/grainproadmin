(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('StationDetailController', StationDetailController);

    StationDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Station', 'District', 'Region', 'Locality'];

    function StationDetailController($scope, $rootScope, $stateParams, previousState, entity, Station, District, Region, Locality) {
        var vm = this;

        vm.station = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('grainAdminApp:stationUpdate', function(event, result) {
            vm.station = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
