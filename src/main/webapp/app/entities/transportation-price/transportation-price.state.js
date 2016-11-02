(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('transportation-price', {
            parent: 'entity',
            url: '/transportation-price?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'grainAdminApp.transportationPrice.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/transportation-price/transportation-prices.html',
                    controller: 'TransportationPriceController',
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
                    $translatePartialLoader.addPart('transportationPrice');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('transportation-price-detail', {
            parent: 'entity',
            url: '/transportation-price/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'grainAdminApp.transportationPrice.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/transportation-price/transportation-price-detail.html',
                    controller: 'TransportationPriceDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('transportationPrice');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'TransportationPrice', function($stateParams, TransportationPrice) {
                    return TransportationPrice.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'transportation-price',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('transportation-price-detail.edit', {
            parent: 'transportation-price-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/transportation-price/transportation-price-dialog.html',
                    controller: 'TransportationPriceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['TransportationPrice', function(TransportationPrice) {
                            return TransportationPrice.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('transportation-price.new', {
            parent: 'transportation-price',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/transportation-price/transportation-price-dialog.html',
                    controller: 'TransportationPriceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                price: null,
                                priceNds: null,
                                distance: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('transportation-price', null, { reload: 'transportation-price' });
                }, function() {
                    $state.go('transportation-price');
                });
            }]
        })
        .state('transportation-price.edit', {
            parent: 'transportation-price',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/transportation-price/transportation-price-dialog.html',
                    controller: 'TransportationPriceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['TransportationPrice', function(TransportationPrice) {
                            return TransportationPrice.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('transportation-price', null, { reload: 'transportation-price' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('transportation-price.delete', {
            parent: 'transportation-price',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/transportation-price/transportation-price-delete-dialog.html',
                    controller: 'TransportationPriceDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['TransportationPrice', function(TransportationPrice) {
                            return TransportationPrice.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('transportation-price', null, { reload: 'transportation-price' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
