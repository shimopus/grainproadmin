(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('DistrictDetailController', DistrictDetailController);

    DistrictDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'District'];

    function DistrictDetailController($scope, $rootScope, $stateParams, previousState, entity, District) {
        var vm = this;

        vm.district = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('grainAdminApp:districtUpdate', function(event, result) {
            vm.district = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
