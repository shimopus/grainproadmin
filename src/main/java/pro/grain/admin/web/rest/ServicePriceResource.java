package pro.grain.admin.web.rest;

import com.codahale.metrics.annotation.Timed;
import pro.grain.admin.service.ServicePriceService;
import pro.grain.admin.web.rest.util.HeaderUtil;
import pro.grain.admin.web.rest.util.PaginationUtil;
import pro.grain.admin.service.dto.ServicePriceDTO;
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
 * REST controller for managing ServicePrice.
 */
@RestController
@RequestMapping("/api")
public class ServicePriceResource {

    private final Logger log = LoggerFactory.getLogger(ServicePriceResource.class);
        
    @Inject
    private ServicePriceService servicePriceService;

    /**
     * POST  /service-prices : Create a new servicePrice.
     *
     * @param servicePriceDTO the servicePriceDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new servicePriceDTO, or with status 400 (Bad Request) if the servicePrice has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/service-prices",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ServicePriceDTO> createServicePrice(@Valid @RequestBody ServicePriceDTO servicePriceDTO) throws URISyntaxException {
        log.debug("REST request to save ServicePrice : {}", servicePriceDTO);
        if (servicePriceDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("servicePrice", "idexists", "A new servicePrice cannot already have an ID")).body(null);
        }
        ServicePriceDTO result = servicePriceService.save(servicePriceDTO);
        return ResponseEntity.created(new URI("/api/service-prices/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("servicePrice", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /service-prices : Updates an existing servicePrice.
     *
     * @param servicePriceDTO the servicePriceDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated servicePriceDTO,
     * or with status 400 (Bad Request) if the servicePriceDTO is not valid,
     * or with status 500 (Internal Server Error) if the servicePriceDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/service-prices",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ServicePriceDTO> updateServicePrice(@Valid @RequestBody ServicePriceDTO servicePriceDTO) throws URISyntaxException {
        log.debug("REST request to update ServicePrice : {}", servicePriceDTO);
        if (servicePriceDTO.getId() == null) {
            return createServicePrice(servicePriceDTO);
        }
        ServicePriceDTO result = servicePriceService.save(servicePriceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("servicePrice", servicePriceDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /service-prices : get all the servicePrices.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of servicePrices in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/service-prices",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ServicePriceDTO>> getAllServicePrices(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ServicePrices");
        Page<ServicePriceDTO> page = servicePriceService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/service-prices");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /service-prices/:id : get the "id" servicePrice.
     *
     * @param id the id of the servicePriceDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the servicePriceDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/service-prices/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ServicePriceDTO> getServicePrice(@PathVariable Long id) {
        log.debug("REST request to get ServicePrice : {}", id);
        ServicePriceDTO servicePriceDTO = servicePriceService.findOne(id);
        return Optional.ofNullable(servicePriceDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /service-prices/:id : delete the "id" servicePrice.
     *
     * @param id the id of the servicePriceDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/service-prices/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteServicePrice(@PathVariable Long id) {
        log.debug("REST request to delete ServicePrice : {}", id);
        servicePriceService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("servicePrice", id.toString())).build();
    }

    /**
     * SEARCH  /_search/service-prices?query=:query : search for the servicePrice corresponding
     * to the query.
     *
     * @param query the query of the servicePrice search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/_search/service-prices",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ServicePriceDTO>> searchServicePrices(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of ServicePrices for query {}", query);
        Page<ServicePriceDTO> page = servicePriceService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/service-prices");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
