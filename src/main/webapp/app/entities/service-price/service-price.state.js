(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('service-price', {
            parent: 'entity',
            url: '/service-price?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'grainAdminApp.servicePrice.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/service-price/service-prices.html',
                    controller: 'ServicePriceController',
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
                    $translatePartialLoader.addPart('servicePrice');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('service-price-detail', {
            parent: 'entity',
            url: '/service-price/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'grainAdminApp.servicePrice.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/service-price/service-price-detail.html',
                    controller: 'ServicePriceDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('servicePrice');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ServicePrice', function($stateParams, ServicePrice) {
                    return ServicePrice.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'service-price',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('service-price-detail.edit', {
            parent: 'service-price-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/service-price/service-price-dialog.html',
                    controller: 'ServicePriceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ServicePrice', function(ServicePrice) {
                            return ServicePrice.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('service-price.new', {
            parent: 'service-price',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/service-price/service-price-dialog.html',
                    controller: 'ServicePriceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                price: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('service-price', null, { reload: 'service-price' });
                }, function() {
                    $state.go('service-price');
                });
            }]
        })
        .state('service-price.edit', {
            parent: 'service-price',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/service-price/service-price-dialog.html',
                    controller: 'ServicePriceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ServicePrice', function(ServicePrice) {
                            return ServicePrice.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('service-price', null, { reload: 'service-price' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('service-price.delete', {
            parent: 'service-price',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/service-price/service-price-delete-dialog.html',
                    controller: 'ServicePriceDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ServicePrice', function(ServicePrice) {
                            return ServicePrice.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('service-price', null, { reload: 'service-price' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
