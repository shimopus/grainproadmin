package pro.grain.admin.service.mapper;

import pro.grain.admin.domain.*;
import pro.grain.admin.service.dto.StationDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Station and its DTO StationDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface StationMapper {

    @Mapping(source = "district.id", target = "districtId")
    @Mapping(source = "district.name", target = "districtName")
    @Mapping(source = "region.id", target = "regionId")
    @Mapping(source = "region.name", target = "regionName")
    @Mapping(source = "locality.id", target = "localityId")
    @Mapping(source = "locality.name", target = "localityName")
    StationDTO stationToStationDTO(Station station);

    List<StationDTO> stationsToStationDTOs(List<Station> stations);

    @Mapping(source = "districtId", target = "district")
    @Mapping(source = "regionId", target = "region")
    @Mapping(source = "localityId", target = "locality")
    Station stationDTOToStation(StationDTO stationDTO);

    List<Station> stationDTOsToStations(List<StationDTO> stationDTOs);

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
}
