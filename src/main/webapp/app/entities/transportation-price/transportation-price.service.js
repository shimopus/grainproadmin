(function() {
    'use strict';
    angular
        .module('grainAdminApp')
        .factory('TransportationPrice', TransportationPrice);

    TransportationPrice.$inject = ['$resource'];

    function TransportationPrice ($resource) {
        var resourceUrl =  'api/transportation-prices/:id';

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
