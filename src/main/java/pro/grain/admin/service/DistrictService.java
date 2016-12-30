package pro.grain.admin.service;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import pro.grain.admin.domain.District;
import pro.grain.admin.repository.DistrictRepository;
import pro.grain.admin.repository.search.DistrictSearchRepository;
import pro.grain.admin.service.dto.DistrictDTO;
import pro.grain.admin.service.mapper.DistrictMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing District.
 */
@Service
@Transactional
public class DistrictService {

    private final Logger log = LoggerFactory.getLogger(DistrictService.class);

    @Inject
    private DistrictRepository districtRepository;

    @Inject
    private DistrictMapper districtMapper;

    @Inject
    private DistrictSearchRepository districtSearchRepository;

    /**
     * Save a district.
     *
     * @param districtDTO the entity to save
     * @return the persisted entity
     */
    public DistrictDTO save(DistrictDTO districtDTO) {
        log.debug("Request to save District : {}", districtDTO);
        District district = districtMapper.districtDTOToDistrict(districtDTO);
        district = districtRepository.save(district);
        DistrictDTO result = districtMapper.districtToDistrictDTO(district);
        districtSearchRepository.save(district);
        return result;
    }

    /**
     *  Get all the districts.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<DistrictDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Districts");
        Page<District> result = districtRepository.findAll(pageable);
        return result.map(district -> districtMapper.districtToDistrictDTO(district));
    }

    /**
     *  Get one district by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public DistrictDTO findOne(Long id) {
        log.debug("Request to get District : {}", id);
        District district = districtRepository.findOne(id);
        DistrictDTO districtDTO = districtMapper.districtToDistrictDTO(district);
        return districtDTO;
    }

    /**
     *  Delete the  district by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete District : {}", id);
        districtRepository.delete(id);
        districtSearchRepository.delete(id);
    }

    /**
     * Search for the district corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<DistrictDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Districts for query {}", query);

        QueryBuilder myQuery = boolQuery().should(
            queryStringQuery("*" + query + "*").analyzeWildcard(true).
                field("name"));

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
            .withQuery(myQuery)
            .withSort(
                new ScoreSortBuilder()
                .order(SortOrder.ASC)
            )
            .withPageable(pageable)
            .build();


        log.debug("My Query: " + searchQuery);

        Page<District> result = districtSearchRepository.search(searchQuery);
        return result.map(districtMapper::districtToDistrictDTO);
    }
}
