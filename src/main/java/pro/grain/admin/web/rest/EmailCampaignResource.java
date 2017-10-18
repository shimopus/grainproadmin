package pro.grain.admin.web.rest;

import com.codahale.metrics.annotation.Timed;
import pro.grain.admin.service.EmailCampaignService;
import pro.grain.admin.web.rest.util.HeaderUtil;
import pro.grain.admin.web.rest.util.PaginationUtil;
import pro.grain.admin.service.dto.EmailCampaignDTO;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing EmailCampaign.
 */
@RestController
@RequestMapping("/api")
public class EmailCampaignResource {

    private final Logger log = LoggerFactory.getLogger(EmailCampaignResource.class);
        
    @Inject
    private EmailCampaignService emailCampaignService;

    /**
     * POST  /email-campaigns : Create a new emailCampaign.
     *
     * @param emailCampaignDTO the emailCampaignDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new emailCampaignDTO, or with status 400 (Bad Request) if the emailCampaign has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/email-campaigns",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<EmailCampaignDTO> createEmailCampaign(@RequestBody EmailCampaignDTO emailCampaignDTO) throws URISyntaxException {
        log.debug("REST request to save EmailCampaign : {}", emailCampaignDTO);
        if (emailCampaignDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("emailCampaign", "idexists", "A new emailCampaign cannot already have an ID")).body(null);
        }
        EmailCampaignDTO result = emailCampaignService.save(emailCampaignDTO);
        return ResponseEntity.created(new URI("/api/email-campaigns/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("emailCampaign", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /email-campaigns : Updates an existing emailCampaign.
     *
     * @param emailCampaignDTO the emailCampaignDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated emailCampaignDTO,
     * or with status 400 (Bad Request) if the emailCampaignDTO is not valid,
     * or with status 500 (Internal Server Error) if the emailCampaignDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/email-campaigns",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<EmailCampaignDTO> updateEmailCampaign(@RequestBody EmailCampaignDTO emailCampaignDTO) throws URISyntaxException {
        log.debug("REST request to update EmailCampaign : {}", emailCampaignDTO);
        if (emailCampaignDTO.getId() == null) {
            return createEmailCampaign(emailCampaignDTO);
        }
        EmailCampaignDTO result = emailCampaignService.save(emailCampaignDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("emailCampaign", emailCampaignDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /email-campaigns : get all the emailCampaigns.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of emailCampaigns in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/email-campaigns",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<EmailCampaignDTO>> getAllEmailCampaigns(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of EmailCampaigns");
        Page<EmailCampaignDTO> page = emailCampaignService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/email-campaigns");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /email-campaigns/:id : get the "id" emailCampaign.
     *
     * @param id the id of the emailCampaignDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the emailCampaignDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/email-campaigns/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<EmailCampaignDTO> getEmailCampaign(@PathVariable Long id) {
        log.debug("REST request to get EmailCampaign : {}", id);
        EmailCampaignDTO emailCampaignDTO = emailCampaignService.findOne(id);
        return Optional.ofNullable(emailCampaignDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /email-campaigns/:id : delete the "id" emailCampaign.
     *
     * @param id the id of the emailCampaignDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/email-campaigns/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteEmailCampaign(@PathVariable Long id) {
        log.debug("REST request to delete EmailCampaign : {}", id);
        emailCampaignService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("emailCampaign", id.toString())).build();
    }

    /**
     * SEARCH  /_search/email-campaigns?query=:query : search for the emailCampaign corresponding
     * to the query.
     *
     * @param query the query of the emailCampaign search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/_search/email-campaigns",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<EmailCampaignDTO>> searchEmailCampaigns(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of EmailCampaigns for query {}", query);
        Page<EmailCampaignDTO> page = emailCampaignService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/email-campaigns");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
