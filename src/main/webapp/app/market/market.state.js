(function() {
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
                bids: ['Market', function (Market) {
                    return Market.query().$promise;
                }],
                mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                    $translatePartialLoader.addPart('market');
                    $translatePartialLoader.addPart('bid');
                    $translatePartialLoader.addPart('nDS');
                    $translatePartialLoader.addPart('qualityClass');
                    return $translate.refresh();
                }]
            }
        });
    }
})();
