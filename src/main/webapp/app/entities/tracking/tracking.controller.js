(function () {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('TrackingController', TrackingController);

    TrackingController.$inject = ['$scope', '$state', 'Tracking', 'TrackingSearch', 'ParseLinks', 'AlertService', 'pagingParams', 'paginationConstants',
        '$timeout', 'Partner', 'PartnerSearch'];

    function TrackingController($scope, $state, Tracking, TrackingSearch, ParseLinks, AlertService, pagingParams, paginationConstants,
                                $timeout, Partner, PartnerSearch) {
        var vm = this;

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;
        vm.searchQuery = pagingParams.search;
        vm.currentSearch = pagingParams.search;
        vm.partners = Partner.query();
        vm.getPartnersSuggestions = getPartnersSuggestions;
        vm.selectedPartner = null;
        vm.partnerChanged = partnerChanged;

        vm.dataFromPromise = Tracking.statisticsByPartner({
            partnerId: null
        }).$promise;

        vm.amChartOptions = {
            "theme": "light",
            "type": "serial",
            "marginRight": 80,
            "autoMarginOffset": 20,
            "marginTop": 20,
            "data": vm.dataFromPromise,
            "valueAxes": [{
                "id": "v1",
                "axisAlpha": 0.1
            }],
            "graphs": [{
                "title": "Открытий писем",
                "balloonText": "<b>[[title]]: [[value]]</b>",
                "bullet": "round",
                "bulletBorderAlpha": 1,
                "bulletBorderColor": "#FFFFFF",
                "hideBulletsCount": 50,
                "lineThickness": 2,
                "lineColor": "#fdd400",
                "valueField": "openCount"
            }, {
                "title": "Открытий файлов",
                "balloonText": "<b>[[title]]: [[value]]</b>",
                "bullet": "round",
                "bulletBorderAlpha": 1,
                "bulletBorderColor": "#FFFFFF",
                "hideBulletsCount": 50,
                "lineThickness": 2,
                "lineColor": "#fd7ea6",
                "valueField": "openFileCount"
            }],
            "categoryField": "mailDate",
            "categoryAxis": {
                "parseDates": true,
                "axisAlpha": 0,
                "minHorizontalGap": 60
            },
            "legend": {
                "useGraphSettings": true
            }
        };

        loadAll();

        function loadAll() {
            if (pagingParams.search) {
                TrackingSearch.query({
                    query: pagingParams.search,
                    page: pagingParams.page - 1,
                    size: vm.itemsPerPage,
                    sort: sort()
                }, onSuccess, onError);
            } else {
                Tracking.query({
                    page: pagingParams.page - 1,
                    size: vm.itemsPerPage,
                    sort: sort()
                }, onSuccess, onError);
            }
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }

            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.trackings = data;
                vm.page = pagingParams.page;
            }

            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function loadPage(page) {
            vm.page = page;
            vm.transition();
        }

        function transition() {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                search: vm.currentSearch
            });
        }

        function search(searchQuery) {
            if (!searchQuery) {
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = '_score';
            vm.reverse = false;
            vm.currentSearch = searchQuery;
            vm.transition();
        }

        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'id';
            vm.reverse = true;
            vm.currentSearch = null;
            vm.transition();
        }

        function getPartnersSuggestions(partnerName) {
            return PartnerSearch.query({
                query: partnerName,
                page: 0,
                size: 20,
                sort: 'asc'
            }).$promise;
        }

        $scope.$watch("vm.selectedPartner", partnerChanged);

        function partnerChanged(model) {
            Tracking.statisticsByPartner({
                partnerId: model ? model.id : null
            }).$promise.then(function (data) {
                $scope.$broadcast('amCharts.updateData', data, 'myFirstChart');
            });
        }
    }
})();
