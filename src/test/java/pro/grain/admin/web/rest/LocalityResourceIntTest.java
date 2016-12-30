package pro.grain.admin.web.rest;

import pro.grain.admin.GrainAdminApp;

import pro.grain.admin.domain.Locality;
import pro.grain.admin.repository.LocalityRepository;
import pro.grain.admin.service.LocalityService;
import pro.grain.admin.repository.search.LocalitySearchRepository;
import pro.grain.admin.service.dto.LocalityDTO;
import pro.grain.admin.service.mapper.LocalityMapper;

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
 * Test class for the LocalityResource REST controller.
 *
 * @see LocalityResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GrainAdminApp.class)
public class LocalityResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    @Inject
    private LocalityRepository localityRepository;

    @Inject
    private LocalityMapper localityMapper;

    @Inject
    private LocalityService localityService;

    @Inject
    private LocalitySearchRepository localitySearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restLocalityMockMvc;

    private Locality locality;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        LocalityResource localityResource = new LocalityResource();
        ReflectionTestUtils.setField(localityResource, "localityService", localityService);
        this.restLocalityMockMvc = MockMvcBuilders.standaloneSetup(localityResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Locality createEntity(EntityManager em) {
        Locality locality = new Locality()
                .name(DEFAULT_NAME);
        return locality;
    }

    @Before
    public void initTest() {
        localitySearchRepository.deleteAll();
        locality = createEntity(em);
    }

    @Test
    @Transactional
    public void createLocality() throws Exception {
        int databaseSizeBeforeCreate = localityRepository.findAll().size();

        // Create the Locality
        LocalityDTO localityDTO = localityMapper.localityToLocalityDTO(locality);

        restLocalityMockMvc.perform(post("/api/localities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(localityDTO)))
                .andExpect(status().isCreated());

        // Validate the Locality in the database
        List<Locality> localities = localityRepository.findAll();
        assertThat(localities).hasSize(databaseSizeBeforeCreate + 1);
        Locality testLocality = localities.get(localities.size() - 1);
        assertThat(testLocality.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the Locality in ElasticSearch
        Locality localityEs = localitySearchRepository.findOne(testLocality.getId());
        assertThat(localityEs).isEqualToComparingFieldByField(testLocality);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = localityRepository.findAll().size();
        // set the field null
        locality.setName(null);

        // Create the Locality, which fails.
        LocalityDTO localityDTO = localityMapper.localityToLocalityDTO(locality);

        restLocalityMockMvc.perform(post("/api/localities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(localityDTO)))
                .andExpect(status().isBadRequest());

        List<Locality> localities = localityRepository.findAll();
        assertThat(localities).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllLocalities() throws Exception {
        // Initialize the database
        localityRepository.saveAndFlush(locality);

        // Get all the localities
        restLocalityMockMvc.perform(get("/api/localities?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(locality.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getLocality() throws Exception {
        // Initialize the database
        localityRepository.saveAndFlush(locality);

        // Get the locality
        restLocalityMockMvc.perform(get("/api/localities/{id}", locality.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(locality.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingLocality() throws Exception {
        // Get the locality
        restLocalityMockMvc.perform(get("/api/localities/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLocality() throws Exception {
        // Initialize the database
        localityRepository.saveAndFlush(locality);
        localitySearchRepository.save(locality);
        int databaseSizeBeforeUpdate = localityRepository.findAll().size();

        // Update the locality
        Locality updatedLocality = localityRepository.findOne(locality.getId());
        updatedLocality
                .name(UPDATED_NAME);
        LocalityDTO localityDTO = localityMapper.localityToLocalityDTO(updatedLocality);

        restLocalityMockMvc.perform(put("/api/localities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(localityDTO)))
                .andExpect(status().isOk());

        // Validate the Locality in the database
        List<Locality> localities = localityRepository.findAll();
        assertThat(localities).hasSize(databaseSizeBeforeUpdate);
        Locality testLocality = localities.get(localities.size() - 1);
        assertThat(testLocality.getName()).isEqualTo(UPDATED_NAME);

        // Validate the Locality in ElasticSearch
        Locality localityEs = localitySearchRepository.findOne(testLocality.getId());
        assertThat(localityEs).isEqualToComparingFieldByField(testLocality);
    }

    @Test
    @Transactional
    public void deleteLocality() throws Exception {
        // Initialize the database
        localityRepository.saveAndFlush(locality);
        localitySearchRepository.save(locality);
        int databaseSizeBeforeDelete = localityRepository.findAll().size();

        // Get the locality
        restLocalityMockMvc.perform(delete("/api/localities/{id}", locality.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean localityExistsInEs = localitySearchRepository.exists(locality.getId());
        assertThat(localityExistsInEs).isFalse();

        // Validate the database is empty
        List<Locality> localities = localityRepository.findAll();
        assertThat(localities).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchLocality() throws Exception {
        // Initialize the database
        localityRepository.saveAndFlush(locality);
        localitySearchRepository.save(locality);

        // Search the locality
        restLocalityMockMvc.perform(get("/api/_search/localities?query=" + locality.getName()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(locality.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
}
