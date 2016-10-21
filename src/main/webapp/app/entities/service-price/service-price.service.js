(function() {
    'use strict';
    angular
        .module('grainAdminApp')
        .factory('ServicePrice', ServicePrice);

    ServicePrice.$inject = ['$resource'];

    function ServicePrice ($resource) {
        var resourceUrl =  'api/service-prices/:id';

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
