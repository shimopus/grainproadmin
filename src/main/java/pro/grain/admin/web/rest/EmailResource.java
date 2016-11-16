package pro.grain.admin.web.rest;

import com.codahale.metrics.annotation.Timed;
import pro.grain.admin.service.EmailService;
import pro.grain.admin.web.rest.util.HeaderUtil;
import pro.grain.admin.web.rest.util.PaginationUtil;
import pro.grain.admin.service.dto.EmailDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Email.
 */
@RestController
@RequestMapping("/api")
public class EmailResource {

    private final Logger log = LoggerFactory.getLogger(EmailResource.class);

    @Inject
    private EmailService emailService;

    /**
     * POST  /emails : Create a new email.
     *
     * @param emailDTO the emailDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new emailDTO, or with status 400 (Bad Request) if the email has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/emails",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<EmailDTO> createEmail(@Valid @RequestBody EmailDTO emailDTO) throws URISyntaxException {
        log.debug("REST request to save Email : {}", emailDTO);
        if (emailDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("email", "idexists", "A new email cannot already have an ID")).body(null);
        }
//        EmailDTO savedEmail = emailService.
        EmailDTO result = emailService.save(emailDTO);
        return ResponseEntity.created(new URI("/api/emails/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("email", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /emails : Updates an existing email.
     *
     * @param emailDTO the emailDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated emailDTO,
     * or with status 400 (Bad Request) if the emailDTO is not valid,
     * or with status 500 (Internal Server Error) if the emailDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/emails",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<EmailDTO> updateEmail(@Valid @RequestBody EmailDTO emailDTO) throws URISyntaxException {
        log.debug("REST request to update Email : {}", emailDTO);
        if (emailDTO.getId() == null) {
            return createEmail(emailDTO);
        }
        EmailDTO result = emailService.save(emailDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("email", emailDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /emails : get all the emails.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of emails in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/emails",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<EmailDTO>> getAllEmails(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Emails");
        Page<EmailDTO> page = emailService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/emails");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /emails/:id : get the "id" email.
     *
     * @param id the id of the emailDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the emailDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/emails/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<EmailDTO> getEmail(@PathVariable Long id) {
        log.debug("REST request to get Email : {}", id);
        EmailDTO emailDTO = emailService.findOne(id);
        return Optional.ofNullable(emailDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /emails/:id : delete the "id" email.
     *
     * @param id the id of the emailDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/emails/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteEmail(@PathVariable Long id) {
        log.debug("REST request to delete Email : {}", id);
        emailService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("email", id.toString())).build();
    }

    /**
     * SEARCH  /_search/emails?query=:query : search for the email corresponding
     * to the query.
     *
     * @param query the query of the email search
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/_search/emails",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<EmailDTO>> searchEmails(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Emails for query {}", query);
        Page<EmailDTO> page = emailService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/emails");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
