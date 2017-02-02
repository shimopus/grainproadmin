package pro.grain.admin.web.rest;

import com.codahale.metrics.annotation.Timed;
import pro.grain.admin.service.PriceUpdateQueueService;
import pro.grain.admin.web.rest.util.HeaderUtil;
import pro.grain.admin.web.rest.util.PaginationUtil;
import pro.grain.admin.service.dto.PriceUpdateQueueDTO;
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
 * REST controller for managing PriceUpdateQueue.
 */
@RestController
@RequestMapping("/api")
public class PriceUpdateQueueResource {

    private final Logger log = LoggerFactory.getLogger(PriceUpdateQueueResource.class);
        
    @Inject
    private PriceUpdateQueueService priceUpdateQueueService;

    /**
     * POST  /price-update-queues : Create a new priceUpdateQueue.
     *
     * @param priceUpdateQueueDTO the priceUpdateQueueDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new priceUpdateQueueDTO, or with status 400 (Bad Request) if the priceUpdateQueue has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/price-update-queues",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PriceUpdateQueueDTO> createPriceUpdateQueue(@Valid @RequestBody PriceUpdateQueueDTO priceUpdateQueueDTO) throws URISyntaxException {
        log.debug("REST request to save PriceUpdateQueue : {}", priceUpdateQueueDTO);
        if (priceUpdateQueueDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("priceUpdateQueue", "idexists", "A new priceUpdateQueue cannot already have an ID")).body(null);
        }
        PriceUpdateQueueDTO result = priceUpdateQueueService.save(priceUpdateQueueDTO);
        return ResponseEntity.created(new URI("/api/price-update-queues/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("priceUpdateQueue", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /price-update-queues : Updates an existing priceUpdateQueue.
     *
     * @param priceUpdateQueueDTO the priceUpdateQueueDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated priceUpdateQueueDTO,
     * or with status 400 (Bad Request) if the priceUpdateQueueDTO is not valid,
     * or with status 500 (Internal Server Error) if the priceUpdateQueueDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/price-update-queues",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PriceUpdateQueueDTO> updatePriceUpdateQueue(@Valid @RequestBody PriceUpdateQueueDTO priceUpdateQueueDTO) throws URISyntaxException {
        log.debug("REST request to update PriceUpdateQueue : {}", priceUpdateQueueDTO);
        if (priceUpdateQueueDTO.getId() == null) {
            return createPriceUpdateQueue(priceUpdateQueueDTO);
        }
        PriceUpdateQueueDTO result = priceUpdateQueueService.save(priceUpdateQueueDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("priceUpdateQueue", priceUpdateQueueDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /price-update-queues : get all the priceUpdateQueues.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of priceUpdateQueues in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/price-update-queues",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<PriceUpdateQueueDTO>> getAllPriceUpdateQueues(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of PriceUpdateQueues");
        Page<PriceUpdateQueueDTO> page = priceUpdateQueueService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/price-update-queues");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /price-update-queues/:id : get the "id" priceUpdateQueue.
     *
     * @param id the id of the priceUpdateQueueDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the priceUpdateQueueDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/price-update-queues/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PriceUpdateQueueDTO> getPriceUpdateQueue(@PathVariable Long id) {
        log.debug("REST request to get PriceUpdateQueue : {}", id);
        PriceUpdateQueueDTO priceUpdateQueueDTO = priceUpdateQueueService.findOne(id);
        return Optional.ofNullable(priceUpdateQueueDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /price-update-queues/:id : delete the "id" priceUpdateQueue.
     *
     * @param id the id of the priceUpdateQueueDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/price-update-queues/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deletePriceUpdateQueue(@PathVariable Long id) {
        log.debug("REST request to delete PriceUpdateQueue : {}", id);
        priceUpdateQueueService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("priceUpdateQueue", id.toString())).build();
    }

    /**
     * SEARCH  /_search/price-update-queues?query=:query : search for the priceUpdateQueue corresponding
     * to the query.
     *
     * @param query the query of the priceUpdateQueue search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/_search/price-update-queues",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<PriceUpdateQueueDTO>> searchPriceUpdateQueues(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of PriceUpdateQueues for query {}", query);
        Page<PriceUpdateQueueDTO> page = priceUpdateQueueService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/price-update-queues");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
