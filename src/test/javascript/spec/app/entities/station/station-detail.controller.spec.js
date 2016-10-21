'use strict';

describe('Controller Tests', function() {

    describe('Station Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockStation, MockDistrict, MockRegion, MockLocality;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockStation = jasmine.createSpy('MockStation');
            MockDistrict = jasmine.createSpy('MockDistrict');
            MockRegion = jasmine.createSpy('MockRegion');
            MockLocality = jasmine.createSpy('MockLocality');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Station': MockStation,
                'District': MockDistrict,
                'Region': MockRegion,
                'Locality': MockLocality
            };
            createController = function() {
                $injector.get('$controller')("StationDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'grainAdminApp:stationUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
