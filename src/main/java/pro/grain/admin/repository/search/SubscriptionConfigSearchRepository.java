package pro.grain.admin.repository.search;

import pro.grain.admin.domain.SubscriptionConfig;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the SubscriptionConfig entity.
 */
public interface SubscriptionConfigSearchRepository extends ElasticsearchRepository<SubscriptionConfig, Long> {
}
