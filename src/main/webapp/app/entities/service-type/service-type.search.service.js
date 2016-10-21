(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .factory('ServiceTypeSearch', ServiceTypeSearch);

    ServiceTypeSearch.$inject = ['$resource'];

    function ServiceTypeSearch($resource) {
        var resourceUrl =  'api/_search/service-types/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
