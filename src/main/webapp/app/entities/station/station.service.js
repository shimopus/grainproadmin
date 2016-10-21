(function() {
    'use strict';
    angular
        .module('grainAdminApp')
        .factory('Station', Station);

    Station.$inject = ['$resource'];

    function Station ($resource) {
        var resourceUrl =  'api/stations/:id';

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
