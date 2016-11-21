package pro.grain.admin.service;

import pro.grain.admin.domain.Passport;
import pro.grain.admin.repository.PassportRepository;
import pro.grain.admin.repository.search.PassportSearchRepository;
import pro.grain.admin.service.dto.PassportDTO;
import pro.grain.admin.service.mapper.PassportMapper;
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
 * Service Implementation for managing Passport.
 */
@Service
@Transactional
public class PassportService {

    private final Logger log = LoggerFactory.getLogger(PassportService.class);
    
    @Inject
    private PassportRepository passportRepository;

    @Inject
    private PassportMapper passportMapper;

    @Inject
    private PassportSearchRepository passportSearchRepository;

    /**
     * Save a passport.
     *
     * @param passportDTO the entity to save
     * @return the persisted entity
     */
    public PassportDTO save(PassportDTO passportDTO) {
        log.debug("Request to save Passport : {}", passportDTO);
        Passport passport = passportMapper.passportDTOToPassport(passportDTO);
        passport = passportRepository.save(passport);
        PassportDTO result = passportMapper.passportToPassportDTO(passport);
        passportSearchRepository.save(passport);
        return result;
    }

    /**
     *  Get all the passports.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<PassportDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Passports");
        Page<Passport> result = passportRepository.findAll(pageable);
        return result.map(passport -> passportMapper.passportToPassportDTO(passport));
    }

    /**
     *  Get one passport by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public PassportDTO findOne(Long id) {
        log.debug("Request to get Passport : {}", id);
        Passport passport = passportRepository.findOne(id);
        PassportDTO passportDTO = passportMapper.passportToPassportDTO(passport);
        return passportDTO;
    }

    /**
     *  Delete the  passport by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Passport : {}", id);
        passportRepository.delete(id);
        passportSearchRepository.delete(id);
    }

    /**
     * Search for the passport corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<PassportDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Passports for query {}", query);
        Page<Passport> result = passportSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(passport -> passportMapper.passportToPassportDTO(passport));
    }
}
