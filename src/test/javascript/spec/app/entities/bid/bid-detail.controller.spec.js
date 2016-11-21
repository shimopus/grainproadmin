'use strict';

describe('Controller Tests', function() {

    describe('Bid Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockBid, MockContact, MockQualityValue, MockPartner, MockPassport;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockBid = jasmine.createSpy('MockBid');
            MockContact = jasmine.createSpy('MockContact');
            MockQualityValue = jasmine.createSpy('MockQualityValue');
            MockPartner = jasmine.createSpy('MockPartner');
            MockPassport = jasmine.createSpy('MockPassport');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Bid': MockBid,
                'Contact': MockContact,
                'QualityValue': MockQualityValue,
                'Partner': MockPartner,
                'Passport': MockPassport
            };
            createController = function() {
                $injector.get('$controller')("BidDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'grainAdminApp:bidUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
