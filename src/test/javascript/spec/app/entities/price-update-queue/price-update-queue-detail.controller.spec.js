'use strict';

describe('Controller Tests', function() {

    describe('PriceUpdateQueue Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockPriceUpdateQueue, MockStation;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockPriceUpdateQueue = jasmine.createSpy('MockPriceUpdateQueue');
            MockStation = jasmine.createSpy('MockStation');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'PriceUpdateQueue': MockPriceUpdateQueue,
                'Station': MockStation
            };
            createController = function() {
                $injector.get('$controller')("PriceUpdateQueueDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'grainAdminApp:priceUpdateQueueUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
