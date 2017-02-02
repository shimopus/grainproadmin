package pro.grain.admin.web.rest;

import pro.grain.admin.GrainAdminApp;

import pro.grain.admin.domain.PriceUpdateQueue;
import pro.grain.admin.domain.Station;
import pro.grain.admin.domain.Station;
import pro.grain.admin.repository.PriceUpdateQueueRepository;
import pro.grain.admin.service.PriceUpdateQueueService;
import pro.grain.admin.repository.search.PriceUpdateQueueSearchRepository;
import pro.grain.admin.service.dto.PriceUpdateQueueDTO;
import pro.grain.admin.service.mapper.PriceUpdateQueueMapper;

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
 * Test class for the PriceUpdateQueueResource REST controller.
 *
 * @see PriceUpdateQueueResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GrainAdminApp.class)
public class PriceUpdateQueueResourceIntTest {

    private static final Boolean DEFAULT_LOADED = false;
    private static final Boolean UPDATED_LOADED = true;

    private static final Long DEFAULT_LOADING_ORDER = 1L;
    private static final Long UPDATED_LOADING_ORDER = 2L;

    @Inject
    private PriceUpdateQueueRepository priceUpdateQueueRepository;

    @Inject
    private PriceUpdateQueueMapper priceUpdateQueueMapper;

    @Inject
    private PriceUpdateQueueService priceUpdateQueueService;

    @Inject
    private PriceUpdateQueueSearchRepository priceUpdateQueueSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restPriceUpdateQueueMockMvc;

    private PriceUpdateQueue priceUpdateQueue;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PriceUpdateQueueResource priceUpdateQueueResource = new PriceUpdateQueueResource();
        ReflectionTestUtils.setField(priceUpdateQueueResource, "priceUpdateQueueService", priceUpdateQueueService);
        this.restPriceUpdateQueueMockMvc = MockMvcBuilders.standaloneSetup(priceUpdateQueueResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PriceUpdateQueue createEntity(EntityManager em) {
        PriceUpdateQueue priceUpdateQueue = new PriceUpdateQueue()
                .loaded(DEFAULT_LOADED)
                .loadingOrder(DEFAULT_LOADING_ORDER);
        // Add required entity
        Station stationFrom = StationResourceIntTest.createEntity(em);
        em.persist(stationFrom);
        em.flush();
        priceUpdateQueue.setStationFrom(stationFrom);
        // Add required entity
        Station stationTo = StationResourceIntTest.createEntity(em, false);
        em.persist(stationTo);
        em.flush();
        priceUpdateQueue.setStationTo(stationTo);
        return priceUpdateQueue;
    }

    @Before
    public void initTest() {
        priceUpdateQueueSearchRepository.deleteAll();
        priceUpdateQueue = createEntity(em);
    }

    @Test
    @Transactional
    public void createPriceUpdateQueue() throws Exception {
        int databaseSizeBeforeCreate = priceUpdateQueueRepository.findAll().size();

        // Create the PriceUpdateQueue
        PriceUpdateQueueDTO priceUpdateQueueDTO = priceUpdateQueueMapper.priceUpdateQueueToPriceUpdateQueueDTO(priceUpdateQueue);

        restPriceUpdateQueueMockMvc.perform(post("/api/price-update-queues")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(priceUpdateQueueDTO)))
                .andExpect(status().isCreated());

        // Validate the PriceUpdateQueue in the database
        List<PriceUpdateQueue> priceUpdateQueues = priceUpdateQueueRepository.findAll();
        assertThat(priceUpdateQueues).hasSize(databaseSizeBeforeCreate + 1);
        PriceUpdateQueue testPriceUpdateQueue = priceUpdateQueues.get(priceUpdateQueues.size() - 1);
        assertThat(testPriceUpdateQueue.isLoaded()).isEqualTo(DEFAULT_LOADED);
        assertThat(testPriceUpdateQueue.getLoadingOrder()).isEqualTo(DEFAULT_LOADING_ORDER);

        // Validate the PriceUpdateQueue in ElasticSearch
        PriceUpdateQueue priceUpdateQueueEs = priceUpdateQueueSearchRepository.findOne(testPriceUpdateQueue.getId());
        assertThat(priceUpdateQueueEs).isEqualToComparingFieldByField(testPriceUpdateQueue);
    }

    @Test
    @Transactional
    public void checkLoadedIsRequired() throws Exception {
        int databaseSizeBeforeTest = priceUpdateQueueRepository.findAll().size();
        // set the field null
        priceUpdateQueue.setLoaded(null);

        // Create the PriceUpdateQueue, which fails.
        PriceUpdateQueueDTO priceUpdateQueueDTO = priceUpdateQueueMapper.priceUpdateQueueToPriceUpdateQueueDTO(priceUpdateQueue);

        restPriceUpdateQueueMockMvc.perform(post("/api/price-update-queues")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(priceUpdateQueueDTO)))
                .andExpect(status().isBadRequest());

