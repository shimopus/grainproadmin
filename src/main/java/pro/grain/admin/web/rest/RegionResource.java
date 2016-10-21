package pro.grain.admin.web.rest;

import com.codahale.metrics.annotation.Timed;
import pro.grain.admin.service.RegionService;
import pro.grain.admin.web.rest.util.HeaderUtil;
import pro.grain.admin.web.rest.util.PaginationUtil;
import pro.grain.admin.service.dto.RegionDTO;
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
 * REST controller for managing Region.
 */
@RestController
@RequestMapping("/api")
public class RegionResource {

    private final Logger log = LoggerFactory.getLogger(RegionResource.class);
        
    @Inject
    private RegionService regionService;

    /**
     * POST  /regions : Create a new region.
     *
     * @param regionDTO the regionDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new regionDTO, or with status 400 (Bad Request) if the region has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/regions",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<RegionDTO> createRegion(@Valid @RequestBody RegionDTO regionDTO) throws URISyntaxException {
        log.debug("REST request to save Region : {}", regionDTO);
        if (regionDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("region", "idexists", "A new region cannot already have an ID")).body(null);
        }
        RegionDTO result = regionService.save(regionDTO);
        return ResponseEntity.created(new URI("/api/regions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("region", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /regions : Updates an existing region.
     *
     * @param regionDTO the regionDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated regionDTO,
     * or with status 400 (Bad Request) if the regionDTO is not valid,
     * or with status 500 (Internal Server Error) if the regionDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/regions",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<RegionDTO> updateRegion(@Valid @RequestBody RegionDTO regionDTO) throws URISyntaxException {
        log.debug("REST request to update Region : {}", regionDTO);
        if (regionDTO.getId() == null) {
            return createRegion(regionDTO);
        }
        RegionDTO result = regionService.save(regionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("region", regionDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /regions : get all the regions.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of regions in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/regions",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<RegionDTO>> getAllRegions(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Regions");
        Page<RegionDTO> page = regionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/regions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /regions/:id : get the "id" region.
     *
     * @param id the id of the regionDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the regionDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/regions/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<RegionDTO> getRegion(@PathVariable Long id) {
        log.debug("REST request to get Region : {}", id);
        RegionDTO regionDTO = regionService.findOne(id);
        return Optional.ofNullable(regionDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /regions/:id : delete the "id" region.
     *
     * @param id the id of the regionDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/regions/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteRegion(@PathVariable Long id) {
        log.debug("REST request to delete Region : {}", id);
        regionService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("region", id.toString())).build();
    }

    /**
     * SEARCH  /_search/regions?query=:query : search for the region corresponding
     * to the query.
     *
     * @param query the query of the region search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/_search/regions",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<RegionDTO>> searchRegions(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Regions for query {}", query);
        Page<RegionDTO> page = regionService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/regions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
