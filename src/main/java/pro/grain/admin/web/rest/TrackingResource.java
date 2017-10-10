package pro.grain.admin.web.rest;

import com.codahale.metrics.annotation.Timed;
import pro.grain.admin.service.TrackingService;
import pro.grain.admin.web.rest.util.HeaderUtil;
import pro.grain.admin.web.rest.util.PaginationUtil;
import pro.grain.admin.service.dto.TrackingDTO;
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
 * REST controller for managing Tracking.
 */
@RestController
@RequestMapping("/api")
public class TrackingResource {

    private final Logger log = LoggerFactory.getLogger(TrackingResource.class);
        
    @Inject
    private TrackingService trackingService;

    /**
     * POST  /trackings : Create a new tracking.
     *
     * @param trackingDTO the trackingDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new trackingDTO, or with status 400 (Bad Request) if the tracking has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/trackings",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TrackingDTO> createTracking(@RequestBody TrackingDTO trackingDTO) throws URISyntaxException {
        log.debug("REST request to save Tracking : {}", trackingDTO);
        if (trackingDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("tracking", "idexists", "A new tracking cannot already have an ID")).body(null);
        }
        TrackingDTO result = trackingService.save(trackingDTO);
        return ResponseEntity.created(new URI("/api/trackings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("tracking", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /trackings : Updates an existing tracking.
     *
     * @param trackingDTO the trackingDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated trackingDTO,
     * or with status 400 (Bad Request) if the trackingDTO is not valid,
     * or with status 500 (Internal Server Error) if the trackingDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/trackings",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TrackingDTO> updateTracking(@RequestBody TrackingDTO trackingDTO) throws URISyntaxException {
        log.debug("REST request to update Tracking : {}", trackingDTO);
        if (trackingDTO.getId() == null) {
            return createTracking(trackingDTO);
        }
        TrackingDTO result = trackingService.save(trackingDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("tracking", trackingDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /trackings : get all the trackings.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of trackings in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/trackings",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TrackingDTO>> getAllTrackings(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Trackings");
        Page<TrackingDTO> page = trackingService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/trackings");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /trackings/:id : get the "id" tracking.
     *
     * @param id the id of the trackingDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the trackingDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/trackings/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TrackingDTO> getTracking(@PathVariable Long id) {
        log.debug("REST request to get Tracking : {}", id);
        TrackingDTO trackingDTO = trackingService.findOne(id);
        return Optional.ofNullable(trackingDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /trackings/:id : delete the "id" tracking.
     *
     * @param id the id of the trackingDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/trackings/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteTracking(@PathVariable Long id) {
        log.debug("REST request to delete Tracking : {}", id);
        trackingService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("tracking", id.toString())).build();
    }

    /**
     * SEARCH  /_search/trackings?query=:query : search for the tracking corresponding
     * to the query.
     *
     * @param query the query of the tracking search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/_search/trackings",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TrackingDTO>> searchTrackings(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Trackings for query {}", query);
        Page<TrackingDTO> page = trackingService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/trackings");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
