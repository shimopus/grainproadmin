package pro.grain.admin.service.mapper;

import pro.grain.admin.domain.*;
import pro.grain.admin.service.dto.PriceUpdateQueueDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity PriceUpdateQueue and its DTO PriceUpdateQueueDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface PriceUpdateQueueMapper {

    @Mapping(source = "stationFrom.id", target = "stationFromId")
    @Mapping(source = "stationFrom.name", target = "stationFromName")
    @Mapping(source = "stationTo.id", target = "stationToId")
    @Mapping(source = "stationTo.name", target = "stationToName")
    PriceUpdateQueueDTO priceUpdateQueueToPriceUpdateQueueDTO(PriceUpdateQueue priceUpdateQueue);

    List<PriceUpdateQueueDTO> priceUpdateQueuesToPriceUpdateQueueDTOs(List<PriceUpdateQueue> priceUpdateQueues);

    @Mapping(source = "stationFromId", target = "stationFrom")
    @Mapping(source = "stationToId", target = "stationTo")
    PriceUpdateQueue priceUpdateQueueDTOToPriceUpdateQueue(PriceUpdateQueueDTO priceUpdateQueueDTO);

    List<PriceUpdateQueue> priceUpdateQueueDTOsToPriceUpdateQueues(List<PriceUpdateQueueDTO> priceUpdateQueueDTOs);

    default Station stationFromId(Long id) {
        if (id == null) {
            return null;
        }
        Station station = new Station();
        station.setId(id);
        return station;
    }
}
