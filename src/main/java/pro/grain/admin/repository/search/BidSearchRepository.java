package pro.grain.admin.repository.search;

import pro.grain.admin.domain.Bid;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Bid entity.
 */
public interface BidSearchRepository extends ElasticsearchRepository<Bid, Long> {
}
