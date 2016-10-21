package pro.grain.admin.repository.search;

import pro.grain.admin.domain.Locality;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Locality entity.
 */
public interface LocalitySearchRepository extends ElasticsearchRepository<Locality, Long> {
}
