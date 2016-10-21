(function() {
    'use strict';
    angular
        .module('grainAdminApp')
        .factory('ServiceType', ServiceType);

    ServiceType.$inject = ['$resource'];

    function ServiceType ($resource) {
        var resourceUrl =  'api/service-types/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
