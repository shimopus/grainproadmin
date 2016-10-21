(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('quality-parameter', {
            parent: 'entity',
            url: '/quality-parameter?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'grainAdminApp.qualityParameter.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/quality-parameter/quality-parameters.html',
                    controller: 'QualityParameterController',
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
                    $translatePartialLoader.addPart('qualityParameter');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('quality-parameter-detail', {
            parent: 'entity',
            url: '/quality-parameter/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'grainAdminApp.qualityParameter.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/quality-parameter/quality-parameter-detail.html',
                    controller: 'QualityParameterDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('qualityParameter');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'QualityParameter', function($stateParams, QualityParameter) {
                    return QualityParameter.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'quality-parameter',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('quality-parameter-detail.edit', {
            parent: 'quality-parameter-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/quality-parameter/quality-parameter-dialog.html',
                    controller: 'QualityParameterDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['QualityParameter', function(QualityParameter) {
                            return QualityParameter.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('quality-parameter.new', {
            parent: 'quality-parameter',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/quality-parameter/quality-parameter-dialog.html',
                    controller: 'QualityParameterDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                availableValues: null,
                                unit: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('quality-parameter', null, { reload: 'quality-parameter' });
                }, function() {
                    $state.go('quality-parameter');
                });
            }]
        })
        .state('quality-parameter.edit', {
            parent: 'quality-parameter',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/quality-parameter/quality-parameter-dialog.html',
                    controller: 'QualityParameterDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['QualityParameter', function(QualityParameter) {
                            return QualityParameter.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('quality-parameter', null, { reload: 'quality-parameter' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('quality-parameter.delete', {
            parent: 'quality-parameter',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/quality-parameter/quality-parameter-delete-dialog.html',
                    controller: 'QualityParameterDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['QualityParameter', function(QualityParameter) {
                            return QualityParameter.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('quality-parameter', null, { reload: 'quality-parameter' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
