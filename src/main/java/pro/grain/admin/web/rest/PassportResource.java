package pro.grain.admin.web.rest;

import com.codahale.metrics.annotation.Timed;
import pro.grain.admin.service.PassportService;
import pro.grain.admin.web.rest.util.HeaderUtil;
import pro.grain.admin.web.rest.util.PaginationUtil;
import pro.grain.admin.service.dto.PassportDTO;
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
 * REST controller for managing Passport.
 */
@RestController
@RequestMapping("/api")
public class PassportResource {

    private final Logger log = LoggerFactory.getLogger(PassportResource.class);
        
    @Inject
    private PassportService passportService;

    /**
     * POST  /passports : Create a new passport.
     *
     * @param passportDTO the passportDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new passportDTO, or with status 400 (Bad Request) if the passport has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/passports",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PassportDTO> createPassport(@Valid @RequestBody PassportDTO passportDTO) throws URISyntaxException {
        log.debug("REST request to save Passport : {}", passportDTO);
        if (passportDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("passport", "idexists", "A new passport cannot already have an ID")).body(null);
        }
        PassportDTO result = passportService.save(passportDTO);
        return ResponseEntity.created(new URI("/api/passports/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("passport", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /passports : Updates an existing passport.
     *
     * @param passportDTO the passportDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated passportDTO,
     * or with status 400 (Bad Request) if the passportDTO is not valid,
     * or with status 500 (Internal Server Error) if the passportDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/passports",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PassportDTO> updatePassport(@Valid @RequestBody PassportDTO passportDTO) throws URISyntaxException {
        log.debug("REST request to update Passport : {}", passportDTO);
        if (passportDTO.getId() == null) {
            return createPassport(passportDTO);
        }
        PassportDTO result = passportService.save(passportDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("passport", passportDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /passports : get all the passports.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of passports in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/passports",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<PassportDTO>> getAllPassports(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Passports");
        Page<PassportDTO> page = passportService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/passports");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /passports/:id : get the "id" passport.
     *
     * @param id the id of the passportDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the passportDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/passports/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PassportDTO> getPassport(@PathVariable Long id) {
        log.debug("REST request to get Passport : {}", id);
        PassportDTO passportDTO = passportService.findOne(id);
        return Optional.ofNullable(passportDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /passports/:id : delete the "id" passport.
     *
     * @param id the id of the passportDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/passports/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deletePassport(@PathVariable Long id) {
        log.debug("REST request to delete Passport : {}", id);
        passportService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("passport", id.toString())).build();
    }

    /**
     * SEARCH  /_search/passports?query=:query : search for the passport corresponding
     * to the query.
     *
     * @param query the query of the passport search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/_search/passports",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<PassportDTO>> searchPassports(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Passports for query {}", query);
        Page<PassportDTO> page = passportService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/passports");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
