package pro.grain.admin.service.mapper;

import pro.grain.admin.domain.*;
import pro.grain.admin.service.dto.OrganisationTypeDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity OrganisationType and its DTO OrganisationTypeDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface OrganisationTypeMapper {

    OrganisationTypeDTO organisationTypeToOrganisationTypeDTO(OrganisationType organisationType);

    List<OrganisationTypeDTO> organisationTypesToOrganisationTypeDTOs(List<OrganisationType> organisationTypes);

    OrganisationType organisationTypeDTOToOrganisationType(OrganisationTypeDTO organisationTypeDTO);

    List<OrganisationType> organisationTypeDTOsToOrganisationTypes(List<OrganisationTypeDTO> organisationTypeDTOs);
}
