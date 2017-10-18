package pro.grain.admin.service;

import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;
import pro.grain.admin.config.GrainProAdminProperties;
import pro.grain.admin.domain.EmailCampaign;
import pro.grain.admin.repository.EmailCampaignRepository;
import pro.grain.admin.repository.search.EmailCampaignSearchRepository;
import pro.grain.admin.service.dto.EmailCampaignDTO;
import pro.grain.admin.service.mapper.EmailCampaignMapper;
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
 * Service Implementation for managing EmailCampaign.
 */
@Service
@Transactional
public class EmailCampaignService {

    private final Logger log = LoggerFactory.getLogger(EmailCampaignService.class);

    @Inject
    private EmailCampaignRepository emailCampaignRepository;

    @Inject
    private EmailCampaignMapper emailCampaignMapper;

    @Inject
    private EmailCampaignSearchRepository emailCampaignSearchRepository;

    @Inject
    private RestTemplate restTemplate;

    @Inject
    private GrainProAdminProperties grainProAdminProperties;

    /**
     * Save a emailCampaign.
     *
     * @param emailCampaignDTO the entity to save
     * @return the persisted entity
     */
    public EmailCampaignDTO save(EmailCampaignDTO emailCampaignDTO) {
        log.debug("Request to save EmailCampaign : {}", emailCampaignDTO);
        boolean isNew = false;

        EmailCampaign emailCampaign = emailCampaignMapper.emailCampaignDTOToEmailCampaign(emailCampaignDTO);
        if (emailCampaign.getId() == null) {
            //new campaign is created
            isNew = true;
        }
        emailCampaign = emailCampaignRepository.save(emailCampaign);
        EmailCampaignDTO result = emailCampaignMapper.emailCampaignToEmailCampaignDTO(emailCampaign);

        if (isNew) {
            restTemplate.postForEntity(grainProAdminProperties.getMailer().getUrl() + "/emailCampaign/create",
                result, String.class);
        }

//        emailCampaignSearchRepository.save(emailCampaign);
        return result;
    }

    /**
     *  Get all the emailCampaigns.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<EmailCampaignDTO> findAll(Pageable pageable) {
        log.debug("Request to get all EmailCampaigns");
        Page<EmailCampaign> result = emailCampaignRepository.findAll(pageable);
        return result.map(emailCampaign -> emailCampaignMapper.emailCampaignToEmailCampaignDTO(emailCampaign));
    }

    /**
     *  Get one emailCampaign by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public EmailCampaignDTO findOne(Long id) {
        log.debug("Request to get EmailCampaign : {}", id);
        EmailCampaign emailCampaign = emailCampaignRepository.findOne(id);
        EmailCampaignDTO emailCampaignDTO = emailCampaignMapper.emailCampaignToEmailCampaignDTO(emailCampaign);
        return emailCampaignDTO;
    }

    /**
     *  Delete the  emailCampaign by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete EmailCampaign : {}", id);
        emailCampaignRepository.delete(id);
        emailCampaignSearchRepository.delete(id);
    }

    /**
     * Search for the emailCampaign corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<EmailCampaignDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of EmailCampaigns for query {}", query);
        Page<EmailCampaign> result = emailCampaignSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(emailCampaign -> emailCampaignMapper.emailCampaignToEmailCampaignDTO(emailCampaign));
    }
}
