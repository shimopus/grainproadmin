(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .factory('QualityValueSearch', QualityValueSearch);

    QualityValueSearch.$inject = ['$resource'];

    function QualityValueSearch($resource) {
        var resourceUrl =  'api/_search/quality-values/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
