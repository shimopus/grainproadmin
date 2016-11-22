package pro.grain.admin.web.rest;

import pro.grain.admin.GrainAdminApp;

import pro.grain.admin.domain.Station;
import pro.grain.admin.repository.StationRepository;
import pro.grain.admin.service.StationService;
import pro.grain.admin.repository.search.StationSearchRepository;
import pro.grain.admin.service.dto.StationDTO;
import pro.grain.admin.service.mapper.StationMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the StationResource REST controller.
 *
 * @see StationResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GrainAdminApp.class)
public class StationResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    private static final String DEFAULT_CODE = "AAAAA";
    private static final String UPDATED_CODE = "BBBBB";

    @Inject
    private StationRepository stationRepository;

    @Inject
    private StationMapper stationMapper;

    @Inject
    private StationService stationService;

    @Inject
    private StationSearchRepository stationSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restStationMockMvc;

    private Station station;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        StationResource stationResource = new StationResource();
        ReflectionTestUtils.setField(stationResource, "stationService", stationService);
        this.restStationMockMvc = MockMvcBuilders.standaloneSetup(stationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Station createEntity(EntityManager em) {
        Station station = new Station()
                .name(DEFAULT_NAME)
                .code(DEFAULT_CODE);
        return station;
    }

    @Before
    public void initTest() {
        stationSearchRepository.deleteAll();
        station = createEntity(em);
    }

    @Test
    @Transactional
    public void createStation() throws Exception {
        int databaseSizeBeforeCreate = stationRepository.findAll().size();

        // Create the Station
        StationDTO stationDTO = stationMapper.stationToStationDTO(station);

        restStationMockMvc.perform(post("/api/stations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(stationDTO)))
                .andExpect(status().isCreated());

        // Validate the Station in the database
        List<Station> stations = stationRepository.findAll();
        assertThat(stations).hasSize(databaseSizeBeforeCreate + 1);
        Station testStation = stations.get(stations.size() - 1);
        assertThat(testStation.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testStation.getCode()).isEqualTo(DEFAULT_CODE);

        // Validate the Station in ElasticSearch
        Station stationEs = stationSearchRepository.findOne(testStation.getId());
        assertThat(stationEs).isEqualToComparingFieldByField(testStation);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = stationRepository.findAll().size();
        // set the field null
        station.setName(null);

        // Create the Station, which fails.
        StationDTO stationDTO = stationMapper.stationToStationDTO(station);

        restStationMockMvc.perform(post("/api/stations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(stationDTO)))
                .andExpect(status().isBadRequest());

        List<Station> stations = stationRepository.findAll();
        assertThat(stations).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = stationRepository.findAll().size();
        // set the field null
        station.setCode(null);

        // Create the Station, which fails.
        StationDTO stationDTO = stationMapper.stationToStationDTO(station);

        restStationMockMvc.perform(post("/api/stations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(stationDTO)))
                .andExpect(status().isBadRequest());

        List<Station> stations = stationRepository.findAll();
        assertThat(stations).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllStations() throws Exception {
        // Initialize the database
        stationRepository.saveAndFlush(station);

        // Get all the stations
        restStationMockMvc.perform(get("/api/stations?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(station.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())));
    }

    @Test
    @Transactional
    public void getStation() throws Exception {
        // Initialize the database
        stationRepository.saveAndFlush(station);

        // Get the station
        restStationMockMvc.perform(get("/api/stations/{id}", station.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(station.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingStation() throws Exception {
        // Get the station
        restStationMockMvc.perform(get("/api/stations/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateStation() throws Exception {
        // Initialize the database
        stationRepository.saveAndFlush(station);
        stationSearchRepository.save(station);
        int databaseSizeBeforeUpdate = stationRepository.findAll().size();

        // Update the station
        Station updatedStation = stationRepository.findOne(station.getId());
        updatedStation
                .name(UPDATED_NAME)
                .code(UPDATED_CODE);
        StationDTO stationDTO = stationMapper.stationToStationDTO(updatedStation);

        restStationMockMvc.perform(put("/api/stations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(stationDTO)))
                .andExpect(status().isOk());

        // Validate the Station in the database
        List<Station> stations = stationRepository.findAll();
        assertThat(stations).hasSize(databaseSizeBeforeUpdate);
        Station testStation = stations.get(stations.size() - 1);
        assertThat(testStation.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStation.getCode()).isEqualTo(UPDATED_CODE);

        // Validate the Station in ElasticSearch
        Station stationEs = stationSearchRepository.findOne(testStation.getId());
        assertThat(stationEs).isEqualToComparingFieldByField(testStation);
    }

    @Test
    @Transactional
    public void deleteStation() throws Exception {
        // Initialize the database
        stationRepository.saveAndFlush(station);
        stationSearchRepository.save(station);
        int databaseSizeBeforeDelete = stationRepository.findAll().size();

        // Get the station
        restStationMockMvc.perform(delete("/api/stations/{id}", station.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean stationExistsInEs = stationSearchRepository.exists(station.getId());
        assertThat(stationExistsInEs).isFalse();

        // Validate the database is empty
        List<Station> stations = stationRepository.findAll();
        assertThat(stations).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchStation() throws Exception {
        // Initialize the database
        stationRepository.saveAndFlush(station);
        stationSearchRepository.save(station);

        // Search the station
        restStationMockMvc.perform(get("/api/_search/stations?query=" + station.getName()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(station.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())));
    }
}
