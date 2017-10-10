package pro.grain.admin.service.mapper;

import pro.grain.admin.domain.*;
import pro.grain.admin.service.dto.TrackingDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Tracking and its DTO TrackingDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface TrackingMapper {

    @Mapping(source = "partner.id", target = "partnerId")
    @Mapping(source = "partner.name", target = "partnerName")
    TrackingDTO trackingToTrackingDTO(Tracking tracking);

    List<TrackingDTO> trackingsToTrackingDTOs(List<Tracking> trackings);

    @Mapping(source = "partnerId", target = "partner")
    Tracking trackingDTOToTracking(TrackingDTO trackingDTO);

    List<Tracking> trackingDTOsToTrackings(List<TrackingDTO> trackingDTOs);

    default Partner partnerFromId(Long id) {
        if (id == null) {
            return null;
        }
        Partner partner = new Partner();
        partner.setId(id);
        return partner;
    }
}
