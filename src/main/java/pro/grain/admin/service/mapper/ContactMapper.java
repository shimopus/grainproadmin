package pro.grain.admin.service.mapper;

import pro.grain.admin.domain.*;
import pro.grain.admin.service.dto.ContactDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Contact and its DTO ContactDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ContactMapper {

    @Mapping(source = "email.id", target = "emailId")
    @Mapping(source = "email.email", target = "emailEmail")
    ContactDTO contactToContactDTO(Contact contact);

    List<ContactDTO> contactsToContactDTOs(List<Contact> contacts);

    @Mapping(source = "emailId", target = "email")
    Contact contactDTOToContact(ContactDTO contactDTO);

    List<Contact> contactDTOsToContacts(List<ContactDTO> contactDTOs);

    default Email emailFromId(Long id) {
        if (id == null) {
            return null;
        }
        Email email = new Email();
        email.setId(id);
        return email;
    }
}
