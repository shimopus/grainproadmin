(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('bid', {
            parent: 'entity',
            url: '/bid?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'grainAdminApp.bid.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/bid/bids.html',
                    controller: 'BidController',
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
                    $translatePartialLoader.addPart('bid');
                    $translatePartialLoader.addPart('qualityClass');
                    $translatePartialLoader.addPart('nDS');
                    $translatePartialLoader.addPart('bidType');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('bid-detail', {
            parent: 'entity',
            url: '/bid/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'grainAdminApp.bid.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/bid/bid-detail.html',
                    controller: 'BidDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('bid');
                    $translatePartialLoader.addPart('qualityClass');
                    $translatePartialLoader.addPart('nDS');
                    $translatePartialLoader.addPart('bidType');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Bid', function($stateParams, Bid) {
                    return Bid.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'bid',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('bid-detail.edit', {
            parent: 'bid-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/bid/bid-dialog.html',
                    controller: 'BidDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Bid', function(Bid) {
                            return Bid.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('bid.new', {
            parent: 'bid',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/bid/bid-dialog.html',
                    controller: 'BidDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                creationDate: null,
                                qualityClass: null,
                                volume: null,
                                price: null,
                                nds: null,
                                isActive: null,
                                archiveDate: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('bid', null, { reload: 'bid' });
                }, function() {
                    $state.go('bid');
                });
            }]
        })
        .state('bid.edit', {
            parent: 'bid',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/bid/bid-dialog.html',
                    controller: 'BidDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Bid', function(Bid) {
                            return Bid.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('bid', null, { reload: 'bid' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('bid.delete', {
            parent: 'bid',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/bid/bid-delete-dialog.html',
                    controller: 'BidDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Bid', function(Bid) {
                            return Bid.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('bid', null, { reload: 'bid' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
