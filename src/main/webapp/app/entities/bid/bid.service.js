(function() {
    'use strict';
    angular
        .module('grainAdminApp')
        .factory('Bid', Bid);

    Bid.$inject = ['$resource', 'DateUtils'];

    function Bid ($resource, DateUtils) {
        var resourceUrl =  'api/bids/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'queryByPartner': {
                method: 'GET',
                url: 'api/bids/bypartner',
                isArray: true
            },
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.creationDate = DateUtils.convertDateTimeFromServer(data.creationDate);
                        data.archiveDate = DateUtils.convertDateTimeFromServer(data.archiveDate);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.creationDate = DateUtils.convertDateTimeToServer(copy.creationDate);
                    copy.archiveDate = DateUtils.convertDateTimeToServer(copy.archiveDate);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.creationDate = DateUtils.convertDateTimeToServer(copy.creationDate);
                    copy.archiveDate = DateUtils.convertDateTimeToServer(copy.archiveDate);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
