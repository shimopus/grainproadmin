(function () {
    'use strict';

    angular
        .module('grainAdminApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('market', {
            parent: 'app',
            url: '/market',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'grainAdminApp.market.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/market/market.html',
                    controller: 'MarketController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('market');
                    $translatePartialLoader.addPart('bid');
                    $translatePartialLoader.addPart('nDS');
                    $translatePartialLoader.addPart('qualityClass');
                    return $translate.refresh();
                }]
            }
        })
            .state('market.quality-passport', {
                parent: 'market',
                url: '/{bidId}/carousel',
                data: {
                    authorities: ['ROLE_USER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/bid/bid-quality-passport.html',
                        controller: 'BidQualityPassportController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: ['Bid', function (Bid) {
                                return Bid.get({id: $stateParams.bidId}).$promise;
                            }]
                        }
                    }).result.then(function () {
                            $state.go('^', {}, {reload: false});
                        }, function () {
                            $state.go('^');
                        });
                }]
            })
            .state('market.partner-card', {
                parent: 'market',
                url: '/{partnerId}/card',
                data: {
                    authorities: ['ROLE_USER']
                },
                onEnter: ['$stateParams', '$state', 'PartnerCard', 'Partner', function ($stateParams, $state, PartnerCard, Partner) {
                    var partner = Partner.get({id: $stateParams.partnerId});
                    PartnerCard.showDialog(partner).result.then(function () {
                            $state.go('^', {}, {reload: false});
                        }, function () {
                            $state.go('^');
                        });
                }]
            });
    }
})();
