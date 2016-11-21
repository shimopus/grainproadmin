(function() {
    'use strict';
    angular
        .module('grainAdminApp')
        .factory('Passport', Passport);

    Passport.$inject = ['$resource'];

    function Passport ($resource) {
        var resourceUrl =  'api/passports/:id';

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
