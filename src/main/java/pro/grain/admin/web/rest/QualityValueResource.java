package pro.grain.admin.web.rest;

import com.codahale.metrics.annotation.Timed;
import pro.grain.admin.service.QualityValueService;
import pro.grain.admin.web.rest.util.HeaderUtil;
import pro.grain.admin.web.rest.util.PaginationUtil;
import pro.grain.admin.service.dto.QualityValueDTO;
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
 * REST controller for managing QualityValue.
 */
@RestController
@RequestMapping("/api")
public class QualityValueResource {

    private final Logger log = LoggerFactory.getLogger(QualityValueResource.class);
        
    @Inject
    private QualityValueService qualityValueService;

    /**
     * POST  /quality-values : Create a new qualityValue.
     *
     * @param qualityValueDTO the qualityValueDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new qualityValueDTO, or with status 400 (Bad Request) if the qualityValue has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/quality-values",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<QualityValueDTO> createQualityValue(@Valid @RequestBody QualityValueDTO qualityValueDTO) throws URISyntaxException {
        log.debug("REST request to save QualityValue : {}", qualityValueDTO);
        if (qualityValueDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("qualityValue", "idexists", "A new qualityValue cannot already have an ID")).body(null);
        }
        QualityValueDTO result = qualityValueService.save(qualityValueDTO);
        return ResponseEntity.created(new URI("/api/quality-values/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("qualityValue", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /quality-values : Updates an existing qualityValue.
     *
     * @param qualityValueDTO the qualityValueDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated qualityValueDTO,
     * or with status 400 (Bad Request) if the qualityValueDTO is not valid,
     * or with status 500 (Internal Server Error) if the qualityValueDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/quality-values",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<QualityValueDTO> updateQualityValue(@Valid @RequestBody QualityValueDTO qualityValueDTO) throws URISyntaxException {
        log.debug("REST request to update QualityValue : {}", qualityValueDTO);
        if (qualityValueDTO.getId() == null) {
            return createQualityValue(qualityValueDTO);
        }
        QualityValueDTO result = qualityValueService.save(qualityValueDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("qualityValue", qualityValueDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /quality-values : get all the qualityValues.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of qualityValues in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/quality-values",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<QualityValueDTO>> getAllQualityValues(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of QualityValues");
        Page<QualityValueDTO> page = qualityValueService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/quality-values");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /quality-values/:id : get the "id" qualityValue.
     *
     * @param id the id of the qualityValueDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the qualityValueDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/quality-values/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<QualityValueDTO> getQualityValue(@PathVariable Long id) {
        log.debug("REST request to get QualityValue : {}", id);
        QualityValueDTO qualityValueDTO = qualityValueService.findOne(id);
        return Optional.ofNullable(qualityValueDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /quality-values/:id : delete the "id" qualityValue.
     *
     * @param id the id of the qualityValueDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/quality-values/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteQualityValue(@PathVariable Long id) {
        log.debug("REST request to delete QualityValue : {}", id);
        qualityValueService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("qualityValue", id.toString())).build();
    }

    /**
     * SEARCH  /_search/quality-values?query=:query : search for the qualityValue corresponding
     * to the query.
     *
     * @param query the query of the qualityValue search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/_search/quality-values",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<QualityValueDTO>> searchQualityValues(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of QualityValues for query {}", query);
        Page<QualityValueDTO> page = qualityValueService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/quality-values");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
