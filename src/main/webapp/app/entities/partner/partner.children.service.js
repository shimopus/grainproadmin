(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .factory('PartnerChildren', PartnerChildren);

    PartnerChildren.$inject = ['$resource', 'DateUtils'];

    function PartnerChildren($resource, DateUtils) {
        var resourceUrl =  'api/partners/children/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.lastUpdate = DateUtils.convertLocalDateFromServer(data.lastUpdate);
                    }
                    return data;
                }
            }
        });
    }
})();
