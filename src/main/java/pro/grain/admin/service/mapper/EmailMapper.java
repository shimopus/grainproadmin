package pro.grain.admin.service.mapper;

import pro.grain.admin.domain.*;
import pro.grain.admin.service.dto.EmailDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Email and its DTO EmailDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface EmailMapper {

    EmailDTO emailToEmailDTO(Email email);

    List<EmailDTO> emailsToEmailDTOs(List<Email> emails);

    Email emailDTOToEmail(EmailDTO emailDTO);

    List<Email> emailDTOsToEmails(List<EmailDTO> emailDTOs);
}
