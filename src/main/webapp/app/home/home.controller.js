(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', 'LoginService', '$state'];

    function HomeController ($scope, Principal, LoginService, $state) {
        var vm = this;

        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = LoginService.open;
        vm.register = register;

        $scope.$on('authenticationSuccess', function() {
            getAccount();
        });

        if (vm.isAuthenticated && vm.isAuthenticated()) {
            $state.go('partner');
        }

        getAccount();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;

                if (vm.isAuthenticated && vm.isAuthenticated()) {
                    $state.go('partner');
                }
            });
        }
        function register () {
            $state.go('register');
        }
    }
})();
