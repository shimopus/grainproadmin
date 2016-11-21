package pro.grain.admin.service.mapper;

import pro.grain.admin.domain.*;
import pro.grain.admin.service.dto.PassportDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Passport and its DTO PassportDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface PassportMapper {

    PassportDTO passportToPassportDTO(Passport passport);

    List<PassportDTO> passportsToPassportDTOs(List<Passport> passports);

    Passport passportDTOToPassport(PassportDTO passportDTO);

    List<Passport> passportDTOsToPassports(List<PassportDTO> passportDTOs);
}
