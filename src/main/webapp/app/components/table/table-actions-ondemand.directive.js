(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .directive('tableActionsOndemand', ['$document', '$timeout', tableActionsOndemand]);

    function tableActionsOndemand ($document, $timeout) {
        return {
            restrict: 'A',
            link: linkFunc
        };

        function linkFunc (scope, element, attrs, tableCtrl) {
            element.on('mousemove', onTableMouseMove);
            $document.on('mouseout', 'table tr', onTableMouseOut);
        }

        var currentTr;
        var timeout;

        function onTableMouseMove(event) {
            $timeout.cancel(timeout);

            var targetTr = angular.element(event.target).closest('tr');

            if (!currentTr || targetTr && currentTr.attr('id') !== targetTr.attr('id')) {
                if (currentTr) {
                    toggleActionButtons(currentTr, true);
                }
                toggleActionButtons(targetTr, false);
                currentTr = targetTr;
            }

        }

        function onTableMouseOut() {
            timeout = $timeout(function(){
                if (currentTr && currentTr.id) {
                    toggleActionButtons(currentTr, true);
                    currentTr = null;
                }
            }, 200);
        }

        function toggleActionButtons(tr, state) {
            var div = tr.find('td.actions > div.btn-group');
            div.toggleClass('hidden', state);
        }
    }
})();
