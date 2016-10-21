package pro.grain.admin.repository.search;

import pro.grain.admin.domain.Partner;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Partner entity.
 */
public interface PartnerSearchRepository extends ElasticsearchRepository<Partner, Long> {
}
