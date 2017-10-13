(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .factory('SubscriptionConfigSearch', SubscriptionConfigSearch);

    SubscriptionConfigSearch.$inject = ['$resource'];

    function SubscriptionConfigSearch($resource) {
        var resourceUrl =  'api/_search/subscription-configs/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
