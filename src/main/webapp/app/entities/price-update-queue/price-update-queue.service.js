(function() {
    'use strict';
    angular
        .module('grainAdminApp')
        .factory('PriceUpdateQueue', PriceUpdateQueue);

    PriceUpdateQueue.$inject = ['$resource'];

    function PriceUpdateQueue ($resource) {
        var resourceUrl =  'api/price-update-queues/:id';

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
