package pro.grain.admin.service;

import pro.grain.admin.domain.Email;
import pro.grain.admin.repository.EmailRepository;
import pro.grain.admin.repository.search.EmailSearchRepository;
import pro.grain.admin.service.dto.EmailDTO;
import pro.grain.admin.service.mapper.EmailMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Email.
 */
@Service
@Transactional
public class EmailService {

    private final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Inject
    private EmailRepository emailRepository;

    @Inject
    private EmailMapper emailMapper;

    @Inject
    private EmailSearchRepository emailSearchRepository;

    /**
     * Save a email.
     *
     * @param emailDTO the entity to save
     * @return the persisted entity
     */
    public EmailDTO save(EmailDTO emailDTO) {
        log.debug("Request to save Email : {}", emailDTO);
        Email email = emailMapper.emailDTOToEmail(emailDTO);
        email = emailRepository.save(email);
        EmailDTO result = emailMapper.emailToEmailDTO(email);
        emailSearchRepository.save(email);
        return result;
    }

    /**
     *  Get all the emails.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<EmailDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Emails");
        Page<Email> result = emailRepository.findAll(pageable);
        return result.map(email -> emailMapper.emailToEmailDTO(email));
    }

    /**
     *  Get one email by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public EmailDTO findOne(Long id) {
        log.debug("Request to get Email : {}", id);
        Email email = emailRepository.findOne(id);
        EmailDTO emailDTO = emailMapper.emailToEmailDTO(email);
        return emailDTO;
    }

    /**
     *  Delete the  email by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Email : {}", id);
        emailRepository.delete(id);
        emailSearchRepository.delete(id);
    }

    /**
     * Search for the email corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<EmailDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Emails for query {}", query);
        Page<Email> result = emailSearchRepository.search(termQuery("email", query), pageable);
        return result.map(email -> emailMapper.emailToEmailDTO(email));
    }
}
