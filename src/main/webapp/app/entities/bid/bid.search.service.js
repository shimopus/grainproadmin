(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .factory('BidSearch', BidSearch);

    BidSearch.$inject = ['$resource'];

    function BidSearch($resource) {
        var resourceUrl =  'api/_search/bids/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
