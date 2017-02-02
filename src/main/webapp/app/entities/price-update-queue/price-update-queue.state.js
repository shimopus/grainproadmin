(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('price-update-queue', {
            parent: 'entity',
            url: '/price-update-queue?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'grainAdminApp.priceUpdateQueue.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/price-update-queue/price-update-queues.html',
                    controller: 'PriceUpdateQueueController',
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
                    $translatePartialLoader.addPart('priceUpdateQueue');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('price-update-queue-detail', {
            parent: 'entity',
            url: '/price-update-queue/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'grainAdminApp.priceUpdateQueue.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/price-update-queue/price-update-queue-detail.html',
                    controller: 'PriceUpdateQueueDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('priceUpdateQueue');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'PriceUpdateQueue', function($stateParams, PriceUpdateQueue) {
                    return PriceUpdateQueue.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'price-update-queue',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('price-update-queue-detail.edit', {
            parent: 'price-update-queue-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/price-update-queue/price-update-queue-dialog.html',
                    controller: 'PriceUpdateQueueDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['PriceUpdateQueue', function(PriceUpdateQueue) {
                            return PriceUpdateQueue.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('price-update-queue.new', {
            parent: 'price-update-queue',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/price-update-queue/price-update-queue-dialog.html',
                    controller: 'PriceUpdateQueueDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                loaded: false,
                                loadingOrder: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('price-update-queue', null, { reload: 'price-update-queue' });
                }, function() {
                    $state.go('price-update-queue');
                });
            }]
        })
        .state('price-update-queue.edit', {
            parent: 'price-update-queue',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/price-update-queue/price-update-queue-dialog.html',
                    controller: 'PriceUpdateQueueDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['PriceUpdateQueue', function(PriceUpdateQueue) {
                            return PriceUpdateQueue.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('price-update-queue', null, { reload: 'price-update-queue' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('price-update-queue.delete', {
            parent: 'price-update-queue',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/price-update-queue/price-update-queue-delete-dialog.html',
                    controller: 'PriceUpdateQueueDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['PriceUpdateQueue', function(PriceUpdateQueue) {
                            return PriceUpdateQueue.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('price-update-queue', null, { reload: 'price-update-queue' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
