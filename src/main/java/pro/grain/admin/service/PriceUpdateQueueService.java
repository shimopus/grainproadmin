package pro.grain.admin.service;

import pro.grain.admin.domain.PriceUpdateQueue;
import pro.grain.admin.repository.PriceUpdateQueueRepository;
import pro.grain.admin.repository.search.PriceUpdateQueueSearchRepository;
import pro.grain.admin.service.dto.PriceUpdateQueueDTO;
import pro.grain.admin.service.mapper.PriceUpdateQueueMapper;
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
 * Service Implementation for managing PriceUpdateQueue.
 */
@Service
@Transactional
public class PriceUpdateQueueService {

    private final Logger log = LoggerFactory.getLogger(PriceUpdateQueueService.class);
    
    @Inject
    private PriceUpdateQueueRepository priceUpdateQueueRepository;

    @Inject
    private PriceUpdateQueueMapper priceUpdateQueueMapper;

    @Inject
    private PriceUpdateQueueSearchRepository priceUpdateQueueSearchRepository;

    /**
     * Save a priceUpdateQueue.
     *
     * @param priceUpdateQueueDTO the entity to save
     * @return the persisted entity
     */
    public PriceUpdateQueueDTO save(PriceUpdateQueueDTO priceUpdateQueueDTO) {
        log.debug("Request to save PriceUpdateQueue : {}", priceUpdateQueueDTO);
        PriceUpdateQueue priceUpdateQueue = priceUpdateQueueMapper.priceUpdateQueueDTOToPriceUpdateQueue(priceUpdateQueueDTO);
        priceUpdateQueue = priceUpdateQueueRepository.save(priceUpdateQueue);
        PriceUpdateQueueDTO result = priceUpdateQueueMapper.priceUpdateQueueToPriceUpdateQueueDTO(priceUpdateQueue);
        priceUpdateQueueSearchRepository.save(priceUpdateQueue);
        return result;
    }

    /**
     *  Get all the priceUpdateQueues.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<PriceUpdateQueueDTO> findAll(Pageable pageable) {
        log.debug("Request to get all PriceUpdateQueues");
        Page<PriceUpdateQueue> result = priceUpdateQueueRepository.findAll(pageable);
        return result.map(priceUpdateQueue -> priceUpdateQueueMapper.priceUpdateQueueToPriceUpdateQueueDTO(priceUpdateQueue));
    }

    /**
     *  Get one priceUpdateQueue by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public PriceUpdateQueueDTO findOne(Long id) {
        log.debug("Request to get PriceUpdateQueue : {}", id);
        PriceUpdateQueue priceUpdateQueue = priceUpdateQueueRepository.findOne(id);
        PriceUpdateQueueDTO priceUpdateQueueDTO = priceUpdateQueueMapper.priceUpdateQueueToPriceUpdateQueueDTO(priceUpdateQueue);
        return priceUpdateQueueDTO;
    }

    /**
     *  Delete the  priceUpdateQueue by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete PriceUpdateQueue : {}", id);
        priceUpdateQueueRepository.delete(id);
        priceUpdateQueueSearchRepository.delete(id);
    }

    /**
     * Search for the priceUpdateQueue corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<PriceUpdateQueueDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of PriceUpdateQueues for query {}", query);
        Page<PriceUpdateQueue> result = priceUpdateQueueSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(priceUpdateQueue -> priceUpdateQueueMapper.priceUpdateQueueToPriceUpdateQueueDTO(priceUpdateQueue));
    }
}
