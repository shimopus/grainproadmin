(function() {
    'use strict';
    angular
        .module('grainAdminApp')
        .factory('Partner', Partner);

    Partner.$inject = ['$resource', 'DateUtils'];

    function Partner ($resource, DateUtils) {
        var resourceUrl =  'api/partners/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.lastUpdate = DateUtils.convertLocalDateFromServer(data.lastUpdate);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.lastUpdate = DateUtils.convertLocalDateToServer(copy.lastUpdate);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.lastUpdate = DateUtils.convertLocalDateToServer(copy.lastUpdate);
                    return angular.toJson(copy);
                }
            },
            'bids': {
                method: 'GET',
                url: resourceUrl + '/bids',
                isArray: true
            }
        });
    }
})();
