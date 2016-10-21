package pro.grain.admin.service.mapper;

import pro.grain.admin.domain.*;
import pro.grain.admin.service.dto.TransportationPriceDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity TransportationPrice and its DTO TransportationPriceDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface TransportationPriceMapper {

    @Mapping(source = "stationFrom.id", target = "stationFromId")
    @Mapping(source = "stationFrom.code", target = "stationFromCode")
    @Mapping(source = "stationTo.id", target = "stationToId")
    @Mapping(source = "stationTo.code", target = "stationToCode")
    TransportationPriceDTO transportationPriceToTransportationPriceDTO(TransportationPrice transportationPrice);

    List<TransportationPriceDTO> transportationPricesToTransportationPriceDTOs(List<TransportationPrice> transportationPrices);

    @Mapping(source = "stationFromId", target = "stationFrom")
    @Mapping(source = "stationToId", target = "stationTo")
    TransportationPrice transportationPriceDTOToTransportationPrice(TransportationPriceDTO transportationPriceDTO);

    List<TransportationPrice> transportationPriceDTOsToTransportationPrices(List<TransportationPriceDTO> transportationPriceDTOs);

    default Station stationFromId(Long id) {
        if (id == null) {
            return null;
        }
        Station station = new Station();
        station.setId(id);
        return station;
    }
}
