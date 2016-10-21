package pro.grain.admin.service.mapper;

import pro.grain.admin.domain.*;
import pro.grain.admin.service.dto.ServicePriceDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity ServicePrice and its DTO ServicePriceDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ServicePriceMapper {

    @Mapping(source = "serviceType.id", target = "serviceTypeId")
    @Mapping(source = "serviceType.name", target = "serviceTypeName")
    ServicePriceDTO servicePriceToServicePriceDTO(ServicePrice servicePrice);

    List<ServicePriceDTO> servicePricesToServicePriceDTOs(List<ServicePrice> servicePrices);

    @Mapping(source = "serviceTypeId", target = "serviceType")
    ServicePrice servicePriceDTOToServicePrice(ServicePriceDTO servicePriceDTO);

    List<ServicePrice> servicePriceDTOsToServicePrices(List<ServicePriceDTO> servicePriceDTOs);

    default ServiceType serviceTypeFromId(Long id) {
        if (id == null) {
            return null;
        }
        ServiceType serviceType = new ServiceType();
        serviceType.setId(id);
        return serviceType;
    }
}
