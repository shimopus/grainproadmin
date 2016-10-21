(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('QualityValueDetailController', QualityValueDetailController);

    QualityValueDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'QualityValue', 'QualityParameter'];

    function QualityValueDetailController($scope, $rootScope, $stateParams, previousState, entity, QualityValue, QualityParameter) {
        var vm = this;

        vm.qualityValue = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('grainAdminApp:qualityValueUpdate', function(event, result) {
            vm.qualityValue = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
