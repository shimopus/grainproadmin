package pro.grain.admin.repository.search;

import pro.grain.admin.domain.OrganisationType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the OrganisationType entity.
 */
public interface OrganisationTypeSearchRepository extends ElasticsearchRepository<OrganisationType, Long> {
}
