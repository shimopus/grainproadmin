package pro.grain.admin.web.rest;

import com.codahale.metrics.annotation.Timed;
import pro.grain.admin.service.LocalityService;
import pro.grain.admin.web.rest.util.HeaderUtil;
import pro.grain.admin.web.rest.util.PaginationUtil;
import pro.grain.admin.service.dto.LocalityDTO;
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
 * REST controller for managing Locality.
 */
@RestController
@RequestMapping("/api")
public class LocalityResource {

    private final Logger log = LoggerFactory.getLogger(LocalityResource.class);
        
    @Inject
    private LocalityService localityService;

    /**
     * POST  /localities : Create a new locality.
     *
     * @param localityDTO the localityDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new localityDTO, or with status 400 (Bad Request) if the locality has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/localities",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LocalityDTO> createLocality(@Valid @RequestBody LocalityDTO localityDTO) throws URISyntaxException {
        log.debug("REST request to save Locality : {}", localityDTO);
        if (localityDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("locality", "idexists", "A new locality cannot already have an ID")).body(null);
        }
        LocalityDTO result = localityService.save(localityDTO);
        return ResponseEntity.created(new URI("/api/localities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("locality", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /localities : Updates an existing locality.
     *
     * @param localityDTO the localityDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated localityDTO,
     * or with status 400 (Bad Request) if the localityDTO is not valid,
     * or with status 500 (Internal Server Error) if the localityDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/localities",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LocalityDTO> updateLocality(@Valid @RequestBody LocalityDTO localityDTO) throws URISyntaxException {
        log.debug("REST request to update Locality : {}", localityDTO);
        if (localityDTO.getId() == null) {
            return createLocality(localityDTO);
        }
        LocalityDTO result = localityService.save(localityDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("locality", localityDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /localities : get all the localities.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of localities in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/localities",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<LocalityDTO>> getAllLocalities(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Localities");
        Page<LocalityDTO> page = localityService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/localities");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /localities/:id : get the "id" locality.
     *
     * @param id the id of the localityDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the localityDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/localities/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LocalityDTO> getLocality(@PathVariable Long id) {
        log.debug("REST request to get Locality : {}", id);
        LocalityDTO localityDTO = localityService.findOne(id);
        return Optional.ofNullable(localityDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /localities/:id : delete the "id" locality.
     *
     * @param id the id of the localityDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/localities/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteLocality(@PathVariable Long id) {
        log.debug("REST request to delete Locality : {}", id);
        localityService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("locality", id.toString())).build();
    }

    /**
     * SEARCH  /_search/localities?query=:query : search for the locality corresponding
     * to the query.
     *
     * @param query the query of the locality search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/_search/localities",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<LocalityDTO>> searchLocalities(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Localities for query {}", query);
        Page<LocalityDTO> page = localityService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/localities");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
