(function() {
    'use strict';
    angular
        .module('grainAdminApp')
        .factory('Market', Market);

    Market.$inject = ['$resource'];

    function Market ($resource) {
        var resourceUrl =  'api/market';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
