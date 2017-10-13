package pro.grain.admin.web.rest;

import pro.grain.admin.GrainAdminApp;

import pro.grain.admin.domain.SubscriptionConfig;
import pro.grain.admin.repository.SubscriptionConfigRepository;
import pro.grain.admin.service.SubscriptionConfigService;
import pro.grain.admin.repository.search.SubscriptionConfigSearchRepository;
import pro.grain.admin.service.dto.SubscriptionConfigDTO;
import pro.grain.admin.service.mapper.SubscriptionConfigMapper;

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
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import pro.grain.admin.domain.enumeration.SubscriptionType;
/**
 * Test class for the SubscriptionConfigResource REST controller.
 *
 * @see SubscriptionConfigResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GrainAdminApp.class)
public class SubscriptionConfigResourceIntTest {

    private static final SubscriptionType DEFAULT_SUBSCRIPTION_TYPE = SubscriptionType.SELL;
    private static final SubscriptionType UPDATED_SUBSCRIPTION_TYPE = SubscriptionType.BUY;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final ZonedDateTime DEFAULT_CREATION_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_CREATION_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_CREATION_DATE_STR = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(DEFAULT_CREATION_DATE);

    private static final ZonedDateTime DEFAULT_LAST_UPDATE_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_LAST_UPDATE_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_LAST_UPDATE_DATE_STR = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(DEFAULT_LAST_UPDATE_DATE);

    @Inject
    private SubscriptionConfigRepository subscriptionConfigRepository;

    @Inject
    private SubscriptionConfigMapper subscriptionConfigMapper;

    @Inject
    private SubscriptionConfigService subscriptionConfigService;

    @Inject
    private SubscriptionConfigSearchRepository subscriptionConfigSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restSubscriptionConfigMockMvc;

    private SubscriptionConfig subscriptionConfig;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SubscriptionConfigResource subscriptionConfigResource = new SubscriptionConfigResource();
        ReflectionTestUtils.setField(subscriptionConfigResource, "subscriptionConfigService", subscriptionConfigService);
        this.restSubscriptionConfigMockMvc = MockMvcBuilders.standaloneSetup(subscriptionConfigResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SubscriptionConfig createEntity(EntityManager em) {
        SubscriptionConfig subscriptionConfig = new SubscriptionConfig()
                .subscriptionType(DEFAULT_SUBSCRIPTION_TYPE)
                .isActive(DEFAULT_IS_ACTIVE)
                .creationDate(DEFAULT_CREATION_DATE)
                .lastUpdateDate(DEFAULT_LAST_UPDATE_DATE);
        return subscriptionConfig;
    }

    @Before
    public void initTest() {
        subscriptionConfigSearchRepository.deleteAll();
        subscriptionConfig = createEntity(em);
    }

    @Test
    @Transactional
    public void createSubscriptionConfig() throws Exception {
        int databaseSizeBeforeCreate = subscriptionConfigRepository.findAll().size();

        // Create the SubscriptionConfig
        SubscriptionConfigDTO subscriptionConfigDTO = subscriptionConfigMapper.subscriptionConfigToSubscriptionConfigDTO(subscriptionConfig);

        restSubscriptionConfigMockMvc.perform(post("/api/subscription-configs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(subscriptionConfigDTO)))
                .andExpect(status().isCreated());

        // Validate the SubscriptionConfig in the database
        List<SubscriptionConfig> subscriptionConfigs = subscriptionConfigRepository.findAll();
        assertThat(subscriptionConfigs).hasSize(databaseSizeBeforeCreate + 1);
        SubscriptionConfig testSubscriptionConfig = subscriptionConfigs.get(subscriptionConfigs.size() - 1);
        assertThat(testSubscriptionConfig.getSubscriptionType()).isEqualTo(DEFAULT_SUBSCRIPTION_TYPE);
        assertThat(testSubscriptionConfig.isIsActive()).isEqualTo(DEFAULT_IS_ACTIVE);
        assertThat(testSubscriptionConfig.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testSubscriptionConfig.getLastUpdateDate()).isEqualTo(DEFAULT_LAST_UPDATE_DATE);

        // Validate the SubscriptionConfig in ElasticSearch
        SubscriptionConfig subscriptionConfigEs = subscriptionConfigSearchRepository.findOne(testSubscriptionConfig.getId());
        assertThat(subscriptionConfigEs).isEqualToComparingFieldByField(testSubscriptionConfig);
    }

    @Test
    @Transactional
    public void getAllSubscriptionConfigs() throws Exception {
        // Initialize the database
        subscriptionConfigRepository.saveAndFlush(subscriptionConfig);

        // Get all the subscriptionConfigs
        restSubscriptionConfigMockMvc.perform(get("/api/subscription-configs?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(subscriptionConfig.getId().intValue())))
                .andExpect(jsonPath("$.[*].subscriptionType").value(hasItem(DEFAULT_SUBSCRIPTION_TYPE.toString())))
                .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())))
                .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE_STR)))
                .andExpect(jsonPath("$.[*].lastUpdateDate").value(hasItem(DEFAULT_LAST_UPDATE_DATE_STR)));
    }

    @Test
    @Transactional
    public void getSubscriptionConfig() throws Exception {
        // Initialize the database
        subscriptionConfigRepository.saveAndFlush(subscriptionConfig);

        // Get the subscriptionConfig
        restSubscriptionConfigMockMvc.perform(get("/api/subscription-configs/{id}", subscriptionConfig.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(subscriptionConfig.getId().intValue()))
            .andExpect(jsonPath("$.subscriptionType").value(DEFAULT_SUBSCRIPTION_TYPE.toString()))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE_STR))
            .andExpect(jsonPath("$.lastUpdateDate").value(DEFAULT_LAST_UPDATE_DATE_STR));
    }

    @Test
    @Transactional
    public void getNonExistingSubscriptionConfig() throws Exception {
        // Get the subscriptionConfig
        restSubscriptionConfigMockMvc.perform(get("/api/subscription-configs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSubscriptionConfig() throws Exception {
        // Initialize the database
        subscriptionConfigRepository.saveAndFlush(subscriptionConfig);
        subscriptionConfigSearchRepository.save(subscriptionConfig);
        int databaseSizeBeforeUpdate = subscriptionConfigRepository.findAll().size();

        // Update the subscriptionConfig
        SubscriptionConfig updatedSubscriptionConfig = subscriptionConfigRepository.findOne(subscriptionConfig.getId());
        updatedSubscriptionConfig
                .subscriptionType(UPDATED_SUBSCRIPTION_TYPE)
                .isActive(UPDATED_IS_ACTIVE)
                .creationDate(UPDATED_CREATION_DATE)
                .lastUpdateDate(UPDATED_LAST_UPDATE_DATE);
        SubscriptionConfigDTO subscriptionConfigDTO = subscriptionConfigMapper.subscriptionConfigToSubscriptionConfigDTO(updatedSubscriptionConfig);

        restSubscriptionConfigMockMvc.perform(put("/api/subscription-configs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(subscriptionConfigDTO)))
                .andExpect(status().isOk());

        // Validate the SubscriptionConfig in the database
        List<SubscriptionConfig> subscriptionConfigs = subscriptionConfigRepository.findAll();
        assertThat(subscriptionConfigs).hasSize(databaseSizeBeforeUpdate);
        SubscriptionConfig testSubscriptionConfig = subscriptionConfigs.get(subscriptionConfigs.size() - 1);
        assertThat(testSubscriptionConfig.getSubscriptionType()).isEqualTo(UPDATED_SUBSCRIPTION_TYPE);
        assertThat(testSubscriptionConfig.isIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
        assertThat(testSubscriptionConfig.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testSubscriptionConfig.getLastUpdateDate()).isEqualTo(UPDATED_LAST_UPDATE_DATE);

        // Validate the SubscriptionConfig in ElasticSearch
        SubscriptionConfig subscriptionConfigEs = subscriptionConfigSearchRepository.findOne(testSubscriptionConfig.getId());
        assertThat(subscriptionConfigEs).isEqualToComparingFieldByField(testSubscriptionConfig);
    }

    @Test
    @Transactional
    public void deleteSubscriptionConfig() throws Exception {
        // Initialize the database
        subscriptionConfigRepository.saveAndFlush(subscriptionConfig);
        subscriptionConfigSearchRepository.save(subscriptionConfig);
        int databaseSizeBeforeDelete = subscriptionConfigRepository.findAll().size();

        // Get the subscriptionConfig
        restSubscriptionConfigMockMvc.perform(delete("/api/subscription-configs/{id}", subscriptionConfig.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean subscriptionConfigExistsInEs = subscriptionConfigSearchRepository.exists(subscriptionConfig.getId());
        assertThat(subscriptionConfigExistsInEs).isFalse();

        // Validate the database is empty
        List<SubscriptionConfig> subscriptionConfigs = subscriptionConfigRepository.findAll();
        assertThat(subscriptionConfigs).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchSubscriptionConfig() throws Exception {
        // Initialize the database
        subscriptionConfigRepository.saveAndFlush(subscriptionConfig);
        subscriptionConfigSearchRepository.save(subscriptionConfig);

        // Search the subscriptionConfig
        restSubscriptionConfigMockMvc.perform(get("/api/_search/subscription-configs?query=id:" + subscriptionConfig.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subscriptionConfig.getId().intValue())))
            .andExpect(jsonPath("$.[*].subscriptionType").value(hasItem(DEFAULT_SUBSCRIPTION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())));
//            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE_STR)))
//            .andExpect(jsonPath("$.[*].lastUpdateDate").value(hasItem(DEFAULT_LAST_UPDATE_DATE_STR)));
    }
}
