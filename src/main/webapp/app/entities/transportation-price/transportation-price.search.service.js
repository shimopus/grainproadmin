(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .factory('TransportationPriceSearch', TransportationPriceSearch);

    TransportationPriceSearch.$inject = ['$resource'];

    function TransportationPriceSearch($resource) {
        var resourceUrl =  'api/_search/transportation-prices/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
