(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .factory('PassportSearch', PassportSearch);

    PassportSearch.$inject = ['$resource'];

    function PassportSearch($resource) {
        var resourceUrl =  'api/_search/passports/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
