(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .factory('OrganisationTypeSearch', OrganisationTypeSearch);

    OrganisationTypeSearch.$inject = ['$resource'];

    function OrganisationTypeSearch($resource) {
        var resourceUrl =  'api/_search/organisation-types/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
