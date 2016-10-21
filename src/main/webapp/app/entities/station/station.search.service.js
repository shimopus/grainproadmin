(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .factory('StationSearch', StationSearch);

    StationSearch.$inject = ['$resource'];

    function StationSearch($resource) {
        var resourceUrl =  'api/_search/stations/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
