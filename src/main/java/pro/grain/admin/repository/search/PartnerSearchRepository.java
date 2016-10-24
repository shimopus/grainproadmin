package pro.grain.admin.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pro.grain.admin.domain.Partner;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Partner entity.
 */
public interface PartnerSearchRepository extends ElasticsearchRepository<Partner, Long> {
    Page<Partner> queryByNameContainingOrInnContainingOrCardContaining(String name, String inn, String card, Pageable pageable);
}
