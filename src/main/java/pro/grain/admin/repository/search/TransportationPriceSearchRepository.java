package pro.grain.admin.repository.search;

import pro.grain.admin.domain.TransportationPrice;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the TransportationPrice entity.
 */
public interface TransportationPriceSearchRepository extends ElasticsearchRepository<TransportationPrice, Long> {
}
