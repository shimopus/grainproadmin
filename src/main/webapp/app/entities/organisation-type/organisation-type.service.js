(function() {
    'use strict';
    angular
        .module('grainAdminApp')
        .factory('OrganisationType', OrganisationType);

    OrganisationType.$inject = ['$resource'];

    function OrganisationType ($resource) {
        var resourceUrl =  'api/organisation-types/:id';

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
