package pro.grain.admin.service;

import pro.grain.admin.domain.OrganisationType;
import pro.grain.admin.repository.OrganisationTypeRepository;
import pro.grain.admin.repository.search.OrganisationTypeSearchRepository;
import pro.grain.admin.service.dto.OrganisationTypeDTO;
import pro.grain.admin.service.mapper.OrganisationTypeMapper;
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
 * Service Implementation for managing OrganisationType.
 */
@Service
@Transactional
public class OrganisationTypeService {

    private final Logger log = LoggerFactory.getLogger(OrganisationTypeService.class);
    
    @Inject
    private OrganisationTypeRepository organisationTypeRepository;

    @Inject
    private OrganisationTypeMapper organisationTypeMapper;

    @Inject
    private OrganisationTypeSearchRepository organisationTypeSearchRepository;

    /**
     * Save a organisationType.
     *
     * @param organisationTypeDTO the entity to save
     * @return the persisted entity
     */
    public OrganisationTypeDTO save(OrganisationTypeDTO organisationTypeDTO) {
        log.debug("Request to save OrganisationType : {}", organisationTypeDTO);
        OrganisationType organisationType = organisationTypeMapper.organisationTypeDTOToOrganisationType(organisationTypeDTO);
        organisationType = organisationTypeRepository.save(organisationType);
        OrganisationTypeDTO result = organisationTypeMapper.organisationTypeToOrganisationTypeDTO(organisationType);
        organisationTypeSearchRepository.save(organisationType);
        return result;
    }

    /**
     *  Get all the organisationTypes.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<OrganisationTypeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all OrganisationTypes");
        Page<OrganisationType> result = organisationTypeRepository.findAll(pageable);
        return result.map(organisationType -> organisationTypeMapper.organisationTypeToOrganisationTypeDTO(organisationType));
    }

    /**
     *  Get one organisationType by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public OrganisationTypeDTO findOne(Long id) {
        log.debug("Request to get OrganisationType : {}", id);
        OrganisationType organisationType = organisationTypeRepository.findOne(id);
        OrganisationTypeDTO organisationTypeDTO = organisationTypeMapper.organisationTypeToOrganisationTypeDTO(organisationType);
        return organisationTypeDTO;
    }

    /**
     *  Delete the  organisationType by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete OrganisationType : {}", id);
        organisationTypeRepository.delete(id);
        organisationTypeSearchRepository.delete(id);
    }

    /**
     * Search for the organisationType corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<OrganisationTypeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of OrganisationTypes for query {}", query);
        Page<OrganisationType> result = organisationTypeSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(organisationType -> organisationTypeMapper.organisationTypeToOrganisationTypeDTO(organisationType));
    }
}
