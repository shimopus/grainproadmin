package pro.grain.admin.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pro.grain.admin.domain.Passport;
import pro.grain.admin.service.dto.PassportDTO;

import java.util.List;

@Mapper(componentModel = "spring", uses = {})
public interface PassportNoImageMapper {
    @Mapping(target = "image", ignore = true)
    PassportDTO passportToPassportDTO(Passport passport);

    List<PassportDTO> passportsToPassportDTOs(List<Passport> passports);

    @Mapping(target = "image", ignore = true)
    Passport passportDTOToPassport(PassportDTO passportDTO);

    List<Passport> passportDTOsToPassports(List<PassportDTO> passportDTOs);
}
