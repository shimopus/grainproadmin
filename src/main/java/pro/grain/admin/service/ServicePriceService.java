package pro.grain.admin.service;

import pro.grain.admin.domain.ServicePrice;
import pro.grain.admin.repository.ServicePriceRepository;
import pro.grain.admin.repository.search.ServicePriceSearchRepository;
import pro.grain.admin.service.dto.ServicePriceDTO;
import pro.grain.admin.service.mapper.ServicePriceMapper;
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
 * Service Implementation for managing ServicePrice.
 */
@Service
@Transactional
public class ServicePriceService {

    private final Logger log = LoggerFactory.getLogger(ServicePriceService.class);
    
    @Inject
    private ServicePriceRepository servicePriceRepository;

    @Inject
    private ServicePriceMapper servicePriceMapper;

    @Inject
    private ServicePriceSearchRepository servicePriceSearchRepository;

    /**
     * Save a servicePrice.
     *
     * @param servicePriceDTO the entity to save
     * @return the persisted entity
     */
    public ServicePriceDTO save(ServicePriceDTO servicePriceDTO) {
        log.debug("Request to save ServicePrice : {}", servicePriceDTO);
        ServicePrice servicePrice = servicePriceMapper.servicePriceDTOToServicePrice(servicePriceDTO);
        servicePrice = servicePriceRepository.save(servicePrice);
        ServicePriceDTO result = servicePriceMapper.servicePriceToServicePriceDTO(servicePrice);
        servicePriceSearchRepository.save(servicePrice);
        return result;
    }

    /**
     *  Get all the servicePrices.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<ServicePriceDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ServicePrices");
        Page<ServicePrice> result = servicePriceRepository.findAll(pageable);
        return result.map(servicePrice -> servicePriceMapper.servicePriceToServicePriceDTO(servicePrice));
    }

    /**
     *  Get one servicePrice by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ServicePriceDTO findOne(Long id) {
        log.debug("Request to get ServicePrice : {}", id);
        ServicePrice servicePrice = servicePriceRepository.findOne(id);
        ServicePriceDTO servicePriceDTO = servicePriceMapper.servicePriceToServicePriceDTO(servicePrice);
        return servicePriceDTO;
    }

    /**
     *  Delete the  servicePrice by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ServicePrice : {}", id);
        servicePriceRepository.delete(id);
        servicePriceSearchRepository.delete(id);
    }

    /**
     * Search for the servicePrice corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ServicePriceDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ServicePrices for query {}", query);
        Page<ServicePrice> result = servicePriceSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(servicePrice -> servicePriceMapper.servicePriceToServicePriceDTO(servicePrice));
    }
}
