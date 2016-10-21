package pro.grain.admin.service.mapper;

import pro.grain.admin.domain.*;
import pro.grain.admin.service.dto.ServiceTypeDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity ServiceType and its DTO ServiceTypeDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ServiceTypeMapper {

    ServiceTypeDTO serviceTypeToServiceTypeDTO(ServiceType serviceType);

    List<ServiceTypeDTO> serviceTypesToServiceTypeDTOs(List<ServiceType> serviceTypes);

    ServiceType serviceTypeDTOToServiceType(ServiceTypeDTO serviceTypeDTO);

    List<ServiceType> serviceTypeDTOsToServiceTypes(List<ServiceTypeDTO> serviceTypeDTOs);
}
