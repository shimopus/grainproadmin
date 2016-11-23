package pro.grain.admin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.grain.admin.repository.PartnerRepository;
import pro.grain.admin.repository.StationRepository;
import pro.grain.admin.repository.search.PartnerSearchRepository;
import pro.grain.admin.repository.search.StationSearchRepository;

import javax.inject.Inject;

@Service
@Transactional
public class ElasticSearchIndexRegenerateService {
    private final Logger log = LoggerFactory.getLogger(ElasticSearchIndexRegenerateService.class);

    private final StationRepository stationRepository;

    private final StationSearchRepository stationSearchRepository;

    private final PartnerRepository partnerRepository;

    private final PartnerSearchRepository partnerSearchRepository;

    @Inject
    public ElasticSearchIndexRegenerateService(StationSearchRepository stationSearchRepository, StationRepository stationRepository, PartnerRepository partnerRepository, PartnerSearchRepository partnerSearchRepository) {
        this.stationSearchRepository = stationSearchRepository;
        this.stationRepository = stationRepository;
        this.partnerRepository = partnerRepository;
        this.partnerSearchRepository = partnerSearchRepository;
    }

    public void resetStations() {
        log.debug("Request to reset Station index in Elastic");

        this.stationSearchRepository.save(this.stationRepository.findAll());
    }

    public void resetPartners() {
        log.debug("Request to reset Partner index in Elastic");

        this.partnerSearchRepository.save(this.partnerRepository.findAllWithEagerRelationships());
    }
}
