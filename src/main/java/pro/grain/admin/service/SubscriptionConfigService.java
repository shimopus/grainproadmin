package pro.grain.admin.service;

import pro.grain.admin.domain.SubscriptionConfig;
import pro.grain.admin.repository.SubscriptionConfigRepository;
import pro.grain.admin.repository.search.SubscriptionConfigSearchRepository;
import pro.grain.admin.service.dto.SubscriptionConfigDTO;
import pro.grain.admin.service.mapper.SubscriptionConfigMapper;
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
 * Service Implementation for managing SubscriptionConfig.
 */
@Service
@Transactional
public class SubscriptionConfigService {

    private final Logger log = LoggerFactory.getLogger(SubscriptionConfigService.class);

    @Inject
    private SubscriptionConfigRepository subscriptionConfigRepository;

    @Inject
    private SubscriptionConfigMapper subscriptionConfigMapper;

    @Inject
    private SubscriptionConfigSearchRepository subscriptionConfigSearchRepository;

    /**
     * Save a subscriptionConfig.
     *
     * @param subscriptionConfigDTO the entity to save
     * @return the persisted entity
     */
    public SubscriptionConfigDTO save(SubscriptionConfigDTO subscriptionConfigDTO) {
        log.debug("Request to save SubscriptionConfig : {}", subscriptionConfigDTO);
        SubscriptionConfig subscriptionConfig = subscriptionConfigMapper.subscriptionConfigDTOToSubscriptionConfig(subscriptionConfigDTO);
        subscriptionConfig = subscriptionConfigRepository.save(subscriptionConfig);
        SubscriptionConfigDTO result = subscriptionConfigMapper.subscriptionConfigToSubscriptionConfigDTO(subscriptionConfig);
        subscriptionConfigSearchRepository.save(subscriptionConfig);
        return result;
    }

    /**
     * Get all the subscriptionConfigs.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<SubscriptionConfigDTO> findAll(Pageable pageable) {
        log.debug("Request to get all SubscriptionConfigs");
        Page<SubscriptionConfig> result = subscriptionConfigRepository.findAll(pageable);
        return result.map(subscriptionConfig -> subscriptionConfigMapper.subscriptionConfigToSubscriptionConfigDTO(subscriptionConfig));
    }

    /**
     * Get one subscriptionConfig by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public SubscriptionConfigDTO findOne(Long id) {
        log.debug("Request to get SubscriptionConfig : {}", id);
        SubscriptionConfig subscriptionConfig = subscriptionConfigRepository.findOne(id);
        SubscriptionConfigDTO subscriptionConfigDTO = subscriptionConfigMapper.subscriptionConfigToSubscriptionConfigDTO(subscriptionConfig);
        return subscriptionConfigDTO;
    }

    /**
     * Delete the  subscriptionConfig by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete SubscriptionConfig : {}", id);
        subscriptionConfigRepository.delete(id);
        subscriptionConfigSearchRepository.delete(id);
    }

    /**
     * Search for the subscriptionConfig corresponding to the query.
     *
     * @param query the query of the search
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<SubscriptionConfigDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of SubscriptionConfigs for query {}", query);
        Page<SubscriptionConfig> result = subscriptionConfigSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(subscriptionConfig -> subscriptionConfigMapper.subscriptionConfigToSubscriptionConfigDTO(subscriptionConfig));
    }

    @Transactional(readOnly = true)
    public SubscriptionConfigDTO findByPartner(Long partnerId) {
        log.debug("Request to get SubscriptionConfig by partner: {}", partnerId);
        SubscriptionConfig subscriptionConfig = subscriptionConfigRepository.findByPartner(partnerId);
        SubscriptionConfigDTO subscriptionConfigDTO = null;

        if (subscriptionConfig != null) {
            subscriptionConfigMapper.subscriptionConfigToSubscriptionConfigDTO(subscriptionConfig);
        } else {
            subscriptionConfigDTO = new SubscriptionConfigDTO();
            subscriptionConfigDTO.setPartnerId(partnerId);
        }
        return subscriptionConfigDTO;
    }
}
