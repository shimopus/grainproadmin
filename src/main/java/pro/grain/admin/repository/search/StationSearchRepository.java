package pro.grain.admin.repository.search;

import pro.grain.admin.domain.Station;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Station entity.
 */
public interface StationSearchRepository extends ElasticsearchRepository<Station, Long> {
}
