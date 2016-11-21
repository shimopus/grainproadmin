package pro.grain.admin.repository.search;

import pro.grain.admin.domain.Passport;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Passport entity.
 */
public interface PassportSearchRepository extends ElasticsearchRepository<Passport, Long> {
}
