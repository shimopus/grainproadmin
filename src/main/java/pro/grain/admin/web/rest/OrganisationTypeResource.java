package pro.grain.admin.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.springframework.security.access.annotation.Secured;
import pro.grain.admin.security.AuthoritiesConstants;
import pro.grain.admin.service.OrganisationTypeService;
import pro.grain.admin.web.rest.util.HeaderUtil;
import pro.grain.admin.web.rest.util.PaginationUtil;
import pro.grain.admin.service.dto.OrganisationTypeDTO;
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
 * REST controller for managing OrganisationType.
 */
@RestController
@RequestMapping("/api")
@Secured(AuthoritiesConstants.ADMIN)
public class OrganisationTypeResource {

    private final Logger log = LoggerFactory.getLogger(OrganisationTypeResource.class);

    @Inject
    private OrganisationTypeService organisationTypeService;

    /**
     * POST  /organisation-types : Create a new organisationType.
     *
     * @param organisationTypeDTO the organisationTypeDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new organisationTypeDTO, or with status 400 (Bad Request) if the organisationType has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/organisation-types",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<OrganisationTypeDTO> createOrganisationType(@Valid @RequestBody OrganisationTypeDTO organisationTypeDTO) throws URISyntaxException {
        log.debug("REST request to save OrganisationType : {}", organisationTypeDTO);
        if (organisationTypeDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("organisationType", "idexists", "A new organisationType cannot already have an ID")).body(null);
        }
        OrganisationTypeDTO result = organisationTypeService.save(organisationTypeDTO);
        return ResponseEntity.created(new URI("/api/organisation-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("organisationType", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /organisation-types : Updates an existing organisationType.
     *
     * @param organisationTypeDTO the organisationTypeDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated organisationTypeDTO,
     * or with status 400 (Bad Request) if the organisationTypeDTO is not valid,
     * or with status 500 (Internal Server Error) if the organisationTypeDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/organisation-types",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<OrganisationTypeDTO> updateOrganisationType(@Valid @RequestBody OrganisationTypeDTO organisationTypeDTO) throws URISyntaxException {
        log.debug("REST request to update OrganisationType : {}", organisationTypeDTO);
        if (organisationTypeDTO.getId() == null) {
            return createOrganisationType(organisationTypeDTO);
        }
        OrganisationTypeDTO result = organisationTypeService.save(organisationTypeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("organisationType", organisationTypeDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /organisation-types : get all the organisationTypes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of organisationTypes in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/organisation-types",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    @Timed
    public ResponseEntity<List<OrganisationTypeDTO>> getAllOrganisationTypes(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of OrganisationTypes");
        Page<OrganisationTypeDTO> page = organisationTypeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/organisation-types");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /organisation-types/:id : get the "id" organisationType.
     *
     * @param id the id of the organisationTypeDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the organisationTypeDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/organisation-types/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<OrganisationTypeDTO> getOrganisationType(@PathVariable Long id) {
        log.debug("REST request to get OrganisationType : {}", id);
        OrganisationTypeDTO organisationTypeDTO = organisationTypeService.findOne(id);
        return Optional.ofNullable(organisationTypeDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /organisation-types/:id : delete the "id" organisationType.
     *
     * @param id the id of the organisationTypeDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/organisation-types/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteOrganisationType(@PathVariable Long id) {
        log.debug("REST request to delete OrganisationType : {}", id);
        organisationTypeService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("organisationType", id.toString())).build();
    }

    /**
     * SEARCH  /_search/organisation-types?query=:query : search for the organisationType corresponding
     * to the query.
     *
     * @param query the query of the organisationType search
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/_search/organisation-types",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<OrganisationTypeDTO>> searchOrganisationTypes(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of OrganisationTypes for query {}", query);
        Page<OrganisationTypeDTO> page = organisationTypeService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/organisation-types");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
