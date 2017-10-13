(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('subscription-config', {
            parent: 'entity',
            url: '/subscription-config?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'grainAdminApp.subscriptionConfig.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/subscription-config/subscription-configs.html',
                    controller: 'SubscriptionConfigController',
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
                    $translatePartialLoader.addPart('subscriptionConfig');
                    $translatePartialLoader.addPart('subscriptionType');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('subscription-config-detail', {
            parent: 'entity',
            url: '/subscription-config/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'grainAdminApp.subscriptionConfig.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/subscription-config/subscription-config-detail.html',
                    controller: 'SubscriptionConfigDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('subscriptionConfig');
                    $translatePartialLoader.addPart('subscriptionType');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'SubscriptionConfig', function($stateParams, SubscriptionConfig) {
                    return SubscriptionConfig.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'subscription-config',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('subscription-config-detail.edit', {
            parent: 'subscription-config-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/subscription-config/subscription-config-dialog.html',
                    controller: 'SubscriptionConfigDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['SubscriptionConfig', function(SubscriptionConfig) {
                            return SubscriptionConfig.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('subscription-config.new', {
            parent: 'subscription-config',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/subscription-config/subscription-config-dialog.html',
                    controller: 'SubscriptionConfigDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                subscriptionType: null,
                                isActive: null,
                                creationDate: null,
                                lastUpdateDate: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('subscription-config', null, { reload: 'subscription-config' });
                }, function() {
                    $state.go('subscription-config');
                });
            }]
        })
        .state('subscription-config.edit', {
            parent: 'subscription-config',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/subscription-config/subscription-config-dialog.html',
                    controller: 'SubscriptionConfigDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['SubscriptionConfig', function(SubscriptionConfig) {
                            return SubscriptionConfig.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('subscription-config', null, { reload: 'subscription-config' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('subscription-config.delete', {
            parent: 'subscription-config',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/subscription-config/subscription-config-delete-dialog.html',
                    controller: 'SubscriptionConfigDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['SubscriptionConfig', function(SubscriptionConfig) {
                            return SubscriptionConfig.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('subscription-config', null, { reload: 'subscription-config' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
