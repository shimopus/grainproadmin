(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('tracking', {
            parent: 'entity',
            url: '/tracking?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'grainAdminApp.tracking.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/tracking/trackings.html',
                    controller: 'TrackingController',
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
                    $translatePartialLoader.addPart('tracking');
                    $translatePartialLoader.addPart('mailOpenType');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('tracking-detail', {
            parent: 'entity',
            url: '/tracking/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'grainAdminApp.tracking.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/tracking/tracking-detail.html',
                    controller: 'TrackingDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('tracking');
                    $translatePartialLoader.addPart('mailOpenType');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Tracking', function($stateParams, Tracking) {
                    return Tracking.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'tracking',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('tracking-detail.edit', {
            parent: 'tracking-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tracking/tracking-dialog.html',
                    controller: 'TrackingDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Tracking', function(Tracking) {
                            return Tracking.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('tracking.new', {
            parent: 'tracking',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tracking/tracking-dialog.html',
                    controller: 'TrackingDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                mailDate: null,
                                openType: null,
                                openCount: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('tracking', null, { reload: 'tracking' });
                }, function() {
                    $state.go('tracking');
                });
            }]
        })
        .state('tracking.edit', {
            parent: 'tracking',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tracking/tracking-dialog.html',
                    controller: 'TrackingDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Tracking', function(Tracking) {
                            return Tracking.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('tracking', null, { reload: 'tracking' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('tracking.delete', {
            parent: 'tracking',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tracking/tracking-delete-dialog.html',
                    controller: 'TrackingDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Tracking', function(Tracking) {
                            return Tracking.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('tracking', null, { reload: 'tracking' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
