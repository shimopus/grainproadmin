(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .factory('QualityParameterSearch', QualityParameterSearch);

    QualityParameterSearch.$inject = ['$resource'];

    function QualityParameterSearch($resource) {
        var resourceUrl =  'api/_search/quality-parameters/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
