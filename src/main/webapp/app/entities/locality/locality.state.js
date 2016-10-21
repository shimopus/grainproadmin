(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('locality', {
            parent: 'entity',
            url: '/locality?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'grainAdminApp.locality.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/locality/localities.html',
                    controller: 'LocalityController',
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
                    $translatePartialLoader.addPart('locality');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('locality-detail', {
            parent: 'entity',
            url: '/locality/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'grainAdminApp.locality.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/locality/locality-detail.html',
                    controller: 'LocalityDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('locality');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Locality', function($stateParams, Locality) {
                    return Locality.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'locality',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('locality-detail.edit', {
            parent: 'locality-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/locality/locality-dialog.html',
                    controller: 'LocalityDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Locality', function(Locality) {
                            return Locality.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('locality.new', {
            parent: 'locality',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/locality/locality-dialog.html',
                    controller: 'LocalityDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('locality', null, { reload: 'locality' });
                }, function() {
                    $state.go('locality');
                });
            }]
        })
        .state('locality.edit', {
            parent: 'locality',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/locality/locality-dialog.html',
                    controller: 'LocalityDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Locality', function(Locality) {
                            return Locality.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('locality', null, { reload: 'locality' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('locality.delete', {
            parent: 'locality',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/locality/locality-delete-dialog.html',
                    controller: 'LocalityDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Locality', function(Locality) {
                            return Locality.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('locality', null, { reload: 'locality' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
