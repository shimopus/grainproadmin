(function() {
    'use strict';
    angular
        .module('grainAdminApp')
        .factory('QualityValue', QualityValue);

    QualityValue.$inject = ['$resource'];

    function QualityValue ($resource) {
        var resourceUrl =  'api/quality-values/:id';

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
