package pro.grain.admin.service.mapper;

import org.aspectj.lang.annotation.After;
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

    @AfterMapping
    default void createOrUpdateEmail(@MappingTarget Contact contact, ContactDTO contactDTO){
        if (contact.getEmail() == null && contactDTO.getEmailEmail() != null) {
            Email email = new Email();
            email.setId(contactDTO.getEmailId());
            email.setEmail(contactDTO.getEmailEmail());
            contact.setEmail(email);
        } else if (contact.getEmail() != null) {
            contact.getEmail().setEmail(contactDTO.getEmailEmail());
        }
    }
}
