package pro.grain.admin.web.rest;

import pro.grain.admin.GrainAdminApp;

import pro.grain.admin.domain.Tracking;
import pro.grain.admin.repository.TrackingRepository;
import pro.grain.admin.service.TrackingService;
import pro.grain.admin.repository.search.TrackingSearchRepository;
import pro.grain.admin.service.dto.TrackingDTO;
import pro.grain.admin.service.mapper.TrackingMapper;

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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import pro.grain.admin.domain.enumeration.MailOpenType;
/**
 * Test class for the TrackingResource REST controller.
 *
 * @see TrackingResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GrainAdminApp.class)
public class TrackingResourceIntTest {

    private static final LocalDate DEFAULT_MAIL_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_MAIL_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final MailOpenType DEFAULT_OPEN_TYPE = MailOpenType.OPEN;
    private static final MailOpenType UPDATED_OPEN_TYPE = MailOpenType.FILE_OPEN;

    private static final Integer DEFAULT_OPEN_COUNT = 1;
    private static final Integer UPDATED_OPEN_COUNT = 2;

    @Inject
    private TrackingRepository trackingRepository;

    @Inject
    private TrackingMapper trackingMapper;

    @Inject
    private TrackingService trackingService;

    @Inject
    private TrackingSearchRepository trackingSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restTrackingMockMvc;

    private Tracking tracking;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TrackingResource trackingResource = new TrackingResource();
        ReflectionTestUtils.setField(trackingResource, "trackingService", trackingService);
        this.restTrackingMockMvc = MockMvcBuilders.standaloneSetup(trackingResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tracking createEntity(EntityManager em) {
        Tracking tracking = new Tracking()
                .mailDate(DEFAULT_MAIL_DATE)
                .openType(DEFAULT_OPEN_TYPE)
                .openCount(DEFAULT_OPEN_COUNT);
        return tracking;
    }

    @Before
    public void initTest() {
        trackingSearchRepository.deleteAll();
        tracking = createEntity(em);
    }

    @Test
    @Transactional
    public void createTracking() throws Exception {
        int databaseSizeBeforeCreate = trackingRepository.findAll().size();

        // Create the Tracking
        TrackingDTO trackingDTO = trackingMapper.trackingToTrackingDTO(tracking);

        restTrackingMockMvc.perform(post("/api/trackings")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(trackingDTO)))
                .andExpect(status().isCreated());

        // Validate the Tracking in the database
        List<Tracking> trackings = trackingRepository.findAll();
        assertThat(trackings).hasSize(databaseSizeBeforeCreate + 1);
        Tracking testTracking = trackings.get(trackings.size() - 1);
        assertThat(testTracking.getMailDate()).isEqualTo(DEFAULT_MAIL_DATE);
        assertThat(testTracking.getOpenType()).isEqualTo(DEFAULT_OPEN_TYPE);
        assertThat(testTracking.getOpenCount()).isEqualTo(DEFAULT_OPEN_COUNT);

        // Validate the Tracking in ElasticSearch
        Tracking trackingEs = trackingSearchRepository.findOne(testTracking.getId());
        assertThat(trackingEs).isEqualToComparingFieldByField(testTracking);
    }

    @Test
    @Transactional
    public void getAllTrackings() throws Exception {
        // Initialize the database
        trackingRepository.saveAndFlush(tracking);

        // Get all the trackings
        restTrackingMockMvc.perform(get("/api/trackings?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(tracking.getId().intValue())))
                .andExpect(jsonPath("$.[*].mailDate").value(hasItem(DEFAULT_MAIL_DATE.toString())))
                .andExpect(jsonPath("$.[*].openType").value(hasItem(DEFAULT_OPEN_TYPE.toString())))
                .andExpect(jsonPath("$.[*].openCount").value(hasItem(DEFAULT_OPEN_COUNT)));
    }

    @Test
    @Transactional
    public void getTracking() throws Exception {
        // Initialize the database
        trackingRepository.saveAndFlush(tracking);

        // Get the tracking
        restTrackingMockMvc.perform(get("/api/trackings/{id}", tracking.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(tracking.getId().intValue()))
            .andExpect(jsonPath("$.mailDate").value(DEFAULT_MAIL_DATE.toString()))
            .andExpect(jsonPath("$.openType").value(DEFAULT_OPEN_TYPE.toString()))
            .andExpect(jsonPath("$.openCount").value(DEFAULT_OPEN_COUNT));
    }

    @Test
    @Transactional
    public void getNonExistingTracking() throws Exception {
        // Get the tracking
        restTrackingMockMvc.perform(get("/api/trackings/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTracking() throws Exception {
        // Initialize the database
        trackingRepository.saveAndFlush(tracking);
        trackingSearchRepository.save(tracking);
        int databaseSizeBeforeUpdate = trackingRepository.findAll().size();

        // Update the tracking
        Tracking updatedTracking = trackingRepository.findOne(tracking.getId());
        updatedTracking
                .mailDate(UPDATED_MAIL_DATE)
                .openType(UPDATED_OPEN_TYPE)
                .openCount(UPDATED_OPEN_COUNT);
        TrackingDTO trackingDTO = trackingMapper.trackingToTrackingDTO(updatedTracking);

        restTrackingMockMvc.perform(put("/api/trackings")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(trackingDTO)))
                .andExpect(status().isOk());

        // Validate the Tracking in the database
        List<Tracking> trackings = trackingRepository.findAll();
        assertThat(trackings).hasSize(databaseSizeBeforeUpdate);
        Tracking testTracking = trackings.get(trackings.size() - 1);
        assertThat(testTracking.getMailDate()).isEqualTo(UPDATED_MAIL_DATE);
        assertThat(testTracking.getOpenType()).isEqualTo(UPDATED_OPEN_TYPE);
        assertThat(testTracking.getOpenCount()).isEqualTo(UPDATED_OPEN_COUNT);

        // Validate the Tracking in ElasticSearch
        Tracking trackingEs = trackingSearchRepository.findOne(testTracking.getId());
        assertThat(trackingEs).isEqualToComparingFieldByField(testTracking);
    }

    @Test
    @Transactional
    public void deleteTracking() throws Exception {
        // Initialize the database
        trackingRepository.saveAndFlush(tracking);
        trackingSearchRepository.save(tracking);
        int databaseSizeBeforeDelete = trackingRepository.findAll().size();

        // Get the tracking
        restTrackingMockMvc.perform(delete("/api/trackings/{id}", tracking.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean trackingExistsInEs = trackingSearchRepository.exists(tracking.getId());
        assertThat(trackingExistsInEs).isFalse();

        // Validate the database is empty
        List<Tracking> trackings = trackingRepository.findAll();
        assertThat(trackings).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTracking() throws Exception {
        // Initialize the database
        trackingRepository.saveAndFlush(tracking);
        trackingSearchRepository.save(tracking);

        // Search the tracking
        restTrackingMockMvc.perform(get("/api/_search/trackings?query=id:" + tracking.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tracking.getId().intValue())))
            .andExpect(jsonPath("$.[*].mailDate").value(hasItem(DEFAULT_MAIL_DATE.toString())))
            .andExpect(jsonPath("$.[*].openType").value(hasItem(DEFAULT_OPEN_TYPE.toString())))
            .andExpect(jsonPath("$.[*].openCount").value(hasItem(DEFAULT_OPEN_COUNT)));
    }
}
