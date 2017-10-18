(function() {
    'use strict';
    angular
        .module('grainAdminApp')
        .factory('EmailCampaign', EmailCampaign);

    EmailCampaign.$inject = ['$resource', 'DateUtils'];

    function EmailCampaign ($resource, DateUtils) {
        var resourceUrl =  'api/email-campaigns/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.date = DateUtils.convertDateTimeFromServer(data.date);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
