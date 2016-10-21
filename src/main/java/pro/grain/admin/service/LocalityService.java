package pro.grain.admin.service;

import pro.grain.admin.domain.Locality;
import pro.grain.admin.repository.LocalityRepository;
import pro.grain.admin.repository.search.LocalitySearchRepository;
import pro.grain.admin.service.dto.LocalityDTO;
import pro.grain.admin.service.mapper.LocalityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Locality.
 */
@Service
@Transactional
public class LocalityService {

    private final Logger log = LoggerFactory.getLogger(LocalityService.class);
    
    @Inject
    private LocalityRepository localityRepository;

    @Inject
    private LocalityMapper localityMapper;

    @Inject
    private LocalitySearchRepository localitySearchRepository;

    /**
     * Save a locality.
     *
     * @param localityDTO the entity to save
     * @return the persisted entity
     */
    public LocalityDTO save(LocalityDTO localityDTO) {
        log.debug("Request to save Locality : {}", localityDTO);
        Locality locality = localityMapper.localityDTOToLocality(localityDTO);
        locality = localityRepository.save(locality);
        LocalityDTO result = localityMapper.localityToLocalityDTO(locality);
        localitySearchRepository.save(locality);
        return result;
    }

    /**
     *  Get all the localities.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<LocalityDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Localities");
        Page<Locality> result = localityRepository.findAll(pageable);
        return result.map(locality -> localityMapper.localityToLocalityDTO(locality));
    }

    /**
     *  Get one locality by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public LocalityDTO findOne(Long id) {
        log.debug("Request to get Locality : {}", id);
        Locality locality = localityRepository.findOne(id);
        LocalityDTO localityDTO = localityMapper.localityToLocalityDTO(locality);
        return localityDTO;
    }

    /**
     *  Delete the  locality by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Locality : {}", id);
        localityRepository.delete(id);
        localitySearchRepository.delete(id);
    }

    /**
     * Search for the locality corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<LocalityDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Localities for query {}", query);
        Page<Locality> result = localitySearchRepository.search(queryStringQuery(query), pageable);
        return result.map(locality -> localityMapper.localityToLocalityDTO(locality));
    }
}
