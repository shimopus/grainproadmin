package pro.grain.admin.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.springframework.dao.DataIntegrityViolationException;
import pro.grain.admin.service.PartnerService;
import pro.grain.admin.service.error.AsMinimumOneValidationException;
import pro.grain.admin.service.error.EntityConstrainViolation;
import pro.grain.admin.web.rest.util.HeaderUtil;
import pro.grain.admin.web.rest.util.PaginationUtil;
import pro.grain.admin.service.dto.PartnerDTO;
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
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Partner.
 */
@RestController
@RequestMapping("/api")
public class PartnerResource {

    private final Logger log = LoggerFactory.getLogger(PartnerResource.class);

    @Inject
    private PartnerService partnerService;

    private void validateBeforeUpdate(PartnerDTO partner) throws AsMinimumOneValidationException {
        if (partner.getContacts().size() < 1) {
            throw new AsMinimumOneValidationException();
        }
    }

    /**
     * POST  /partners : Create a new partner.
     *
     * @param partnerDTO the partnerDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new partnerDTO, or with status 400 (Bad Request) if the partner has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/partners",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PartnerDTO> createPartner(@Valid @RequestBody PartnerDTO partnerDTO) throws URISyntaxException {
        log.debug("REST request to save Partner : {}", partnerDTO);
        if (partnerDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("partner", "idexists", "A new partner cannot already have an ID")).body(null);
        }

        try {
            validateBeforeUpdate(partnerDTO);
        } catch (AsMinimumOneValidationException ex){
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert("contact", "asminimumone", "As minimum one Contact should be created for Partner")).body(null);
        }

        PartnerDTO result;

        try {
            result = partnerService.save(partnerDTO);
        } catch (EntityConstrainViolation ex) {
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert("partner", "uniqe", "The same Partner is already exists")).body(null);
        }

        if (result != null) {
            return ResponseEntity.created(new URI("/api/partners/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("partner", result.getId().toString()))
                .body(result);
        } else {
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert("partner", "smthwentwrong", "Something went wrong")).body(null);
        }
    }

    /**
     * PUT  /partners : Updates an existing partner.
     *
     * @param partnerDTO the partnerDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated partnerDTO,
     * or with status 400 (Bad Request) if the partnerDTO is not valid,
     * or with status 500 (Internal Server Error) if the partnerDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/partners",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PartnerDTO> updatePartner(@Valid @RequestBody PartnerDTO partnerDTO) throws URISyntaxException {
        log.debug("REST request to update Partner : {}", partnerDTO);
        if (partnerDTO.getId() == null) {
            return createPartner(partnerDTO);
        }

        try {
            validateBeforeUpdate(partnerDTO);
        } catch (AsMinimumOneValidationException ex){
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert("contact", "asminimumone", "As minimum one Contact should be created for Partner")).body(null);
        }

        PartnerDTO result;
        try {
            result = partnerService.save(partnerDTO);
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert("partner", "uniqe", "The same Partner is already exists")).body(null);
        }

        if (result != null) {
            return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("partner", partnerDTO.getId().toString()))
                .body(result);
        } else {
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert("partner", "smthwentwrong", "Something went wrong")).body(null);
        }
    }

    /**
     * GET  /partners : get all the partners.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of partners in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/partners",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<PartnerDTO>> getAllPartners(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Partners");
        Page<PartnerDTO> page = partnerService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/partners");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /partners/:id : get the "id" partner.
     *
     * @param id the id of the partnerDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the partnerDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/partners/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PartnerDTO> getPartner(@PathVariable Long id) {
        log.debug("REST request to get Partner : {}", id);
        PartnerDTO partnerDTO = partnerService.findOne(id);
        return Optional.ofNullable(partnerDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /partners/:id : delete the "id" partner.
     *
     * @param id the id of the partnerDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/partners/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deletePartner(@PathVariable Long id) {
        log.debug("REST request to delete Partner : {}", id);
        partnerService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("partner", id.toString())).build();
    }

    /**
     * SEARCH  /_search/partners?query=:query : search for the partner corresponding
     * to the query.
     *
     * @param query the query of the partner search
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/_search/partners",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<PartnerDTO>> searchPartners(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Partners for query {}", query);
        Page<PartnerDTO> page = partnerService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/partners");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
