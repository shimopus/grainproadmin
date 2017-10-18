package pro.grain.admin.repository.search;

import pro.grain.admin.domain.EmailCampaign;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the EmailCampaign entity.
 */
public interface EmailCampaignSearchRepository extends ElasticsearchRepository<EmailCampaign, Long> {
}
