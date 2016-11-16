package pro.grain.admin.web.rest;

import com.codahale.metrics.annotation.Timed;
import pro.grain.admin.service.BidService;
import pro.grain.admin.web.rest.util.HeaderUtil;
import pro.grain.admin.web.rest.util.PaginationUtil;
import pro.grain.admin.service.dto.BidDTO;
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
 * REST controller for managing Bid.
 */
@RestController
@RequestMapping("/api")
public class BidResource {

    private final Logger log = LoggerFactory.getLogger(BidResource.class);

    @Inject
    private BidService bidService;

    /**
     * POST  /bids : Create a new bid.
     *
     * @param bidDTO the bidDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new bidDTO, or with status 400 (Bad Request) if the bid has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/bids",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<BidDTO> createBid(@Valid @RequestBody BidDTO bidDTO) throws URISyntaxException {
        log.debug("REST request to save Bid : {}", bidDTO);
        if (bidDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("bid", "idexists", "A new bid cannot already have an ID")).body(null);
        }
        BidDTO result = bidService.save(bidDTO);
        return ResponseEntity.created(new URI("/api/bids/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("bid", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /bids : Updates an existing bid.
     *
     * @param bidDTO the bidDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated bidDTO,
     * or with status 400 (Bad Request) if the bidDTO is not valid,
     * or with status 500 (Internal Server Error) if the bidDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/bids",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<BidDTO> updateBid(@Valid @RequestBody BidDTO bidDTO) throws URISyntaxException {
        log.debug("REST request to update Bid : {}", bidDTO);
        if (bidDTO.getId() == null) {
            return createBid(bidDTO);
        }
        BidDTO result = bidService.save(bidDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("bid", bidDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /bids : get all the bids.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of bids in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/bids",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<BidDTO>> getAllBids(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Bids");
        Page<BidDTO> page = bidService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/bids");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/bids/bypartner",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<BidDTO>> getBids(@RequestParam("partnerId") Long id) throws URISyntaxException {
        log.debug("REST request to get all Bids for a Partner with id={}", id);
        List<BidDTO> bidsDTO = bidService.findByPartner(id);

        return new ResponseEntity<>(bidsDTO, HttpStatus.OK);
    }

    /**
     * GET  /bids/:id : get the "id" bid.
     *
     * @param id the id of the bidDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the bidDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/bids/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<BidDTO> getBid(@PathVariable Long id) {
        log.debug("REST request to get Bid : {}", id);
        BidDTO bidDTO = bidService.findOne(id);
        return Optional.ofNullable(bidDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /bids/:id : delete the "id" bid.
     *
     * @param id the id of the bidDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/bids/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteBid(@PathVariable Long id) {
        log.debug("REST request to delete Bid : {}", id);
        bidService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("bid", id.toString())).build();
    }

    /**
     * SEARCH  /_search/bids?query=:query : search for the bid corresponding
     * to the query.
     *
     * @param query the query of the bid search
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/_search/bids",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<BidDTO>> searchBids(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Bids for query {}", query);
        Page<BidDTO> page = bidService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/bids");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
