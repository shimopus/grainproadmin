package pro.grain.admin.service;

import pro.grain.admin.config.GrainProAdminProperties;
import pro.grain.admin.domain.TransportationPrice;
import pro.grain.admin.repository.TransportationPriceRepository;
import pro.grain.admin.repository.search.TransportationPriceSearchRepository;
import pro.grain.admin.service.dto.TransportationPriceDTO;
import pro.grain.admin.service.mapper.TransportationPriceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing TransportationPrice.
 */
@Service
@Transactional
public class TransportationPriceService {

    private final Logger log = LoggerFactory.getLogger(TransportationPriceService.class);

    private final TransportationPriceRepository transportationPriceRepository;

    private final TransportationPriceMapper transportationPriceMapper;

    private final TransportationPriceSearchRepository transportationPriceSearchRepository;

    private final GrainProAdminProperties grainProAdminProperties;

    @Inject
    public TransportationPriceService(TransportationPriceRepository transportationPriceRepository,
                                      TransportationPriceMapper transportationPriceMapper,
                                      TransportationPriceSearchRepository transportationPriceSearchRepository,
                                      GrainProAdminProperties grainProAdminProperties) {
        this.transportationPriceRepository = transportationPriceRepository;
        this.transportationPriceMapper = transportationPriceMapper;
        this.transportationPriceSearchRepository = transportationPriceSearchRepository;
        this.grainProAdminProperties = grainProAdminProperties;
    }

    /**
     * Save a transportationPrice.
     *
     * @param transportationPriceDTO the entity to save
     * @return the persisted entity
     */
    public TransportationPriceDTO save(TransportationPriceDTO transportationPriceDTO) {
        log.debug("Request to save TransportationPrice : {}", transportationPriceDTO);
        TransportationPrice transportationPrice = transportationPriceMapper.transportationPriceDTOToTransportationPrice(transportationPriceDTO);
        transportationPrice = save(transportationPrice);
        TransportationPriceDTO result = transportationPriceMapper.transportationPriceToTransportationPriceDTO(transportationPrice);
        transportationPriceSearchRepository.save(transportationPrice);
        return result;
    }

    /**
     * Save a transportationPrice.
     *
     * @param transportationPrice the entity to save
     * @return the persisted entity
     */
    public TransportationPrice save(TransportationPrice transportationPrice) {
        transportationPrice = transportationPriceRepository.save(transportationPrice);
        return transportationPrice;
    }

    /**
     *  Get all the transportationPrices.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<TransportationPriceDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TransportationPrices");
        Page<TransportationPrice> result = transportationPriceRepository.findAll(pageable);
        return result.map(transportationPrice -> transportationPriceMapper.transportationPriceToTransportationPriceDTO(transportationPrice));
    }

    /**
     *  Get one transportationPrice by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public TransportationPriceDTO findOne(Long id) {
        log.debug("Request to get TransportationPrice : {}", id);
        TransportationPrice transportationPrice = transportationPriceRepository.findOne(id);
        return transportationPriceMapper.transportationPriceToTransportationPriceDTO(transportationPrice);
    }

    /**
     *  Get transportationPrice from stationA to stationB .
     *
     *  @param stationACode the Station code
     *  @param stationBCode the Station code
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public TransportationPriceDTO findOne(String stationACode, String stationBCode) {
        log.debug("Request to get TransportationPrice from {} to {}", stationACode, stationBCode);

        TransportationPrice transportationPrice =
            transportationPriceRepository.findByStationCodes(stationACode,
                stationBCode,
                grainProAdminProperties.getPrice().getCurrentVersionNumber());

        return transportationPriceMapper.transportationPriceToTransportationPriceDTO(transportationPrice);
    }

    /**
     *  Delete the  transportationPrice by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete TransportationPrice : {}", id);
        transportationPriceRepository.delete(id);
        transportationPriceSearchRepository.delete(id);
    }

    /**
     * Search for the transportationPrice corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<TransportationPriceDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TransportationPrices for query {}", query);
        Page<TransportationPrice> result = transportationPriceSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(transportationPrice -> transportationPriceMapper.transportationPriceToTransportationPriceDTO(transportationPrice));
    }
}
