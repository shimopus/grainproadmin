package pro.grain.admin.service;

import pro.grain.admin.domain.ServiceType;
import pro.grain.admin.repository.ServiceTypeRepository;
import pro.grain.admin.repository.search.ServiceTypeSearchRepository;
import pro.grain.admin.service.dto.ServiceTypeDTO;
import pro.grain.admin.service.mapper.ServiceTypeMapper;
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
 * Service Implementation for managing ServiceType.
 */
@Service
@Transactional
public class ServiceTypeService {

    private final Logger log = LoggerFactory.getLogger(ServiceTypeService.class);
    
    @Inject
    private ServiceTypeRepository serviceTypeRepository;

    @Inject
    private ServiceTypeMapper serviceTypeMapper;

    @Inject
    private ServiceTypeSearchRepository serviceTypeSearchRepository;

    /**
     * Save a serviceType.
     *
     * @param serviceTypeDTO the entity to save
     * @return the persisted entity
     */
    public ServiceTypeDTO save(ServiceTypeDTO serviceTypeDTO) {
        log.debug("Request to save ServiceType : {}", serviceTypeDTO);
        ServiceType serviceType = serviceTypeMapper.serviceTypeDTOToServiceType(serviceTypeDTO);
        serviceType = serviceTypeRepository.save(serviceType);
        ServiceTypeDTO result = serviceTypeMapper.serviceTypeToServiceTypeDTO(serviceType);
        serviceTypeSearchRepository.save(serviceType);
        return result;
    }

    /**
     *  Get all the serviceTypes.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<ServiceTypeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ServiceTypes");
        Page<ServiceType> result = serviceTypeRepository.findAll(pageable);
        return result.map(serviceType -> serviceTypeMapper.serviceTypeToServiceTypeDTO(serviceType));
    }

    /**
     *  Get one serviceType by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ServiceTypeDTO findOne(Long id) {
        log.debug("Request to get ServiceType : {}", id);
        ServiceType serviceType = serviceTypeRepository.findOne(id);
        ServiceTypeDTO serviceTypeDTO = serviceTypeMapper.serviceTypeToServiceTypeDTO(serviceType);
        return serviceTypeDTO;
    }

    /**
     *  Delete the  serviceType by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ServiceType : {}", id);
        serviceTypeRepository.delete(id);
        serviceTypeSearchRepository.delete(id);
    }

    /**
     * Search for the serviceType corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ServiceTypeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ServiceTypes for query {}", query);
        Page<ServiceType> result = serviceTypeSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(serviceType -> serviceTypeMapper.serviceTypeToServiceTypeDTO(serviceType));
    }
}
