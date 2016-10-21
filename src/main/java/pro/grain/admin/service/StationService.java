package pro.grain.admin.service;

import pro.grain.admin.domain.Station;
import pro.grain.admin.repository.StationRepository;
import pro.grain.admin.repository.search.StationSearchRepository;
import pro.grain.admin.service.dto.StationDTO;
import pro.grain.admin.service.mapper.StationMapper;
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
 * Service Implementation for managing Station.
 */
@Service
@Transactional
public class StationService {

    private final Logger log = LoggerFactory.getLogger(StationService.class);
    
    @Inject
    private StationRepository stationRepository;

    @Inject
    private StationMapper stationMapper;

    @Inject
    private StationSearchRepository stationSearchRepository;

    /**
     * Save a station.
     *
     * @param stationDTO the entity to save
     * @return the persisted entity
     */
    public StationDTO save(StationDTO stationDTO) {
        log.debug("Request to save Station : {}", stationDTO);
        Station station = stationMapper.stationDTOToStation(stationDTO);
        station = stationRepository.save(station);
        StationDTO result = stationMapper.stationToStationDTO(station);
        stationSearchRepository.save(station);
        return result;
    }

    /**
     *  Get all the stations.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<StationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Stations");
        Page<Station> result = stationRepository.findAll(pageable);
        return result.map(station -> stationMapper.stationToStationDTO(station));
    }

    /**
     *  Get one station by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public StationDTO findOne(Long id) {
        log.debug("Request to get Station : {}", id);
        Station station = stationRepository.findOne(id);
        StationDTO stationDTO = stationMapper.stationToStationDTO(station);
        return stationDTO;
    }

    /**
     *  Delete the  station by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Station : {}", id);
        stationRepository.delete(id);
        stationSearchRepository.delete(id);
    }

    /**
     * Search for the station corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<StationDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Stations for query {}", query);
        Page<Station> result = stationSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(station -> stationMapper.stationToStationDTO(station));
    }
}
