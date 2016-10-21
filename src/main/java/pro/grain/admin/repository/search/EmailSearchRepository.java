package pro.grain.admin.repository.search;

import pro.grain.admin.domain.Email;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Email entity.
 */
public interface EmailSearchRepository extends ElasticsearchRepository<Email, Long> {
}