        List<PriceUpdateQueue> priceUpdateQueues = priceUpdateQueueRepository.findAll();
        assertThat(priceUpdateQueues).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPriceUpdateQueues() throws Exception {
        // Initialize the database
        priceUpdateQueueRepository.saveAndFlush(priceUpdateQueue);

        // Get all the priceUpdateQueues
        restPriceUpdateQueueMockMvc.perform(get("/api/price-update-queues?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(priceUpdateQueue.getId().intValue())))
                .andExpect(jsonPath("$.[*].loaded").value(hasItem(DEFAULT_LOADED.booleanValue())))
                .andExpect(jsonPath("$.[*].loadingOrder").value(hasItem(DEFAULT_LOADING_ORDER.intValue())));
    }

    @Test
    @Transactional
    public void getPriceUpdateQueue() throws Exception {
        // Initialize the database
        priceUpdateQueueRepository.saveAndFlush(priceUpdateQueue);

        // Get the priceUpdateQueue
        restPriceUpdateQueueMockMvc.perform(get("/api/price-update-queues/{id}", priceUpdateQueue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(priceUpdateQueue.getId().intValue()))
            .andExpect(jsonPath("$.loaded").value(DEFAULT_LOADED.booleanValue()))
            .andExpect(jsonPath("$.loadingOrder").value(DEFAULT_LOADING_ORDER.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingPriceUpdateQueue() throws Exception {
        // Get the priceUpdateQueue
        restPriceUpdateQueueMockMvc.perform(get("/api/price-update-queues/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePriceUpdateQueue() throws Exception {
        // Initialize the database
        priceUpdateQueueRepository.saveAndFlush(priceUpdateQueue);
        priceUpdateQueueSearchRepository.save(priceUpdateQueue);
        int databaseSizeBeforeUpdate = priceUpdateQueueRepository.findAll().size();

        // Update the priceUpdateQueue
        PriceUpdateQueue updatedPriceUpdateQueue = priceUpdateQueueRepository.findOne(priceUpdateQueue.getId());
        updatedPriceUpdateQueue
                .loaded(UPDATED_LOADED)
                .loadingOrder(UPDATED_LOADING_ORDER);
        PriceUpdateQueueDTO priceUpdateQueueDTO = priceUpdateQueueMapper.priceUpdateQueueToPriceUpdateQueueDTO(updatedPriceUpdateQueue);

        restPriceUpdateQueueMockMvc.perform(put("/api/price-update-queues")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(priceUpdateQueueDTO)))
                .andExpect(status().isOk());

        // Validate the PriceUpdateQueue in the database
        List<PriceUpdateQueue> priceUpdateQueues = priceUpdateQueueRepository.findAll();
        assertThat(priceUpdateQueues).hasSize(databaseSizeBeforeUpdate);
        PriceUpdateQueue testPriceUpdateQueue = priceUpdateQueues.get(priceUpdateQueues.size() - 1);
        assertThat(testPriceUpdateQueue.isLoaded()).isEqualTo(UPDATED_LOADED);
        assertThat(testPriceUpdateQueue.getLoadingOrder()).isEqualTo(UPDATED_LOADING_ORDER);

        // Validate the PriceUpdateQueue in ElasticSearch
        PriceUpdateQueue priceUpdateQueueEs = priceUpdateQueueSearchRepository.findOne(testPriceUpdateQueue.getId());
        assertThat(priceUpdateQueueEs).isEqualToComparingFieldByField(testPriceUpdateQueue);
    }

    @Test
    @Transactional
    public void deletePriceUpdateQueue() throws Exception {
        // Initialize the database
        priceUpdateQueueRepository.saveAndFlush(priceUpdateQueue);
        priceUpdateQueueSearchRepository.save(priceUpdateQueue);
        int databaseSizeBeforeDelete = priceUpdateQueueRepository.findAll().size();

        // Get the priceUpdateQueue
        restPriceUpdateQueueMockMvc.perform(delete("/api/price-update-queues/{id}", priceUpdateQueue.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean priceUpdateQueueExistsInEs = priceUpdateQueueSearchRepository.exists(priceUpdateQueue.getId());
        assertThat(priceUpdateQueueExistsInEs).isFalse();

        // Validate the database is empty
        List<PriceUpdateQueue> priceUpdateQueues = priceUpdateQueueRepository.findAll();
        assertThat(priceUpdateQueues).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPriceUpdateQueue() throws Exception {
        // Initialize the database
        priceUpdateQueueRepository.saveAndFlush(priceUpdateQueue);
        priceUpdateQueueSearchRepository.save(priceUpdateQueue);

        // Search the priceUpdateQueue
        restPriceUpdateQueueMockMvc.perform(get("/api/_search/price-update-queues?query=id:" + priceUpdateQueue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(priceUpdateQueue.getId().intValue())))
            .andExpect(jsonPath("$.[*].loaded").value(hasItem(DEFAULT_LOADED.booleanValue())))
            .andExpect(jsonPath("$.[*].loadingOrder").value(hasItem(DEFAULT_LOADING_ORDER.intValue())));
    }
}
