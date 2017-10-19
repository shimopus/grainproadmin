package pro.grain.admin.web.rest;

import com.codahale.metrics.annotation.Timed;
import pro.grain.admin.service.SubscriptionConfigService;
import pro.grain.admin.web.rest.util.HeaderUtil;
import pro.grain.admin.web.rest.util.PaginationUtil;
import pro.grain.admin.service.dto.SubscriptionConfigDTO;
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
 * REST controller for managing SubscriptionConfig.
 */
@RestController
@RequestMapping("/api")
public class SubscriptionConfigResource {

    private final Logger log = LoggerFactory.getLogger(SubscriptionConfigResource.class);

    @Inject
    private SubscriptionConfigService subscriptionConfigService;

    /**
     * POST  /subscription-configs : Create a new subscriptionConfig.
     *
     * @param subscriptionConfigDTO the subscriptionConfigDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new subscriptionConfigDTO, or with status 400 (Bad Request) if the subscriptionConfig has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/subscription-configs",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SubscriptionConfigDTO> createSubscriptionConfig(@RequestBody SubscriptionConfigDTO subscriptionConfigDTO) throws URISyntaxException {
        log.debug("REST request to save SubscriptionConfig : {}", subscriptionConfigDTO);
        if (subscriptionConfigDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("subscriptionConfig", "idexists", "A new subscriptionConfig cannot already have an ID")).body(null);
        }
        SubscriptionConfigDTO result = subscriptionConfigService.save(subscriptionConfigDTO);
        return ResponseEntity.created(new URI("/api/subscription-configs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("subscriptionConfig", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /subscription-configs : Updates an existing subscriptionConfig.
     *
     * @param subscriptionConfigDTO the subscriptionConfigDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated subscriptionConfigDTO,
     * or with status 400 (Bad Request) if the subscriptionConfigDTO is not valid,
     * or with status 500 (Internal Server Error) if the subscriptionConfigDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/subscription-configs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SubscriptionConfigDTO> updateSubscriptionConfig(@RequestBody SubscriptionConfigDTO subscriptionConfigDTO) throws URISyntaxException {
        log.debug("REST request to update SubscriptionConfig : {}", subscriptionConfigDTO);
        if (subscriptionConfigDTO.getId() == null) {
            return createSubscriptionConfig(subscriptionConfigDTO);
        }
        SubscriptionConfigDTO result = subscriptionConfigService.save(subscriptionConfigDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("subscriptionConfig", subscriptionConfigDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /subscription-configs : get all the subscriptionConfigs.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of subscriptionConfigs in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/subscription-configs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<SubscriptionConfigDTO>> getAllSubscriptionConfigs(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of SubscriptionConfigs");
        Page<SubscriptionConfigDTO> page = subscriptionConfigService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/subscription-configs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /subscription-configs/:id : get the "id" subscriptionConfig.
     *
     * @param id the id of the subscriptionConfigDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the subscriptionConfigDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/subscription-configs/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SubscriptionConfigDTO> getSubscriptionConfig(@PathVariable Long id) {
        log.debug("REST request to get SubscriptionConfig : {}", id);
        SubscriptionConfigDTO subscriptionConfigDTO = subscriptionConfigService.findOne(id);
        return Optional.ofNullable(subscriptionConfigDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /subscription-configs/:id : delete the "id" subscriptionConfig.
     *
     * @param id the id of the subscriptionConfigDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/subscription-configs/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSubscriptionConfig(@PathVariable Long id) {
        log.debug("REST request to delete SubscriptionConfig : {}", id);
        subscriptionConfigService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("subscriptionConfig", id.toString())).build();
    }

    /**
     * SEARCH  /_search/subscription-configs?query=:query : search for the subscriptionConfig corresponding
     * to the query.
     *
     * @param query the query of the subscriptionConfig search
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/_search/subscription-configs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<SubscriptionConfigDTO>> searchSubscriptionConfigs(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of SubscriptionConfigs for query {}", query);
        Page<SubscriptionConfigDTO> page = subscriptionConfigService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/subscription-configs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/subscription-configs/getbypartner",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SubscriptionConfigDTO> getByPartner(@RequestParam Long partnerId) {
        log.debug("REST request to get SubscriptionConfig by partner: {}", partnerId);
        SubscriptionConfigDTO subscriptionConfigDTO = subscriptionConfigService.findByPartner(partnerId);
        return Optional.ofNullable(subscriptionConfigDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/subscription-configs/getactive",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<SubscriptionConfigDTO>> getAllActive() {
        log.debug("REST request to get all active SubscriptionConfig");
        List<SubscriptionConfigDTO> subscriptionConfigDTOs = subscriptionConfigService.getAllActive();
        return Optional.ofNullable(subscriptionConfigDTOs)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
