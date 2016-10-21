(function() {
    'use strict';
    angular
        .module('grainAdminApp')
        .factory('QualityParameter', QualityParameter);

    QualityParameter.$inject = ['$resource'];

    function QualityParameter ($resource) {
        var resourceUrl =  'api/quality-parameters/:id';

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
