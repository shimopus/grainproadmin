package pro.grain.admin.service.mapper;

import pro.grain.admin.domain.*;
import pro.grain.admin.service.dto.QualityParameterDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity QualityParameter and its DTO QualityParameterDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface QualityParameterMapper {

    QualityParameterDTO qualityParameterToQualityParameterDTO(QualityParameter qualityParameter);

    List<QualityParameterDTO> qualityParametersToQualityParameterDTOs(List<QualityParameter> qualityParameters);

    QualityParameter qualityParameterDTOToQualityParameter(QualityParameterDTO qualityParameterDTO);

    List<QualityParameter> qualityParameterDTOsToQualityParameters(List<QualityParameterDTO> qualityParameterDTOs);
}
