package pro.grain.admin.service;

import pro.grain.admin.domain.QualityValue;
import pro.grain.admin.repository.QualityValueRepository;
import pro.grain.admin.repository.search.QualityValueSearchRepository;
import pro.grain.admin.service.dto.QualityValueDTO;
import pro.grain.admin.service.mapper.QualityValueMapper;
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
 * Service Implementation for managing QualityValue.
 */
@Service
@Transactional
public class QualityValueService {

    private final Logger log = LoggerFactory.getLogger(QualityValueService.class);
    
    @Inject
    private QualityValueRepository qualityValueRepository;

    @Inject
    private QualityValueMapper qualityValueMapper;

    @Inject
    private QualityValueSearchRepository qualityValueSearchRepository;

    /**
     * Save a qualityValue.
     *
     * @param qualityValueDTO the entity to save
     * @return the persisted entity
     */
    public QualityValueDTO save(QualityValueDTO qualityValueDTO) {
        log.debug("Request to save QualityValue : {}", qualityValueDTO);
        QualityValue qualityValue = qualityValueMapper.qualityValueDTOToQualityValue(qualityValueDTO);
        qualityValue = qualityValueRepository.save(qualityValue);
        QualityValueDTO result = qualityValueMapper.qualityValueToQualityValueDTO(qualityValue);
        qualityValueSearchRepository.save(qualityValue);
        return result;
    }

    /**
     *  Get all the qualityValues.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<QualityValueDTO> findAll(Pageable pageable) {
        log.debug("Request to get all QualityValues");
        Page<QualityValue> result = qualityValueRepository.findAll(pageable);
        return result.map(qualityValue -> qualityValueMapper.qualityValueToQualityValueDTO(qualityValue));
    }

    /**
     *  Get one qualityValue by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public QualityValueDTO findOne(Long id) {
        log.debug("Request to get QualityValue : {}", id);
        QualityValue qualityValue = qualityValueRepository.findOne(id);
        QualityValueDTO qualityValueDTO = qualityValueMapper.qualityValueToQualityValueDTO(qualityValue);
        return qualityValueDTO;
    }

    /**
     *  Delete the  qualityValue by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete QualityValue : {}", id);
        qualityValueRepository.delete(id);
        qualityValueSearchRepository.delete(id);
    }

    /**
     * Search for the qualityValue corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<QualityValueDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of QualityValues for query {}", query);
        Page<QualityValue> result = qualityValueSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(qualityValue -> qualityValueMapper.qualityValueToQualityValueDTO(qualityValue));
    }
}
