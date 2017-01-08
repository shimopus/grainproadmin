(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('station', {
            parent: 'entity',
            url: '/station?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'grainAdminApp.station.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/station/stations.html',
                    controller: 'StationController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('station');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('station-detail', {
            parent: 'entity',
            url: '/station/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'grainAdminApp.station.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/station/station-detail.html',
                    controller: 'StationDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('station');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Station', function($stateParams, Station) {
                    return Station.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'station',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('station-detail.edit', {
            parent: 'station-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/station/station-dialog.html',
                    controller: 'StationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Station', function(Station) {
                            return Station.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('station.new', {
            parent: 'station',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/station/station-dialog.html',
                    controller: 'StationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                code: null,
                                coordinates: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('station', null, { reload: 'station' });
                }, function() {
                    $state.go('station');
                });
            }]
        })
        .state('station.edit', {
            parent: 'station',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/station/station-dialog.html',
                    controller: 'StationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Station', function(Station) {
                            return Station.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('station', null, { reload: 'station' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('station.delete', {
            parent: 'station',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/station/station-delete-dialog.html',
                    controller: 'StationDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Station', function(Station) {
                            return Station.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('station', null, { reload: 'station' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
