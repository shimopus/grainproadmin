(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .factory('LocalitySearch', LocalitySearch);

    LocalitySearch.$inject = ['$resource'];

    function LocalitySearch($resource) {
        var resourceUrl =  'api/_search/localities/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
