package pro.grain.admin.service;

import pro.grain.admin.domain.Contact;
import pro.grain.admin.domain.Partner;
import pro.grain.admin.repository.ContactRepository;
import pro.grain.admin.repository.PartnerRepository;
import pro.grain.admin.repository.search.ContactSearchRepository;
import pro.grain.admin.repository.search.PartnerSearchRepository;
import pro.grain.admin.service.dto.ContactDTO;
import pro.grain.admin.service.mapper.ContactMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Contact.
 */
@Service
@Transactional
public class ContactService {

    private final Logger log = LoggerFactory.getLogger(ContactService.class);

    @Inject
    private ContactRepository contactRepository;

    @Inject
    private ContactMapper contactMapper;

    @Inject
    private ContactSearchRepository contactSearchRepository;

    @Inject
    private PartnerSearchRepository partnerSearchRepository;

    @Inject
    private PartnerRepository partnerRepository;

    /**
     * Save a contact.
     *
     * @param contactDTO the entity to save
     * @return the persisted entity
     */
    public ContactDTO save(ContactDTO contactDTO) {
        log.debug("Request to save Contact : {}", contactDTO);
        Contact contact = contactMapper.contactDTOToContact(contactDTO);
        contact = contactRepository.save(contact);
        ContactDTO result = contactMapper.contactToContactDTO(contact);
        contactSearchRepository.save(contact);
        return result;
    }

    public void updateRelatedObjectsForSearch() {
        List<Partner> partners = partnerRepository.findAll();
        if (partners != null && partners.size() > 0) {
            partnerSearchRepository.save(partners);
        }
    }

    /**
     *  Get all the contacts.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ContactDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Contacts");
        Page<Contact> result = contactRepository.findAll(pageable);
        return result.map(contact -> contactMapper.contactToContactDTO(contact));
    }

    /**
     *  Get one contact by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public ContactDTO findOne(Long id) {
        log.debug("Request to get Contact : {}", id);
        Contact contact = contactRepository.findOne(id);
        ContactDTO contactDTO = contactMapper.contactToContactDTO(contact);
        return contactDTO;
    }

    /**
     *  Delete the  contact by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Contact : {}", id);
        contactRepository.delete(id);
        contactSearchRepository.delete(id);
    }

    /**
     * Search for the contact corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ContactDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Contacts for query {}", query);
        Page<Contact> result = contactSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(contact -> contactMapper.contactToContactDTO(contact));
    }
}
