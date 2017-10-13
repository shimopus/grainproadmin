'use strict';

describe('Controller Tests', function() {

    describe('SubscriptionConfig Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockSubscriptionConfig, MockContact, MockStation;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockSubscriptionConfig = jasmine.createSpy('MockSubscriptionConfig');
            MockContact = jasmine.createSpy('MockContact');
            MockStation = jasmine.createSpy('MockStation');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'SubscriptionConfig': MockSubscriptionConfig,
                'Contact': MockContact,
                'Station': MockStation
            };
            createController = function() {
                $injector.get('$controller')("SubscriptionConfigDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'grainAdminApp:subscriptionConfigUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
