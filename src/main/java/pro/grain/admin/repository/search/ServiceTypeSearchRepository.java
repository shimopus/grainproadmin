package pro.grain.admin.repository.search;

import pro.grain.admin.domain.ServiceType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ServiceType entity.
 */
public interface ServiceTypeSearchRepository extends ElasticsearchRepository<ServiceType, Long> {
}
