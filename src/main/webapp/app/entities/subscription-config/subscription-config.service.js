(function() {
    'use strict';
    angular
        .module('grainAdminApp')
        .factory('SubscriptionConfig', SubscriptionConfig);

    SubscriptionConfig.$inject = ['$resource', 'DateUtils'];

    function SubscriptionConfig ($resource, DateUtils) {
        var resourceUrl =  'api/subscription-configs/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.creationDate = DateUtils.convertDateTimeFromServer(data.creationDate);
                        data.lastUpdateDate = DateUtils.convertDateTimeFromServer(data.lastUpdateDate);
                    }
                    return data;
                }
            },
            'getByPartner': {
                method: 'GET',
                url: 'api/subscription-configs/getbypartner',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.creationDate = DateUtils.convertDateTimeFromServer(data.creationDate);
                        data.lastUpdateDate = DateUtils.convertDateTimeFromServer(data.lastUpdateDate);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
