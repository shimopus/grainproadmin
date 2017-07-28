(function () {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('BidDialogController', BidDialogController);

    BidDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'partner',
        'Bid', 'QualityParameter', 'Partner', 'QualityValue', '$q', 'Passport', 'DataUtils', "PartnerSearch"];

    function BidDialogController($timeout, $scope, $stateParams, $uibModalInstance, entity, partner,
                                 Bid, QualityParameter, Partner, QualityValue, $q, Passport, DataUtils, PartnerSearch) {
        var vm = this;

        if (entity.id) {
            vm.initialBid = angular.copy(entity);
        }

        vm.bid = entity;
        vm.currentPartner = partner;

        if (!vm.bid.agentId) {
            vm.bid.agentId = vm.currentPartner.id;
        }
        //if it is creation
        if (vm.bid.id === null && vm.currentPartner.nds && vm.currentPartner.nds !== "BOTH") {
            vm.bid.nds = vm.currentPartner.nds;
        }

        vm.clear = clear;
        vm.save = save;
        vm.qualityParameters = QualityParameter.query();
        vm.qualityParametersMaskOptions = {
            maskDefinitions: {
                '9': /[0-9,.]/
            }
        };
        vm.partners = Partner.query();
        vm.getPartnersSuggestions = getPartnersSuggestions;
        vm.formatSelection = formatSelection;
        vm.addQualityPassport = addQualityPassport;
        vm.files = null;
        vm.bid.bidType = vm.bid.bidType || 'SELL';

        var emptyQualityValue = {
            qualityParameter: null,
            value: null
        };

        //if it is new bid - just create empty array for quality parameters
        if (vm.bid.id === null) {
            vm.selectedQualityValues = [1, 2, 3, 4].map(function () {
                return angular.copy(emptyQualityValue);
            });
        } else {
            vm.selectedQualityValues = vm.bid.qualityParameters.map(function (qualityValue) {
                    if (qualityValue.qualityParameterId) {
                        return {
                            id: qualityValue.id,
                            qualityParameter: {
                                id: qualityValue.qualityParameterId,
                                name: qualityValue.qualityParameterName,
                                unit: qualityValue.qualityParameterUnit
                            },
                            value: qualityValue.value
                        };
                    }
                }
            );

            if (vm.selectedQualityValues.length < 4) {
                for (var i = vm.selectedQualityValues.length; i < 4; i++) {
                    vm.selectedQualityValues.push(angular.copy(emptyQualityValue));
                }
            }
        }

        vm.selectedElevator = null;

        if (vm.bid.elevatorId) {
            vm.selectedElevator = Partner.get({id: vm.bid.elevatorId});
        }

        $timeout(function () {
            angular.element('.form-group:eq(0)>input').focus();
        });

        function clear() {
            $uibModalInstance.dismiss('cancel');
        }

        function save() {
            vm.isSaving = true;
            updateQualityParameters();
            updateElevatorParameter();
            if (vm.bid.id !== null) {
                updateBid(vm.bid);
            } else {
                Bid.save(vm.bid,
                    function(resultBid) {
                        onSaveSuccess(resultBid);
                    }, onSaveError);
            }
        }

        function updateBid(bid) {
            //archive initial bid
            vm.initialBid.archiveDate = new Date();
            Bid.update(vm.initialBid);

            //create new edited bid
            bid.id = null;
            bid.qualityParameters.forEach(function (qualityParameter) {
                qualityParameter.id = null;
            });
            bid.qualityPassports.forEach(function (qualityPassport) {
                qualityPassport.id = null;
            });
            bid.creationDate = new Date();
            Bid.save(bid,
                function(resultBid) {
                    //modify Bids just to be displayed on the screen
                    resultBid.elevator = {
                        name: vm.initialBid.elevatorName
                    };
                    vm.initialBid.elevator = {
                        name: vm.initialBid.elevatorName
                    };
                    onSaveSuccess(resultBid, vm.initialBid);
                },
                onSaveError);
        }

        function updateQualityParameters() {
            vm.bid.qualityParameters = vm.selectedQualityValues
                .map(function (qualityValue) {
                    if (qualityValue.qualityParameter) {
                        return {
                            id: null,
                            qualityParameterId: qualityValue.qualityParameter.id,
                            value: qualityValue.value
                        };
                    }
                })
                .filter(function (elm) {
                    return angular.isDefined(elm);
                });
        }

        function updateElevatorParameter() {
            vm.bid.elevatorId = vm.selectedElevator.id;
            vm.bid.elevatorName = vm.selectedElevator.name;
        }

        function onSaveSuccess(newBid, initialBid) {
            $scope.$emit('grainAdminApp:bidUpdate', {
                newBid: newBid,
                initialBid: initialBid
            });
            $uibModalInstance.close(newBid);
            vm.isSaving = false;
        }

        function onSaveError() {
            vm.isSaving = false;
        }

        function getPartnersSuggestions(partnerName) {
            return PartnerSearch.query({
                query: partnerName,
                page: 0,
                size: 20,
                sort: 'asc'
            }).$promise;
        }

        function formatSelection(selectedValue, objects, parameterName) {
            if (!selectedValue) return "";

            return objects.find(function (object) {
                return object.id === selectedValue;
            })[parameterName];
        }

        $scope.$watch('vm.files', function () {
            vm.addQualityPassport(vm.files);
        });

        function addQualityPassport(files) {
            if (files && files.length) {
                for (var i = 0; i < files.length; i++) {
                    var file = files[i];
                    if (!file.$error) {
                        addQualityPassportByFile(file);
                    }
                }
            }
        }

        function addQualityPassportByFile(file) {
            var passport = {
                id: null,
                imageContentType: file.type,
                title: file.name
            };

            DataUtils.toBase64(file, function (base64Data) {
                $scope.$apply(function () {
                    passport.image = base64Data;
                });
            });

            if (!vm.bid.qualityPassports) {
                vm.bid.qualityPassports = [];
            }
            vm.bid.qualityPassports.push(passport);
        }
    }
})();
