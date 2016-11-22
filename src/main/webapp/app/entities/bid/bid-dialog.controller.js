(function() {
    'use strict';

    angular
        .module('grainAdminApp')
        .controller('BidDialogController', BidDialogController);

    BidDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'partner',
        'Bid', 'Contact', 'QualityParameter', 'Partner', 'QualityValue', '$q', 'Passport', 'DataUtils'];

    function BidDialogController($timeout, $scope, $stateParams, $uibModalInstance, entity, partner,
                                 Bid, Contact, QualityParameter, Partner, QualityValue, $q, Passport, DataUtils) {
        var vm = this;

        vm.bid = entity;
        vm.currentPartner = partner;

        if (!vm.bid.agentId) {
            vm.bid.agentId = vm.currentPartner.id;
        }
        if (vm.currentPartner.nds && vm.currentPartner.nds !== "BOTH") {
            vm.bid.nds = vm.currentPartner.nds;
        }

        vm.clear = clear;
        vm.save = save;
        vm.contacts = Contact.query();
        vm.qualityParameters = QualityParameter.query();
        vm.partners = Partner.query();
        vm.getPartnersSuggestions = getPartnersSuggestions;
        vm.formatSelection = formatSelection;
        vm.addQualityPassport = addQualityPassport;
        vm.files = null;

        var emptyQualityValue = {
            qualityParameter: null,
            value: null
        };
        vm.selectedQualityValues = [1, 2, 3, 4].map(function () {
            return angular.copy(emptyQualityValue);
        });

        vm.selectedElevator = null;

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
                Bid.update(vm.bid, onSaveSuccess, onSaveError);
            } else {
                Bid.save(vm.bid, onSaveSuccess, onSaveError);
            }
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
        }

        function onSaveSuccess(result) {
            $scope.$emit('grainAdminApp:bidUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError() {
            vm.isSaving = false;
        }

        function getPartnersSuggestions() {
            return vm.partners.filter(function (partner) {
                return partner.id !== vm.currentPartner.id;
            });
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

            DataUtils.toBase64(file, function(base64Data) {
                $scope.$apply(function() {
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
