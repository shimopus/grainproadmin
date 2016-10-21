package pro.grain.admin.service;

import pro.grain.admin.domain.QualityParameter;
import pro.grain.admin.repository.QualityParameterRepository;
import pro.grain.admin.repository.search.QualityParameterSearchRepository;
import pro.grain.admin.service.dto.QualityParameterDTO;
import pro.grain.admin.service.mapper.QualityParameterMapper;
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
 * Service Implementation for managing QualityParameter.
 */
@Service
@Transactional
public class QualityParameterService {

    private final Logger log = LoggerFactory.getLogger(QualityParameterService.class);
    
    @Inject
    private QualityParameterRepository qualityParameterRepository;

    @Inject
    private QualityParameterMapper qualityParameterMapper;

    @Inject
    private QualityParameterSearchRepository qualityParameterSearchRepository;

    /**
     * Save a qualityParameter.
     *
     * @param qualityParameterDTO the entity to save
     * @return the persisted entity
     */
    public QualityParameterDTO save(QualityParameterDTO qualityParameterDTO) {
        log.debug("Request to save QualityParameter : {}", qualityParameterDTO);
        QualityParameter qualityParameter = qualityParameterMapper.qualityParameterDTOToQualityParameter(qualityParameterDTO);
        qualityParameter = qualityParameterRepository.save(qualityParameter);
        QualityParameterDTO result = qualityParameterMapper.qualityParameterToQualityParameterDTO(qualityParameter);
        qualityParameterSearchRepository.save(qualityParameter);
        return result;
    }

    /**
     *  Get all the qualityParameters.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<QualityParameterDTO> findAll(Pageable pageable) {
        log.debug("Request to get all QualityParameters");
        Page<QualityParameter> result = qualityParameterRepository.findAll(pageable);
        return result.map(qualityParameter -> qualityParameterMapper.qualityParameterToQualityParameterDTO(qualityParameter));
    }

    /**
     *  Get one qualityParameter by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public QualityParameterDTO findOne(Long id) {
        log.debug("Request to get QualityParameter : {}", id);
        QualityParameter qualityParameter = qualityParameterRepository.findOne(id);
        QualityParameterDTO qualityParameterDTO = qualityParameterMapper.qualityParameterToQualityParameterDTO(qualityParameter);
        return qualityParameterDTO;
    }

    /**
     *  Delete the  qualityParameter by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete QualityParameter : {}", id);
        qualityParameterRepository.delete(id);
        qualityParameterSearchRepository.delete(id);
    }

    /**
     * Search for the qualityParameter corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<QualityParameterDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of QualityParameters for query {}", query);
        Page<QualityParameter> result = qualityParameterSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(qualityParameter -> qualityParameterMapper.qualityParameterToQualityParameterDTO(qualityParameter));
    }
}
