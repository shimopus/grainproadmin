(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .factory('EmailCampaignSearch', EmailCampaignSearch);

    EmailCampaignSearch.$inject = ['$resource'];

    function EmailCampaignSearch($resource) {
        var resourceUrl =  'api/_search/email-campaigns/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
