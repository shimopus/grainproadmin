package pro.grain.admin.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.springframework.security.access.annotation.Secured;
import pro.grain.admin.security.AuthoritiesConstants;
import pro.grain.admin.service.ServiceTypeService;
import pro.grain.admin.web.rest.util.HeaderUtil;
import pro.grain.admin.web.rest.util.PaginationUtil;
import pro.grain.admin.service.dto.ServiceTypeDTO;
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
 * REST controller for managing ServiceType.
 */
@RestController
@RequestMapping("/api")
@Secured(AuthoritiesConstants.ADMIN)
public class ServiceTypeResource {

    private final Logger log = LoggerFactory.getLogger(ServiceTypeResource.class);

    @Inject
    private ServiceTypeService serviceTypeService;

    /**
     * POST  /service-types : Create a new serviceType.
     *
     * @param serviceTypeDTO the serviceTypeDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new serviceTypeDTO, or with status 400 (Bad Request) if the serviceType has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/service-types",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ServiceTypeDTO> createServiceType(@Valid @RequestBody ServiceTypeDTO serviceTypeDTO) throws URISyntaxException {
        log.debug("REST request to save ServiceType : {}", serviceTypeDTO);
        if (serviceTypeDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("serviceType", "idexists", "A new serviceType cannot already have an ID")).body(null);
        }
        ServiceTypeDTO result = serviceTypeService.save(serviceTypeDTO);
        return ResponseEntity.created(new URI("/api/service-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("serviceType", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /service-types : Updates an existing serviceType.
     *
     * @param serviceTypeDTO the serviceTypeDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated serviceTypeDTO,
     * or with status 400 (Bad Request) if the serviceTypeDTO is not valid,
     * or with status 500 (Internal Server Error) if the serviceTypeDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/service-types",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ServiceTypeDTO> updateServiceType(@Valid @RequestBody ServiceTypeDTO serviceTypeDTO) throws URISyntaxException {
        log.debug("REST request to update ServiceType : {}", serviceTypeDTO);
        if (serviceTypeDTO.getId() == null) {
            return createServiceType(serviceTypeDTO);
        }
        ServiceTypeDTO result = serviceTypeService.save(serviceTypeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("serviceType", serviceTypeDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /service-types : get all the serviceTypes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of serviceTypes in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/service-types",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    @Timed
    public ResponseEntity<List<ServiceTypeDTO>> getAllServiceTypes(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ServiceTypes");
        Page<ServiceTypeDTO> page = serviceTypeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/service-types");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /service-types/:id : get the "id" serviceType.
     *
     * @param id the id of the serviceTypeDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the serviceTypeDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/service-types/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ServiceTypeDTO> getServiceType(@PathVariable Long id) {
        log.debug("REST request to get ServiceType : {}", id);
        ServiceTypeDTO serviceTypeDTO = serviceTypeService.findOne(id);
        return Optional.ofNullable(serviceTypeDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /service-types/:id : delete the "id" serviceType.
     *
     * @param id the id of the serviceTypeDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/service-types/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteServiceType(@PathVariable Long id) {
        log.debug("REST request to delete ServiceType : {}", id);
        serviceTypeService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("serviceType", id.toString())).build();
    }

    /**
     * SEARCH  /_search/service-types?query=:query : search for the serviceType corresponding
     * to the query.
     *
     * @param query the query of the serviceType search
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/_search/service-types",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ServiceTypeDTO>> searchServiceTypes(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of ServiceTypes for query {}", query);
        Page<ServiceTypeDTO> page = serviceTypeService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/service-types");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
