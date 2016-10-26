package pro.grain.admin.service;

import org.elasticsearch.index.query.QueryBuilder;
import pro.grain.admin.domain.Partner;
import pro.grain.admin.repository.PartnerRepository;
import pro.grain.admin.repository.search.PartnerSearchRepository;
import pro.grain.admin.service.dto.PartnerDTO;
import pro.grain.admin.service.mapper.PartnerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Partner.
 */
@Service
@Transactional
public class PartnerService {

    private final Logger log = LoggerFactory.getLogger(PartnerService.class);

    @Inject
    private PartnerRepository partnerRepository;

    @Inject
    private PartnerMapper partnerMapper;

    @Inject
    private PartnerSearchRepository partnerSearchRepository;

    /**
     * Save a partner.
     *
     * @param partnerDTO the entity to save
     * @return the persisted entity
     */
    public PartnerDTO save(PartnerDTO partnerDTO) {
        log.debug("Request to save Partner : {}", partnerDTO);
        Partner partner = partnerMapper.partnerDTOToPartner(partnerDTO);
        partner = partnerRepository.save(partner);
        PartnerDTO result = partnerMapper.partnerToPartnerDTO(partner);
        partnerSearchRepository.save(partner);
        return result;
    }

    /**
     * Get all the partners.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<PartnerDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Partners");
        Page<Partner> result = partnerRepository.findAll(pageable);
        return result.map(partner -> partnerMapper.partnerToPartnerDTO(partner));
    }

    /**
     * Get one partner by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public PartnerDTO findOne(Long id) {
        log.debug("Request to get Partner : {}", id);
        Partner partner = partnerRepository.findOneWithEagerRelationships(id);
        PartnerDTO partnerDTO = partnerMapper.partnerToPartnerDTO(partner);
        return partnerDTO;
    }

    /**
     * Delete the  partner by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Partner : {}", id);
        partnerRepository.delete(id);
        partnerSearchRepository.delete(id);
    }

    public List<PartnerDTO> children(Long id) {
        log.debug("Request to get children for Partner : {}", id);
        Set<Partner> children = partnerRepository.getOne(id).getOwnedBies();
        return children.stream().map(partner -> partnerMapper.partnerToPartnerDTO(partner)).collect(Collectors.toList());
    }

    public void addChild(Long childId) {
        log.debug("Request to add children for Partner : {}", childId);
        Partner parent = partnerRepository.findOneWithEagerRelationships(childId);

    }

    /**
     * Search for the partner corresponding to the query.
     *
     * @param query the query of the search
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<PartnerDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Partners for query {}", query);
//        Page<Partner> result = partnerSearchRepository.search(queryStringQuery(query), pageable);

        QueryBuilder nestedQuery = nestedQuery("contacts",
            boolQuery()
                .should(
                    queryStringQuery("*" + query + "*").analyzeWildcard(true)
                        .field("contacts.personName")
                        .field("contacts.phone")
                        .field("contacts.skype"))
        );

        QueryBuilder myQuery = boolQuery().should(queryStringQuery("*" + query + "*").analyzeWildcard(true).field("name").field("inn").field("card"))
            .should(nestedQuery);

        log.debug("My Query: " + myQuery);

        Page<Partner> result = partnerSearchRepository.search(myQuery, pageable);
        return result.map(partner -> partnerMapper.partnerToPartnerDTO(partner));
    }
}
