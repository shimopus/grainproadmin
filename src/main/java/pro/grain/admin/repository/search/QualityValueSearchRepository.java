package pro.grain.admin.repository.search;

import pro.grain.admin.domain.QualityValue;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the QualityValue entity.
 */
public interface QualityValueSearchRepository extends ElasticsearchRepository<QualityValue, Long> {
}
