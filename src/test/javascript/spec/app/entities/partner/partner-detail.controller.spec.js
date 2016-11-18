'use strict';
/*

describe('Controller Tests', function() {

    describe('Partner Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockPartner, MockBid, MockOrganisationType, MockDistrict, MockRegion, MockLocality, MockStation, MockContact, MockServicePrice;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockPartner = jasmine.createSpy('MockPartner');
            MockBid = jasmine.createSpy('MockBid');
            MockOrganisationType = jasmine.createSpy('MockOrganisationType');
            MockDistrict = jasmine.createSpy('MockDistrict');
            MockRegion = jasmine.createSpy('MockRegion');
            MockLocality = jasmine.createSpy('MockLocality');
            MockStation = jasmine.createSpy('MockStation');
            MockContact = jasmine.createSpy('MockContact');
            MockServicePrice = jasmine.createSpy('MockServicePrice');


            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Partner': MockPartner,
                'Bid': MockBid,
                'OrganisationType': MockOrganisationType,
                'District': MockDistrict,
                'Region': MockRegion,
                'Locality': MockLocality,
                'Station': MockStation,
                'Contact': MockContact,
                'ServicePrice': MockServicePrice
            };
            createController = function() {
                $injector.get('$controller')("PartnerDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'grainAdminApp:partnerUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
*/
