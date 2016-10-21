package pro.grain.admin.service;

import pro.grain.admin.domain.TransportationPrice;
import pro.grain.admin.repository.TransportationPriceRepository;
import pro.grain.admin.repository.search.TransportationPriceSearchRepository;
import pro.grain.admin.service.dto.TransportationPriceDTO;
import pro.grain.admin.service.mapper.TransportationPriceMapper;
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
 * Service Implementation for managing TransportationPrice.
 */
@Service
@Transactional
public class TransportationPriceService {

    private final Logger log = LoggerFactory.getLogger(TransportationPriceService.class);
    
    @Inject
    private TransportationPriceRepository transportationPriceRepository;

    @Inject
    private TransportationPriceMapper transportationPriceMapper;

    @Inject
    private TransportationPriceSearchRepository transportationPriceSearchRepository;

    /**
     * Save a transportationPrice.
     *
     * @param transportationPriceDTO the entity to save
     * @return the persisted entity
     */
    public TransportationPriceDTO save(TransportationPriceDTO transportationPriceDTO) {
        log.debug("Request to save TransportationPrice : {}", transportationPriceDTO);
        TransportationPrice transportationPrice = transportationPriceMapper.transportationPriceDTOToTransportationPrice(transportationPriceDTO);
        transportationPrice = transportationPriceRepository.save(transportationPrice);
        TransportationPriceDTO result = transportationPriceMapper.transportationPriceToTransportationPriceDTO(transportationPrice);
        transportationPriceSearchRepository.save(transportationPrice);
        return result;
    }

    /**
     *  Get all the transportationPrices.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<TransportationPriceDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TransportationPrices");
        Page<TransportationPrice> result = transportationPriceRepository.findAll(pageable);
        return result.map(transportationPrice -> transportationPriceMapper.transportationPriceToTransportationPriceDTO(transportationPrice));
    }

    /**
     *  Get one transportationPrice by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public TransportationPriceDTO findOne(Long id) {
        log.debug("Request to get TransportationPrice : {}", id);
        TransportationPrice transportationPrice = transportationPriceRepository.findOne(id);
        TransportationPriceDTO transportationPriceDTO = transportationPriceMapper.transportationPriceToTransportationPriceDTO(transportationPrice);
        return transportationPriceDTO;
    }

    /**
     *  Delete the  transportationPrice by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete TransportationPrice : {}", id);
        transportationPriceRepository.delete(id);
        transportationPriceSearchRepository.delete(id);
    }

    /**
     * Search for the transportationPrice corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<TransportationPriceDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TransportationPrices for query {}", query);
        Page<TransportationPrice> result = transportationPriceSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(transportationPrice -> transportationPriceMapper.transportationPriceToTransportationPriceDTO(transportationPrice));
    }
}
