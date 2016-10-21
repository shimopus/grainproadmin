package pro.grain.admin.service.mapper;

import pro.grain.admin.domain.*;
import pro.grain.admin.service.dto.LocalityDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Locality and its DTO LocalityDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface LocalityMapper {

    LocalityDTO localityToLocalityDTO(Locality locality);

    List<LocalityDTO> localitiesToLocalityDTOs(List<Locality> localities);

    Locality localityDTOToLocality(LocalityDTO localityDTO);

    List<Locality> localityDTOsToLocalities(List<LocalityDTO> localityDTOs);
}
