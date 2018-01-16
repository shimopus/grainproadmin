package pro.grain.admin.service;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.scheduling.annotation.Async;
import pro.grain.admin.config.GrainProAdminProperties;
import pro.grain.admin.domain.PriceUpdateQueue;
import pro.grain.admin.domain.Station;
import pro.grain.admin.repository.LocationToBaseStationRepository;
import pro.grain.admin.repository.PriceUpdateQueueRepository;
import pro.grain.admin.repository.search.PriceUpdateQueueSearchRepository;
import pro.grain.admin.service.dto.PriceUpdateQueueDTO;
import pro.grain.admin.service.mapper.PriceUpdateQueueMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing PriceUpdateQueue.
 */
@Service
@Transactional
public class PriceUpdateQueueService {

    private final Logger log = LoggerFactory.getLogger(PriceUpdateQueueService.class);

    @Inject
    private PriceUpdateQueueRepository priceUpdateQueueRepository;

    @Inject
    private PriceUpdateQueueMapper priceUpdateQueueMapper;

    @Inject
    private PriceUpdateQueueSearchRepository priceUpdateQueueSearchRepository;

    @Inject
    private LocationToBaseStationRepository locationToBaseStationRepository;

    @Inject
    EntityManager entityManager;

    @Inject
    private GrainProAdminProperties grainProAdminProperties;

    /**
     * Save a priceUpdateQueue.
     *
     * @param priceUpdateQueueDTO the entity to save
     * @return the persisted entity
     */
    public PriceUpdateQueueDTO save(PriceUpdateQueueDTO priceUpdateQueueDTO) {
        log.debug("Request to save PriceUpdateQueue : {}", priceUpdateQueueDTO);
        PriceUpdateQueue priceUpdateQueue = priceUpdateQueueMapper.priceUpdateQueueDTOToPriceUpdateQueue(priceUpdateQueueDTO);
        priceUpdateQueue = priceUpdateQueueRepository.save(priceUpdateQueue);
        PriceUpdateQueueDTO result = priceUpdateQueueMapper.priceUpdateQueueToPriceUpdateQueueDTO(priceUpdateQueue);
        priceUpdateQueueSearchRepository.save(priceUpdateQueue);
        return result;
    }

    /**
     *  Get all the priceUpdateQueues.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<PriceUpdateQueueDTO> findAll(Pageable pageable) {
        log.debug("Request to get all PriceUpdateQueues");
        Page<PriceUpdateQueue> result = priceUpdateQueueRepository.findAll(pageable);
        return result.map(priceUpdateQueueMapper::priceUpdateQueueToPriceUpdateQueueDTO);
    }

    /**
     *  Get one priceUpdateQueue by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public PriceUpdateQueueDTO findOne(Long id) {
        log.debug("Request to get PriceUpdateQueue : {}", id);
        PriceUpdateQueue priceUpdateQueue = priceUpdateQueueRepository.findOne(id);
        return priceUpdateQueueMapper.priceUpdateQueueToPriceUpdateQueueDTO(priceUpdateQueue);
    }

    public synchronized PriceUpdateQueueDTO findNextAvailable() {
        log.debug("Request to get next available Price Update Queue");

        entityManager.clear();

        PriceUpdateQueue priceUpdateQueue = priceUpdateQueueRepository.findNextAvailable();

        if (priceUpdateQueue == null) return null;

        priceUpdateQueue.setLoaded(true);
        priceUpdateQueueRepository.saveAndFlush(priceUpdateQueue);

        return priceUpdateQueueMapper.priceUpdateQueueToPriceUpdateQueueDTO(priceUpdateQueue);
    }

    public void markAsUnavailable(Long priceUpdateQueueId) {
        priceUpdateQueueRepository.markAsUnavailable(priceUpdateQueueId);
    }

    /**
     *  Delete the  priceUpdateQueue by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete PriceUpdateQueue : {}", id);
        priceUpdateQueueRepository.delete(id);
        priceUpdateQueueSearchRepository.delete(id);
    }

    /**
     * Search for the priceUpdateQueue corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<PriceUpdateQueueDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of PriceUpdateQueues for query {}", query);
        Page<PriceUpdateQueue> result = priceUpdateQueueSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(priceUpdateQueueMapper::priceUpdateQueueToPriceUpdateQueueDTO);
    }

    public void clearQueue() {
        log.debug("Clear Download Queue");

        priceUpdateQueueRepository.deleteAllInBatch();
    }

    @Async
    @Transactional
    public CompletableFuture<Integer> initializeQueue(int from) {
        log.warn("!!!!!!!!!!! Initialize Download Queue !!!!!!!!!!!!!!!");

        List<Pair<Station, Station>> queue = new ArrayList<>();

        List<Station> baseStations = locationToBaseStationRepository.getBaseStationsForCurrentPartners();

        for (int i = 0; i < baseStations.size(); i++) {
            Station stationFrom = baseStations.get(i);

            for (int j = i; j < baseStations.size(); j++) {
                Station stationTo = baseStations.get(j);

                queue.add(new ImmutablePair<>(stationFrom, stationTo));
            }
        }

        long order = from*100;
        int to = from + grainProAdminProperties.getPriceUpload().getDownloadBucketSize();

        log.warn("Iteration queue size: {}, to: {}", queue.size(), to);

        for (int i = from; i < to && i < queue.size(); i++) {
            log.warn("Get item: {}", i);
            Pair<Station, Station> pair = queue.get(i);

            if (pair.getRight().equals(pair.getLeft())) {
                continue;
            }

            int prices = priceUpdateQueueRepository.findByStationCodes(
                pair.getLeft().getCode(),
                pair.getRight().getCode(),
                grainProAdminProperties.getPrice().getCurrentVersionNumber()+1);

            if (prices == 0) {
                log.warn("Adding new in queue from {} to {}. Prices {}", pair.getLeft().getName(), pair.getRight().getName(), prices);
                priceUpdateQueueRepository.save(new PriceUpdateQueue(
                    false,
                    order,
                    pair.getLeft(),
                    pair.getRight()
                ));

                order += 100;
            } else {
                log.warn("Check price from {} to {}. Price count: {}", pair.getLeft().getName(), pair.getRight().getName(), prices);
            }

            if (i % 50 == 0) {
                try {
                    log.warn("Try to flush");
                    entityManager.flush();
                    entityManager.clear();
                } catch (Throwable e) {
                    log.error("Can not flush1 ", e);
                }
            }
        }

        try {
            entityManager.flush();
            entityManager.clear();
        } catch (Exception e) {
            log.error("Can not flush2 ", e);
        }

        log.warn("!!!!!!!!!!! Download Queue is initialized with size {} !!!!!!!!!!!!!", priceUpdateQueueRepository.count());

        return CompletableFuture.completedFuture(to < queue.size() ? to+1 : -1);
    }
}
