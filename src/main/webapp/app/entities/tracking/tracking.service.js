(function() {
    'use strict';
    angular
        .module('grainAdminApp')
        .factory('Tracking', Tracking);

    Tracking.$inject = ['$resource', 'DateUtils'];

    function Tracking ($resource, DateUtils) {
        var resourceUrl =  'api/trackings/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.mailDate = DateUtils.convertLocalDateFromServer(data.mailDate);
                        data.eventDate = DateUtils.convertDateTimeFromServer(data.eventDate);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.mailDate = DateUtils.convertLocalDateToServer(copy.mailDate);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.mailDate = DateUtils.convertLocalDateToServer(copy.mailDate);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
