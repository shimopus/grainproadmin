package pro.grain.admin.repository.search;

import pro.grain.admin.domain.PriceUpdateQueue;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the PriceUpdateQueue entity.
 */
public interface PriceUpdateQueueSearchRepository extends ElasticsearchRepository<PriceUpdateQueue, Long> {
}
