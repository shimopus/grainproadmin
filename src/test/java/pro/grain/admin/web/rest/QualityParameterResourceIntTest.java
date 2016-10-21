package pro.grain.admin.web.rest;

import pro.grain.admin.GrainAdminApp;

import pro.grain.admin.domain.QualityParameter;
import pro.grain.admin.repository.QualityParameterRepository;
import pro.grain.admin.service.QualityParameterService;
import pro.grain.admin.repository.search.QualityParameterSearchRepository;
import pro.grain.admin.service.dto.QualityParameterDTO;
import pro.grain.admin.service.mapper.QualityParameterMapper;

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
 * Test class for the QualityParameterResource REST controller.
 *
 * @see QualityParameterResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GrainAdminApp.class)
public class QualityParameterResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    private static final String DEFAULT_AVAILABLE_VALUES = "AAAAA";
    private static final String UPDATED_AVAILABLE_VALUES = "BBBBB";

    private static final String DEFAULT_UNIT = "AAAAA";
    private static final String UPDATED_UNIT = "BBBBB";

    @Inject
    private QualityParameterRepository qualityParameterRepository;

    @Inject
    private QualityParameterMapper qualityParameterMapper;

    @Inject
    private QualityParameterService qualityParameterService;

    @Inject
    private QualityParameterSearchRepository qualityParameterSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restQualityParameterMockMvc;

    private QualityParameter qualityParameter;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        QualityParameterResource qualityParameterResource = new QualityParameterResource();
        ReflectionTestUtils.setField(qualityParameterResource, "qualityParameterService", qualityParameterService);
        this.restQualityParameterMockMvc = MockMvcBuilders.standaloneSetup(qualityParameterResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static QualityParameter createEntity(EntityManager em) {
        QualityParameter qualityParameter = new QualityParameter()
                .name(DEFAULT_NAME)
                .availableValues(DEFAULT_AVAILABLE_VALUES)
                .unit(DEFAULT_UNIT);
        return qualityParameter;
    }

    @Before
    public void initTest() {
        qualityParameterSearchRepository.deleteAll();
        qualityParameter = createEntity(em);
    }

    @Test
    @Transactional
    public void createQualityParameter() throws Exception {
        int databaseSizeBeforeCreate = qualityParameterRepository.findAll().size();

        // Create the QualityParameter
        QualityParameterDTO qualityParameterDTO = qualityParameterMapper.qualityParameterToQualityParameterDTO(qualityParameter);

        restQualityParameterMockMvc.perform(post("/api/quality-parameters")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(qualityParameterDTO)))
                .andExpect(status().isCreated());

        // Validate the QualityParameter in the database
        List<QualityParameter> qualityParameters = qualityParameterRepository.findAll();
        assertThat(qualityParameters).hasSize(databaseSizeBeforeCreate + 1);
        QualityParameter testQualityParameter = qualityParameters.get(qualityParameters.size() - 1);
        assertThat(testQualityParameter.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testQualityParameter.getAvailableValues()).isEqualTo(DEFAULT_AVAILABLE_VALUES);
        assertThat(testQualityParameter.getUnit()).isEqualTo(DEFAULT_UNIT);

        // Validate the QualityParameter in ElasticSearch
        QualityParameter qualityParameterEs = qualityParameterSearchRepository.findOne(testQualityParameter.getId());
        assertThat(qualityParameterEs).isEqualToComparingFieldByField(testQualityParameter);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = qualityParameterRepository.findAll().size();
        // set the field null
        qualityParameter.setName(null);

        // Create the QualityParameter, which fails.
        QualityParameterDTO qualityParameterDTO = qualityParameterMapper.qualityParameterToQualityParameterDTO(qualityParameter);

        restQualityParameterMockMvc.perform(post("/api/quality-parameters")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(qualityParameterDTO)))
                .andExpect(status().isBadRequest());

        List<QualityParameter> qualityParameters = qualityParameterRepository.findAll();
        assertThat(qualityParameters).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllQualityParameters() throws Exception {
        // Initialize the database
        qualityParameterRepository.saveAndFlush(qualityParameter);

        // Get all the qualityParameters
        restQualityParameterMockMvc.perform(get("/api/quality-parameters?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(qualityParameter.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].availableValues").value(hasItem(DEFAULT_AVAILABLE_VALUES.toString())))
                .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT.toString())));
    }

    @Test
    @Transactional
    public void getQualityParameter() throws Exception {
        // Initialize the database
        qualityParameterRepository.saveAndFlush(qualityParameter);

        // Get the qualityParameter
        restQualityParameterMockMvc.perform(get("/api/quality-parameters/{id}", qualityParameter.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(qualityParameter.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.availableValues").value(DEFAULT_AVAILABLE_VALUES.toString()))
            .andExpect(jsonPath("$.unit").value(DEFAULT_UNIT.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingQualityParameter() throws Exception {
        // Get the qualityParameter
        restQualityParameterMockMvc.perform(get("/api/quality-parameters/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateQualityParameter() throws Exception {
        // Initialize the database
        qualityParameterRepository.saveAndFlush(qualityParameter);
        qualityParameterSearchRepository.save(qualityParameter);
        int databaseSizeBeforeUpdate = qualityParameterRepository.findAll().size();

        // Update the qualityParameter
        QualityParameter updatedQualityParameter = qualityParameterRepository.findOne(qualityParameter.getId());
        updatedQualityParameter
                .name(UPDATED_NAME)
                .availableValues(UPDATED_AVAILABLE_VALUES)
                .unit(UPDATED_UNIT);
        QualityParameterDTO qualityParameterDTO = qualityParameterMapper.qualityParameterToQualityParameterDTO(updatedQualityParameter);

        restQualityParameterMockMvc.perform(put("/api/quality-parameters")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(qualityParameterDTO)))
                .andExpect(status().isOk());

        // Validate the QualityParameter in the database
        List<QualityParameter> qualityParameters = qualityParameterRepository.findAll();
        assertThat(qualityParameters).hasSize(databaseSizeBeforeUpdate);
        QualityParameter testQualityParameter = qualityParameters.get(qualityParameters.size() - 1);
        assertThat(testQualityParameter.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testQualityParameter.getAvailableValues()).isEqualTo(UPDATED_AVAILABLE_VALUES);
        assertThat(testQualityParameter.getUnit()).isEqualTo(UPDATED_UNIT);

        // Validate the QualityParameter in ElasticSearch
        QualityParameter qualityParameterEs = qualityParameterSearchRepository.findOne(testQualityParameter.getId());
        assertThat(qualityParameterEs).isEqualToComparingFieldByField(testQualityParameter);
    }

    @Test
    @Transactional
    public void deleteQualityParameter() throws Exception {
        // Initialize the database
        qualityParameterRepository.saveAndFlush(qualityParameter);
        qualityParameterSearchRepository.save(qualityParameter);
        int databaseSizeBeforeDelete = qualityParameterRepository.findAll().size();

        // Get the qualityParameter
        restQualityParameterMockMvc.perform(delete("/api/quality-parameters/{id}", qualityParameter.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean qualityParameterExistsInEs = qualityParameterSearchRepository.exists(qualityParameter.getId());
        assertThat(qualityParameterExistsInEs).isFalse();

        // Validate the database is empty
        List<QualityParameter> qualityParameters = qualityParameterRepository.findAll();
        assertThat(qualityParameters).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchQualityParameter() throws Exception {
        // Initialize the database
        qualityParameterRepository.saveAndFlush(qualityParameter);
        qualityParameterSearchRepository.save(qualityParameter);

        // Search the qualityParameter
        restQualityParameterMockMvc.perform(get("/api/_search/quality-parameters?query=id:" + qualityParameter.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(qualityParameter.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].availableValues").value(hasItem(DEFAULT_AVAILABLE_VALUES.toString())))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT.toString())));
    }
}
