package pro.grain.admin.repository.search;

import pro.grain.admin.domain.ServicePrice;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ServicePrice entity.
 */
public interface ServicePriceSearchRepository extends ElasticsearchRepository<ServicePrice, Long> {
}
