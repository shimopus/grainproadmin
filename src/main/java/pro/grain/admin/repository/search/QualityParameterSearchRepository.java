package pro.grain.admin.repository.search;

import pro.grain.admin.domain.QualityParameter;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the QualityParameter entity.
 */
public interface QualityParameterSearchRepository extends ElasticsearchRepository<QualityParameter, Long> {
}
