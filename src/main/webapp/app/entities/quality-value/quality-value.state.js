(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('quality-value', {
            parent: 'entity',
            url: '/quality-value?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'grainAdminApp.qualityValue.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/quality-value/quality-values.html',
                    controller: 'QualityValueController',
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
                    $translatePartialLoader.addPart('qualityValue');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('quality-value-detail', {
            parent: 'entity',
            url: '/quality-value/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'grainAdminApp.qualityValue.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/quality-value/quality-value-detail.html',
                    controller: 'QualityValueDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('qualityValue');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'QualityValue', function($stateParams, QualityValue) {
                    return QualityValue.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'quality-value',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('quality-value-detail.edit', {
            parent: 'quality-value-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/quality-value/quality-value-dialog.html',
                    controller: 'QualityValueDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['QualityValue', function(QualityValue) {
                            return QualityValue.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('quality-value.new', {
            parent: 'quality-value',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/quality-value/quality-value-dialog.html',
                    controller: 'QualityValueDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                value: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('quality-value', null, { reload: 'quality-value' });
                }, function() {
                    $state.go('quality-value');
                });
            }]
        })
        .state('quality-value.edit', {
            parent: 'quality-value',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/quality-value/quality-value-dialog.html',
                    controller: 'QualityValueDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['QualityValue', function(QualityValue) {
                            return QualityValue.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('quality-value', null, { reload: 'quality-value' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('quality-value.delete', {
            parent: 'quality-value',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/quality-value/quality-value-delete-dialog.html',
                    controller: 'QualityValueDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['QualityValue', function(QualityValue) {
                            return QualityValue.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('quality-value', null, { reload: 'quality-value' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
