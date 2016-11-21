(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('passport', {
            parent: 'entity',
            url: '/passport?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'grainAdminApp.passport.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/passport/passports.html',
                    controller: 'PassportController',
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
                    $translatePartialLoader.addPart('passport');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('passport-detail', {
            parent: 'entity',
            url: '/passport/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'grainAdminApp.passport.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/passport/passport-detail.html',
                    controller: 'PassportDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('passport');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Passport', function($stateParams, Passport) {
                    return Passport.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'passport',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('passport-detail.edit', {
            parent: 'passport-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/passport/passport-dialog.html',
                    controller: 'PassportDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Passport', function(Passport) {
                            return Passport.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('passport.new', {
            parent: 'passport',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/passport/passport-dialog.html',
                    controller: 'PassportDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                image: null,
                                imageContentType: null,
                                title: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('passport', null, { reload: 'passport' });
                }, function() {
                    $state.go('passport');
                });
            }]
        })
        .state('passport.edit', {
            parent: 'passport',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/passport/passport-dialog.html',
                    controller: 'PassportDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Passport', function(Passport) {
                            return Passport.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('passport', null, { reload: 'passport' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('passport.delete', {
            parent: 'passport',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/passport/passport-delete-dialog.html',
                    controller: 'PassportDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Passport', function(Passport) {
                            return Passport.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('passport', null, { reload: 'passport' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
