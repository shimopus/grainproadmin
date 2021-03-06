package pro.grain.admin.service.mapper;

import pro.grain.admin.domain.*;
import pro.grain.admin.service.dto.SubscriptionConfigDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity SubscriptionConfig and its DTO SubscriptionConfigDTO.
 */
@Mapper(componentModel = "spring", uses = {EmailMapper.class})
public interface SubscriptionConfigMapper {

    @Mapping(source = "contact.id", target = "contactId")
    @Mapping(source = "contact.personName", target = "contactPersonName")
    @Mapping(source = "contact.email.email", target = "contactEmail")
    @Mapping(source = "station.id", target = "stationId")
    @Mapping(source = "station.name", target = "stationName")
    @Mapping(source = "station.code", target = "stationCode")
    @Mapping(source = "partner.id", target = "partnerId")
    @Mapping(source = "partner.name", target = "partnerName")
    SubscriptionConfigDTO subscriptionConfigToSubscriptionConfigDTO(SubscriptionConfig subscriptionConfig);

    List<SubscriptionConfigDTO> subscriptionConfigsToSubscriptionConfigDTOs(List<SubscriptionConfig> subscriptionConfigs);

    @Mapping(source = "contactId", target = "contact")
    @Mapping(source = "stationId", target = "station")
    @Mapping(source = "partnerId", target = "partner")
    SubscriptionConfig subscriptionConfigDTOToSubscriptionConfig(SubscriptionConfigDTO subscriptionConfigDTO);

    List<SubscriptionConfig> subscriptionConfigDTOsToSubscriptionConfigs(List<SubscriptionConfigDTO> subscriptionConfigDTOs);

    default Contact contactFromId(Long id) {
        if (id == null) {
            return null;
        }
        Contact contact = new Contact();
        contact.setId(id);
        return contact;
    }

    default Station stationFromId(Long id) {
        if (id == null) {
            return null;
        }
        Station station = new Station();
        station.setId(id);
        return station;
    }

    default Partner partnerFromId(Long id) {
        if (id == null) {
            return null;
        }
        Partner partner = new Partner();
        partner.setId(id);
        return partner;
    }
}
