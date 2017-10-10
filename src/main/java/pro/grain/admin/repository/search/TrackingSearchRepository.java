package pro.grain.admin.repository.search;

import pro.grain.admin.domain.Tracking;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Tracking entity.
 */
public interface TrackingSearchRepository extends ElasticsearchRepository<Tracking, Long> {
}
