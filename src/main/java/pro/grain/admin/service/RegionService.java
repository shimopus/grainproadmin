package pro.grain.admin.service;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import pro.grain.admin.domain.Region;
import pro.grain.admin.repository.RegionRepository;
import pro.grain.admin.repository.search.RegionSearchRepository;
import pro.grain.admin.service.dto.RegionDTO;
import pro.grain.admin.service.mapper.RegionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Region.
 */
@Service
@Transactional
public class RegionService {

    private final Logger log = LoggerFactory.getLogger(RegionService.class);

    @Inject
    private RegionRepository regionRepository;

    @Inject
    private RegionMapper regionMapper;

    @Inject
    private RegionSearchRepository regionSearchRepository;

    /**
     * Save a region.
     *
     * @param regionDTO the entity to save
     * @return the persisted entity
     */
    public RegionDTO save(RegionDTO regionDTO) {
        log.debug("Request to save Region : {}", regionDTO);
        Region region = regionMapper.regionDTOToRegion(regionDTO);
        region = regionRepository.save(region);
        RegionDTO result = regionMapper.regionToRegionDTO(region);
        regionSearchRepository.save(region);
        return result;
    }

    /**
     *  Get all the regions.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<RegionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Regions");
        Page<Region> result = regionRepository.findAll(pageable);
        return result.map(region -> regionMapper.regionToRegionDTO(region));
    }

    /**
     *  Get one region by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public RegionDTO findOne(Long id) {
        log.debug("Request to get Region : {}", id);
        Region region = regionRepository.findOne(id);
        RegionDTO regionDTO = regionMapper.regionToRegionDTO(region);
        return regionDTO;
    }

    /**
     *  Delete the  region by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Region : {}", id);
        regionRepository.delete(id);
        regionSearchRepository.delete(id);
    }

    /**
     * Search for the region corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<RegionDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Regions for query {}", query);

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

        Page<Region> result = regionSearchRepository.search(searchQuery);
        return result.map(regionMapper::regionToRegionDTO);
    }
}
