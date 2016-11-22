package pro.grain.admin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.grain.admin.repository.StationRepository;
import pro.grain.admin.repository.search.StationSearchRepository;

import javax.inject.Inject;

@Service
@Transactional
public class ElasticSearchIndexRegenerateService {
    private final Logger log = LoggerFactory.getLogger(ElasticSearchIndexRegenerateService.class);

    private final StationRepository stationRepository;

    private final StationSearchRepository stationSearchRepository;

    @Inject
    public ElasticSearchIndexRegenerateService(StationSearchRepository stationSearchRepository, StationRepository stationRepository) {
        this.stationSearchRepository = stationSearchRepository;
        this.stationRepository = stationRepository;
    }

    public void resetStations() {
        log.debug("Request to reset Station index in Elastic");

        this.stationSearchRepository.save(this.stationRepository.findAll());
    }
}
