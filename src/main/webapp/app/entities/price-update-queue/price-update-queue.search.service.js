(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .factory('PriceUpdateQueueSearch', PriceUpdateQueueSearch);

    PriceUpdateQueueSearch.$inject = ['$resource'];

    function PriceUpdateQueueSearch($resource) {
        var resourceUrl =  'api/_search/price-update-queues/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
