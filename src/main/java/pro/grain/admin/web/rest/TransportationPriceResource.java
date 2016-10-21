package pro.grain.admin.web.rest;

import com.codahale.metrics.annotation.Timed;
import pro.grain.admin.service.TransportationPriceService;
import pro.grain.admin.web.rest.util.HeaderUtil;
import pro.grain.admin.web.rest.util.PaginationUtil;
import pro.grain.admin.service.dto.TransportationPriceDTO;
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
 * REST controller for managing TransportationPrice.
 */
@RestController
@RequestMapping("/api")
public class TransportationPriceResource {

    private final Logger log = LoggerFactory.getLogger(TransportationPriceResource.class);
        
    @Inject
    private TransportationPriceService transportationPriceService;

    /**
     * POST  /transportation-prices : Create a new transportationPrice.
     *
     * @param transportationPriceDTO the transportationPriceDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new transportationPriceDTO, or with status 400 (Bad Request) if the transportationPrice has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/transportation-prices",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TransportationPriceDTO> createTransportationPrice(@Valid @RequestBody TransportationPriceDTO transportationPriceDTO) throws URISyntaxException {
        log.debug("REST request to save TransportationPrice : {}", transportationPriceDTO);
        if (transportationPriceDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("transportationPrice", "idexists", "A new transportationPrice cannot already have an ID")).body(null);
        }
        TransportationPriceDTO result = transportationPriceService.save(transportationPriceDTO);
        return ResponseEntity.created(new URI("/api/transportation-prices/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("transportationPrice", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /transportation-prices : Updates an existing transportationPrice.
     *
     * @param transportationPriceDTO the transportationPriceDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated transportationPriceDTO,
     * or with status 400 (Bad Request) if the transportationPriceDTO is not valid,
     * or with status 500 (Internal Server Error) if the transportationPriceDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/transportation-prices",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TransportationPriceDTO> updateTransportationPrice(@Valid @RequestBody TransportationPriceDTO transportationPriceDTO) throws URISyntaxException {
        log.debug("REST request to update TransportationPrice : {}", transportationPriceDTO);
        if (transportationPriceDTO.getId() == null) {
            return createTransportationPrice(transportationPriceDTO);
        }
        TransportationPriceDTO result = transportationPriceService.save(transportationPriceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("transportationPrice", transportationPriceDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /transportation-prices : get all the transportationPrices.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of transportationPrices in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/transportation-prices",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TransportationPriceDTO>> getAllTransportationPrices(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of TransportationPrices");
        Page<TransportationPriceDTO> page = transportationPriceService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/transportation-prices");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /transportation-prices/:id : get the "id" transportationPrice.
     *
     * @param id the id of the transportationPriceDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the transportationPriceDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/transportation-prices/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TransportationPriceDTO> getTransportationPrice(@PathVariable Long id) {
        log.debug("REST request to get TransportationPrice : {}", id);
        TransportationPriceDTO transportationPriceDTO = transportationPriceService.findOne(id);
        return Optional.ofNullable(transportationPriceDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /transportation-prices/:id : delete the "id" transportationPrice.
     *
     * @param id the id of the transportationPriceDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/transportation-prices/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteTransportationPrice(@PathVariable Long id) {
        log.debug("REST request to delete TransportationPrice : {}", id);
        transportationPriceService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("transportationPrice", id.toString())).build();
    }

    /**
     * SEARCH  /_search/transportation-prices?query=:query : search for the transportationPrice corresponding
     * to the query.
     *
     * @param query the query of the transportationPrice search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/_search/transportation-prices",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TransportationPriceDTO>> searchTransportationPrices(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of TransportationPrices for query {}", query);
        Page<TransportationPriceDTO> page = transportationPriceService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/transportation-prices");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
