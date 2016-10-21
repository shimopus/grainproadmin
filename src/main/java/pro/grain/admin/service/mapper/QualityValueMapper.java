package pro.grain.admin.service.mapper;

import pro.grain.admin.domain.*;
import pro.grain.admin.service.dto.QualityValueDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity QualityValue and its DTO QualityValueDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface QualityValueMapper {

    @Mapping(source = "qualityParameter.id", target = "qualityParameterId")
    @Mapping(source = "qualityParameter.name", target = "qualityParameterName")
    QualityValueDTO qualityValueToQualityValueDTO(QualityValue qualityValue);

    List<QualityValueDTO> qualityValuesToQualityValueDTOs(List<QualityValue> qualityValues);

    @Mapping(source = "qualityParameterId", target = "qualityParameter")
    QualityValue qualityValueDTOToQualityValue(QualityValueDTO qualityValueDTO);

    List<QualityValue> qualityValueDTOsToQualityValues(List<QualityValueDTO> qualityValueDTOs);

    default QualityParameter qualityParameterFromId(Long id) {
        if (id == null) {
            return null;
        }
        QualityParameter qualityParameter = new QualityParameter();
        qualityParameter.setId(id);
        return qualityParameter;
    }
}
