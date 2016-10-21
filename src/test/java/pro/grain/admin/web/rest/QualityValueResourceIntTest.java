package pro.grain.admin.web.rest;

import pro.grain.admin.GrainAdminApp;

import pro.grain.admin.domain.QualityValue;
import pro.grain.admin.repository.QualityValueRepository;
import pro.grain.admin.service.QualityValueService;
import pro.grain.admin.repository.search.QualityValueSearchRepository;
import pro.grain.admin.service.dto.QualityValueDTO;
import pro.grain.admin.service.mapper.QualityValueMapper;

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
 * Test class for the QualityValueResource REST controller.
 *
 * @see QualityValueResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GrainAdminApp.class)
public class QualityValueResourceIntTest {

    private static final Long DEFAULT_VALUE = 1L;
    private static final Long UPDATED_VALUE = 2L;

    @Inject
    private QualityValueRepository qualityValueRepository;

    @Inject
    private QualityValueMapper qualityValueMapper;

    @Inject
    private QualityValueService qualityValueService;

    @Inject
    private QualityValueSearchRepository qualityValueSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restQualityValueMockMvc;

    private QualityValue qualityValue;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        QualityValueResource qualityValueResource = new QualityValueResource();
        ReflectionTestUtils.setField(qualityValueResource, "qualityValueService", qualityValueService);
        this.restQualityValueMockMvc = MockMvcBuilders.standaloneSetup(qualityValueResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static QualityValue createEntity(EntityManager em) {
        QualityValue qualityValue = new QualityValue()
                .value(DEFAULT_VALUE);
        return qualityValue;
    }

    @Before
    public void initTest() {
        qualityValueSearchRepository.deleteAll();
        qualityValue = createEntity(em);
    }

    @Test
    @Transactional
    public void createQualityValue() throws Exception {
        int databaseSizeBeforeCreate = qualityValueRepository.findAll().size();

        // Create the QualityValue
        QualityValueDTO qualityValueDTO = qualityValueMapper.qualityValueToQualityValueDTO(qualityValue);

        restQualityValueMockMvc.perform(post("/api/quality-values")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(qualityValueDTO)))
                .andExpect(status().isCreated());

        // Validate the QualityValue in the database
        List<QualityValue> qualityValues = qualityValueRepository.findAll();
        assertThat(qualityValues).hasSize(databaseSizeBeforeCreate + 1);
        QualityValue testQualityValue = qualityValues.get(qualityValues.size() - 1);
        assertThat(testQualityValue.getValue()).isEqualTo(DEFAULT_VALUE);

        // Validate the QualityValue in ElasticSearch
        QualityValue qualityValueEs = qualityValueSearchRepository.findOne(testQualityValue.getId());
        assertThat(qualityValueEs).isEqualToComparingFieldByField(testQualityValue);
    }

    @Test
    @Transactional
    public void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = qualityValueRepository.findAll().size();
        // set the field null
        qualityValue.setValue(null);

        // Create the QualityValue, which fails.
        QualityValueDTO qualityValueDTO = qualityValueMapper.qualityValueToQualityValueDTO(qualityValue);

        restQualityValueMockMvc.perform(post("/api/quality-values")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(qualityValueDTO)))
                .andExpect(status().isBadRequest());

        List<QualityValue> qualityValues = qualityValueRepository.findAll();
        assertThat(qualityValues).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllQualityValues() throws Exception {
        // Initialize the database
        qualityValueRepository.saveAndFlush(qualityValue);

        // Get all the qualityValues
        restQualityValueMockMvc.perform(get("/api/quality-values?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(qualityValue.getId().intValue())))
                .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.intValue())));
    }

    @Test
    @Transactional
    public void getQualityValue() throws Exception {
        // Initialize the database
        qualityValueRepository.saveAndFlush(qualityValue);

        // Get the qualityValue
        restQualityValueMockMvc.perform(get("/api/quality-values/{id}", qualityValue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(qualityValue.getId().intValue()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingQualityValue() throws Exception {
        // Get the qualityValue
        restQualityValueMockMvc.perform(get("/api/quality-values/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateQualityValue() throws Exception {
        // Initialize the database
        qualityValueRepository.saveAndFlush(qualityValue);
        qualityValueSearchRepository.save(qualityValue);
        int databaseSizeBeforeUpdate = qualityValueRepository.findAll().size();

        // Update the qualityValue
        QualityValue updatedQualityValue = qualityValueRepository.findOne(qualityValue.getId());
        updatedQualityValue
                .value(UPDATED_VALUE);
        QualityValueDTO qualityValueDTO = qualityValueMapper.qualityValueToQualityValueDTO(updatedQualityValue);

        restQualityValueMockMvc.perform(put("/api/quality-values")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(qualityValueDTO)))
                .andExpect(status().isOk());

        // Validate the QualityValue in the database
        List<QualityValue> qualityValues = qualityValueRepository.findAll();
        assertThat(qualityValues).hasSize(databaseSizeBeforeUpdate);
        QualityValue testQualityValue = qualityValues.get(qualityValues.size() - 1);
        assertThat(testQualityValue.getValue()).isEqualTo(UPDATED_VALUE);

        // Validate the QualityValue in ElasticSearch
        QualityValue qualityValueEs = qualityValueSearchRepository.findOne(testQualityValue.getId());
        assertThat(qualityValueEs).isEqualToComparingFieldByField(testQualityValue);
    }

    @Test
    @Transactional
    public void deleteQualityValue() throws Exception {
        // Initialize the database
        qualityValueRepository.saveAndFlush(qualityValue);
        qualityValueSearchRepository.save(qualityValue);
        int databaseSizeBeforeDelete = qualityValueRepository.findAll().size();

        // Get the qualityValue
        restQualityValueMockMvc.perform(delete("/api/quality-values/{id}", qualityValue.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean qualityValueExistsInEs = qualityValueSearchRepository.exists(qualityValue.getId());
        assertThat(qualityValueExistsInEs).isFalse();

        // Validate the database is empty
        List<QualityValue> qualityValues = qualityValueRepository.findAll();
        assertThat(qualityValues).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchQualityValue() throws Exception {
        // Initialize the database
        qualityValueRepository.saveAndFlush(qualityValue);
        qualityValueSearchRepository.save(qualityValue);

        // Search the qualityValue
        restQualityValueMockMvc.perform(get("/api/_search/quality-values?query=id:" + qualityValue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(qualityValue.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.intValue())));
    }
}
