(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .factory('EmailSearch', EmailSearch);

    EmailSearch.$inject = ['$resource'];

    function EmailSearch($resource) {
        var resourceUrl =  'api/_search/emails/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
