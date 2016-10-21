package pro.grain.admin.service.mapper;

import pro.grain.admin.domain.*;
import pro.grain.admin.service.dto.RegionDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Region and its DTO RegionDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface RegionMapper {

    RegionDTO regionToRegionDTO(Region region);

    List<RegionDTO> regionsToRegionDTOs(List<Region> regions);

    Region regionDTOToRegion(RegionDTO regionDTO);

    List<Region> regionDTOsToRegions(List<RegionDTO> regionDTOs);
}
