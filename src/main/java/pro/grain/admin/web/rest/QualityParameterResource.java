package pro.grain.admin.web.rest;

import com.codahale.metrics.annotation.Timed;
import pro.grain.admin.service.QualityParameterService;
import pro.grain.admin.web.rest.util.HeaderUtil;
import pro.grain.admin.web.rest.util.PaginationUtil;
import pro.grain.admin.service.dto.QualityParameterDTO;
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
 * REST controller for managing QualityParameter.
 */
@RestController
@RequestMapping("/api")
public class QualityParameterResource {

    private final Logger log = LoggerFactory.getLogger(QualityParameterResource.class);
        
    @Inject
    private QualityParameterService qualityParameterService;

    /**
     * POST  /quality-parameters : Create a new qualityParameter.
     *
     * @param qualityParameterDTO the qualityParameterDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new qualityParameterDTO, or with status 400 (Bad Request) if the qualityParameter has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/quality-parameters",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<QualityParameterDTO> createQualityParameter(@Valid @RequestBody QualityParameterDTO qualityParameterDTO) throws URISyntaxException {
        log.debug("REST request to save QualityParameter : {}", qualityParameterDTO);
        if (qualityParameterDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("qualityParameter", "idexists", "A new qualityParameter cannot already have an ID")).body(null);
        }
        QualityParameterDTO result = qualityParameterService.save(qualityParameterDTO);
        return ResponseEntity.created(new URI("/api/quality-parameters/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("qualityParameter", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /quality-parameters : Updates an existing qualityParameter.
     *
     * @param qualityParameterDTO the qualityParameterDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated qualityParameterDTO,
     * or with status 400 (Bad Request) if the qualityParameterDTO is not valid,
     * or with status 500 (Internal Server Error) if the qualityParameterDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/quality-parameters",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<QualityParameterDTO> updateQualityParameter(@Valid @RequestBody QualityParameterDTO qualityParameterDTO) throws URISyntaxException {
        log.debug("REST request to update QualityParameter : {}", qualityParameterDTO);
        if (qualityParameterDTO.getId() == null) {
            return createQualityParameter(qualityParameterDTO);
        }
        QualityParameterDTO result = qualityParameterService.save(qualityParameterDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("qualityParameter", qualityParameterDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /quality-parameters : get all the qualityParameters.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of qualityParameters in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/quality-parameters",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<QualityParameterDTO>> getAllQualityParameters(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of QualityParameters");
        Page<QualityParameterDTO> page = qualityParameterService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/quality-parameters");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /quality-parameters/:id : get the "id" qualityParameter.
     *
     * @param id the id of the qualityParameterDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the qualityParameterDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/quality-parameters/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<QualityParameterDTO> getQualityParameter(@PathVariable Long id) {
        log.debug("REST request to get QualityParameter : {}", id);
        QualityParameterDTO qualityParameterDTO = qualityParameterService.findOne(id);
        return Optional.ofNullable(qualityParameterDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /quality-parameters/:id : delete the "id" qualityParameter.
     *
     * @param id the id of the qualityParameterDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/quality-parameters/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteQualityParameter(@PathVariable Long id) {
        log.debug("REST request to delete QualityParameter : {}", id);
        qualityParameterService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("qualityParameter", id.toString())).build();
    }

    /**
     * SEARCH  /_search/quality-parameters?query=:query : search for the qualityParameter corresponding
     * to the query.
     *
     * @param query the query of the qualityParameter search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/_search/quality-parameters",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<QualityParameterDTO>> searchQualityParameters(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of QualityParameters for query {}", query);
        Page<QualityParameterDTO> page = qualityParameterService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/quality-parameters");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
