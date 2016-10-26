(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .factory('PartnerChildren', PartnerChildren);

    PartnerChildren.$inject = ['$resource', 'DateUtils'];

    function PartnerChildren($resource, DateUtils) {
        var resourceUrl =  'api/partners/children/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.lastUpdate = DateUtils.convertLocalDateToServer(copy.lastUpdate);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
