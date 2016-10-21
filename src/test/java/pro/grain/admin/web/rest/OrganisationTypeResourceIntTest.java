package pro.grain.admin.web.rest;

import pro.grain.admin.GrainAdminApp;

import pro.grain.admin.domain.OrganisationType;
import pro.grain.admin.repository.OrganisationTypeRepository;
import pro.grain.admin.service.OrganisationTypeService;
import pro.grain.admin.repository.search.OrganisationTypeSearchRepository;
import pro.grain.admin.service.dto.OrganisationTypeDTO;
import pro.grain.admin.service.mapper.OrganisationTypeMapper;

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
 * Test class for the OrganisationTypeResource REST controller.
 *
 * @see OrganisationTypeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GrainAdminApp.class)
public class OrganisationTypeResourceIntTest {

    private static final String DEFAULT_TYPE = "AAAAA";
    private static final String UPDATED_TYPE = "BBBBB";

    @Inject
    private OrganisationTypeRepository organisationTypeRepository;

    @Inject
    private OrganisationTypeMapper organisationTypeMapper;

    @Inject
    private OrganisationTypeService organisationTypeService;

    @Inject
    private OrganisationTypeSearchRepository organisationTypeSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restOrganisationTypeMockMvc;

    private OrganisationType organisationType;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OrganisationTypeResource organisationTypeResource = new OrganisationTypeResource();
        ReflectionTestUtils.setField(organisationTypeResource, "organisationTypeService", organisationTypeService);
        this.restOrganisationTypeMockMvc = MockMvcBuilders.standaloneSetup(organisationTypeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrganisationType createEntity(EntityManager em) {
        OrganisationType organisationType = new OrganisationType()
                .type(DEFAULT_TYPE);
        return organisationType;
    }

    @Before
    public void initTest() {
        organisationTypeSearchRepository.deleteAll();
        organisationType = createEntity(em);
    }

    @Test
    @Transactional
    public void createOrganisationType() throws Exception {
        int databaseSizeBeforeCreate = organisationTypeRepository.findAll().size();

        // Create the OrganisationType
        OrganisationTypeDTO organisationTypeDTO = organisationTypeMapper.organisationTypeToOrganisationTypeDTO(organisationType);

        restOrganisationTypeMockMvc.perform(post("/api/organisation-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(organisationTypeDTO)))
                .andExpect(status().isCreated());

        // Validate the OrganisationType in the database
        List<OrganisationType> organisationTypes = organisationTypeRepository.findAll();
        assertThat(organisationTypes).hasSize(databaseSizeBeforeCreate + 1);
        OrganisationType testOrganisationType = organisationTypes.get(organisationTypes.size() - 1);
        assertThat(testOrganisationType.getType()).isEqualTo(DEFAULT_TYPE);

        // Validate the OrganisationType in ElasticSearch
        OrganisationType organisationTypeEs = organisationTypeSearchRepository.findOne(testOrganisationType.getId());
        assertThat(organisationTypeEs).isEqualToComparingFieldByField(testOrganisationType);
    }

    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = organisationTypeRepository.findAll().size();
        // set the field null
        organisationType.setType(null);

        // Create the OrganisationType, which fails.
        OrganisationTypeDTO organisationTypeDTO = organisationTypeMapper.organisationTypeToOrganisationTypeDTO(organisationType);

        restOrganisationTypeMockMvc.perform(post("/api/organisation-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(organisationTypeDTO)))
                .andExpect(status().isBadRequest());

        List<OrganisationType> organisationTypes = organisationTypeRepository.findAll();
        assertThat(organisationTypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllOrganisationTypes() throws Exception {
        // Initialize the database
        organisationTypeRepository.saveAndFlush(organisationType);

        // Get all the organisationTypes
        restOrganisationTypeMockMvc.perform(get("/api/organisation-types?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(organisationType.getId().intValue())))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    @Transactional
    public void getOrganisationType() throws Exception {
        // Initialize the database
        organisationTypeRepository.saveAndFlush(organisationType);

        // Get the organisationType
        restOrganisationTypeMockMvc.perform(get("/api/organisation-types/{id}", organisationType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(organisationType.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingOrganisationType() throws Exception {
        // Get the organisationType
        restOrganisationTypeMockMvc.perform(get("/api/organisation-types/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOrganisationType() throws Exception {
        // Initialize the database
        organisationTypeRepository.saveAndFlush(organisationType);
        organisationTypeSearchRepository.save(organisationType);
        int databaseSizeBeforeUpdate = organisationTypeRepository.findAll().size();

        // Update the organisationType
        OrganisationType updatedOrganisationType = organisationTypeRepository.findOne(organisationType.getId());
        updatedOrganisationType
                .type(UPDATED_TYPE);
        OrganisationTypeDTO organisationTypeDTO = organisationTypeMapper.organisationTypeToOrganisationTypeDTO(updatedOrganisationType);

        restOrganisationTypeMockMvc.perform(put("/api/organisation-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(organisationTypeDTO)))
                .andExpect(status().isOk());

        // Validate the OrganisationType in the database
        List<OrganisationType> organisationTypes = organisationTypeRepository.findAll();
        assertThat(organisationTypes).hasSize(databaseSizeBeforeUpdate);
        OrganisationType testOrganisationType = organisationTypes.get(organisationTypes.size() - 1);
        assertThat(testOrganisationType.getType()).isEqualTo(UPDATED_TYPE);

        // Validate the OrganisationType in ElasticSearch
        OrganisationType organisationTypeEs = organisationTypeSearchRepository.findOne(testOrganisationType.getId());
        assertThat(organisationTypeEs).isEqualToComparingFieldByField(testOrganisationType);
    }

    @Test
    @Transactional
    public void deleteOrganisationType() throws Exception {
        // Initialize the database
        organisationTypeRepository.saveAndFlush(organisationType);
        organisationTypeSearchRepository.save(organisationType);
        int databaseSizeBeforeDelete = organisationTypeRepository.findAll().size();

        // Get the organisationType
        restOrganisationTypeMockMvc.perform(delete("/api/organisation-types/{id}", organisationType.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean organisationTypeExistsInEs = organisationTypeSearchRepository.exists(organisationType.getId());
        assertThat(organisationTypeExistsInEs).isFalse();

        // Validate the database is empty
        List<OrganisationType> organisationTypes = organisationTypeRepository.findAll();
        assertThat(organisationTypes).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchOrganisationType() throws Exception {
        // Initialize the database
        organisationTypeRepository.saveAndFlush(organisationType);
        organisationTypeSearchRepository.save(organisationType);

        // Search the organisationType
        restOrganisationTypeMockMvc.perform(get("/api/_search/organisation-types?query=id:" + organisationType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(organisationType.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }
}
