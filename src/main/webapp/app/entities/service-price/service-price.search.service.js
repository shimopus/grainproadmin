(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .factory('ServicePriceSearch', ServicePriceSearch);

    ServicePriceSearch.$inject = ['$resource'];

    function ServicePriceSearch($resource) {
        var resourceUrl =  'api/_search/service-prices/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
