(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .factory('TrackingSearch', TrackingSearch);

    TrackingSearch.$inject = ['$resource'];

    function TrackingSearch($resource) {
        var resourceUrl =  'api/_search/trackings/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
