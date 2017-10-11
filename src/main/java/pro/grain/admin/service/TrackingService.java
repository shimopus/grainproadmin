package pro.grain.admin.service;

import pro.grain.admin.domain.Tracking;
import pro.grain.admin.repository.TrackingRepository;
import pro.grain.admin.repository.search.TrackingSearchRepository;
import pro.grain.admin.service.dto.TrackingDTO;
import pro.grain.admin.service.mapper.TrackingMapper;
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
 * Service Implementation for managing Tracking.
 */
@Service
@Transactional
public class TrackingService {

    private final Logger log = LoggerFactory.getLogger(TrackingService.class);

    private final TrackingRepository trackingRepository;

    private final TrackingMapper trackingMapper;

    private final TrackingSearchRepository trackingSearchRepository;

    @Inject
    public TrackingService(TrackingRepository trackingRepository, TrackingMapper trackingMapper, TrackingSearchRepository trackingSearchRepository) {
        this.trackingRepository = trackingRepository;
        this.trackingMapper = trackingMapper;
        this.trackingSearchRepository = trackingSearchRepository;
    }

    /**
     * Save a tracking.
     *
     * @param trackingDTO the entity to save
     * @return the persisted entity
     */
    public TrackingDTO save(TrackingDTO trackingDTO) {
        log.debug("Request to save Tracking : {}", trackingDTO);
        Tracking tracking = trackingMapper.trackingDTOToTracking(trackingDTO);
        tracking = trackingRepository.save(tracking);
        TrackingDTO result = trackingMapper.trackingToTrackingDTO(tracking);
        //Не будем сохранять трекинг в эластик. Пока незачем.
        //trackingSearchRepository.save(tracking);
        return result;
    }

    /**
     *  Get all the trackings.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<TrackingDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Trackings");
        Page<Tracking> result = trackingRepository.findAll(pageable);
        return result.map(tracking -> trackingMapper.trackingToTrackingDTO(tracking));
    }

    /**
     *  Get one tracking by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public TrackingDTO findOne(Long id) {
        log.debug("Request to get Tracking : {}", id);
        Tracking tracking = trackingRepository.findOne(id);
        TrackingDTO trackingDTO = trackingMapper.trackingToTrackingDTO(tracking);
        return trackingDTO;
    }

    /**
     *  Delete the  tracking by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Tracking : {}", id);
        trackingRepository.delete(id);
        trackingSearchRepository.delete(id);
    }

    /**
     * Search for the tracking corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<TrackingDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Trackings for query {}", query);
        Page<Tracking> result = trackingSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(tracking -> trackingMapper.trackingToTrackingDTO(tracking));
    }
}
