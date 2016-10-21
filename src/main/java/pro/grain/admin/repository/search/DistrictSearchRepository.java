package pro.grain.admin.repository.search;

import pro.grain.admin.domain.District;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the District entity.
 */
public interface DistrictSearchRepository extends ElasticsearchRepository<District, Long> {
}
