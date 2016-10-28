package pro.grain.admin.service.mapper;

import pro.grain.admin.domain.*;
import pro.grain.admin.service.dto.PartnerDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Partner and its DTO PartnerDTO.
 */
@Mapper(componentModel = "spring", uses = {ContactMapper.class, ServicePriceMapper.class, })
public interface PartnerMapper {

    @Mapping(source = "organisationType.id", target = "organisationTypeId")
    @Mapping(source = "organisationType.type", target = "organisationTypeType")
    @Mapping(source = "district.id", target = "districtId")
    @Mapping(source = "district.name", target = "districtName")
    @Mapping(source = "region.id", target = "regionId")
    @Mapping(source = "region.name", target = "regionName")
    @Mapping(source = "locality.id", target = "localityId")
    @Mapping(source = "locality.name", target = "localityName")
    @Mapping(source = "station.id", target = "stationId")
    @Mapping(source = "station.code", target = "stationCode")
    @Mapping(source = "ownerFor.id", target = "ownerForId")
    @Mapping(source = "ownerFor.name", target = "ownerForName")
    PartnerDTO partnerToPartnerDTO(Partner partner);

    List<PartnerDTO> partnersToPartnerDTOs(List<Partner> partners);

    @Mapping(target = "agentBids", ignore = true)
    @Mapping(target = "elevatorBids", ignore = true)
//    @Mapping(target = "ownedBies", ignore = true)
    @Mapping(source = "organisationTypeId", target = "organisationType")
    @Mapping(source = "districtId", target = "district")
    @Mapping(source = "regionId", target = "region")
    @Mapping(source = "localityId", target = "locality")
    @Mapping(source = "stationId", target = "station")
    @Mapping(source = "ownerForId", target = "ownerFor")
    Partner partnerDTOToPartner(PartnerDTO partnerDTO);

    List<Partner> partnerDTOsToPartners(List<PartnerDTO> partnerDTOs);

    default OrganisationType organisationTypeFromId(Long id) {
        if (id == null) {
            return null;
        }
        OrganisationType organisationType = new OrganisationType();
        organisationType.setId(id);
        return organisationType;
    }

    default District districtFromId(Long id) {
        if (id == null) {
            return null;
        }
        District district = new District();
        district.setId(id);
        return district;
    }

    default Region regionFromId(Long id) {
        if (id == null) {
            return null;
        }
        Region region = new Region();
        region.setId(id);
        return region;
    }

    default Locality localityFromId(Long id) {
        if (id == null) {
            return null;
        }
        Locality locality = new Locality();
        locality.setId(id);
        return locality;
    }

    default Station stationFromId(Long id) {
        if (id == null) {
            return null;
        }
        Station station = new Station();
        station.setId(id);
        return station;
    }

    default Contact contactFromId(Long id) {
        if (id == null) {
            return null;
        }
        Contact contact = new Contact();
        contact.setId(id);
        return contact;
    }

    default ServicePrice servicePriceFromId(Long id) {
        if (id == null) {
            return null;
        }
        ServicePrice servicePrice = new ServicePrice();
        servicePrice.setId(id);
        return servicePrice;
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
