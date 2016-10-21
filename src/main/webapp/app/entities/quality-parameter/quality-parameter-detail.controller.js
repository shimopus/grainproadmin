(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('QualityParameterDetailController', QualityParameterDetailController);

    QualityParameterDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'QualityParameter'];

    function QualityParameterDetailController($scope, $rootScope, $stateParams, previousState, entity, QualityParameter) {
        var vm = this;

        vm.qualityParameter = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('grainAdminApp:qualityParameterUpdate', function(event, result) {
            vm.qualityParameter = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
