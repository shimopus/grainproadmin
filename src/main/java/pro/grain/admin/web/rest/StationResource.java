package pro.grain.admin.web.rest;

import com.codahale.metrics.annotation.Timed;
import pro.grain.admin.service.StationService;
import pro.grain.admin.web.rest.util.HeaderUtil;
import pro.grain.admin.web.rest.util.PaginationUtil;
import pro.grain.admin.service.dto.StationDTO;
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
 * REST controller for managing Station.
 */
@RestController
@RequestMapping("/api")
public class StationResource {

    private final Logger log = LoggerFactory.getLogger(StationResource.class);
        
    @Inject
    private StationService stationService;

    /**
     * POST  /stations : Create a new station.
     *
     * @param stationDTO the stationDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new stationDTO, or with status 400 (Bad Request) if the station has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/stations",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<StationDTO> createStation(@Valid @RequestBody StationDTO stationDTO) throws URISyntaxException {
        log.debug("REST request to save Station : {}", stationDTO);
        if (stationDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("station", "idexists", "A new station cannot already have an ID")).body(null);
        }
        StationDTO result = stationService.save(stationDTO);
        return ResponseEntity.created(new URI("/api/stations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("station", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /stations : Updates an existing station.
     *
     * @param stationDTO the stationDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated stationDTO,
     * or with status 400 (Bad Request) if the stationDTO is not valid,
     * or with status 500 (Internal Server Error) if the stationDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/stations",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<StationDTO> updateStation(@Valid @RequestBody StationDTO stationDTO) throws URISyntaxException {
        log.debug("REST request to update Station : {}", stationDTO);
        if (stationDTO.getId() == null) {
            return createStation(stationDTO);
        }
        StationDTO result = stationService.save(stationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("station", stationDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /stations : get all the stations.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of stations in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/stations",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<StationDTO>> getAllStations(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Stations");
        Page<StationDTO> page = stationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/stations");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /stations/:id : get the "id" station.
     *
     * @param id the id of the stationDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the stationDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/stations/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<StationDTO> getStation(@PathVariable Long id) {
        log.debug("REST request to get Station : {}", id);
        StationDTO stationDTO = stationService.findOne(id);
        return Optional.ofNullable(stationDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /stations/:id : delete the "id" station.
     *
     * @param id the id of the stationDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/stations/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        log.debug("REST request to delete Station : {}", id);
        stationService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("station", id.toString())).build();
    }

    /**
     * SEARCH  /_search/stations?query=:query : search for the station corresponding
     * to the query.
     *
     * @param query the query of the station search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/_search/stations",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<StationDTO>> searchStations(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Stations for query {}", query);
        Page<StationDTO> page = stationService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/stations");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
