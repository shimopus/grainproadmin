package pro.grain.admin.web.rest;

import pro.grain.admin.GrainAdminApp;

import pro.grain.admin.domain.District;
import pro.grain.admin.repository.DistrictRepository;
import pro.grain.admin.service.DistrictService;
import pro.grain.admin.repository.search.DistrictSearchRepository;
import pro.grain.admin.service.dto.DistrictDTO;
import pro.grain.admin.service.mapper.DistrictMapper;

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
 * Test class for the DistrictResource REST controller.
 *
 * @see DistrictResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GrainAdminApp.class)
public class DistrictResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    @Inject
    private DistrictRepository districtRepository;

    @Inject
    private DistrictMapper districtMapper;

    @Inject
    private DistrictService districtService;

    @Inject
    private DistrictSearchRepository districtSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restDistrictMockMvc;

    private District district;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DistrictResource districtResource = new DistrictResource();
        ReflectionTestUtils.setField(districtResource, "districtService", districtService);
        this.restDistrictMockMvc = MockMvcBuilders.standaloneSetup(districtResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static District createEntity(EntityManager em) {
        District district = new District()
                .name(DEFAULT_NAME);
        return district;
    }

    @Before
    public void initTest() {
        districtSearchRepository.deleteAll();
        district = createEntity(em);
    }

    @Test
    @Transactional
    public void createDistrict() throws Exception {
        int databaseSizeBeforeCreate = districtRepository.findAll().size();

        // Create the District
        DistrictDTO districtDTO = districtMapper.districtToDistrictDTO(district);

        restDistrictMockMvc.perform(post("/api/districts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(districtDTO)))
                .andExpect(status().isCreated());

        // Validate the District in the database
        List<District> districts = districtRepository.findAll();
        assertThat(districts).hasSize(databaseSizeBeforeCreate + 1);
        District testDistrict = districts.get(districts.size() - 1);
        assertThat(testDistrict.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the District in ElasticSearch
        District districtEs = districtSearchRepository.findOne(testDistrict.getId());
        assertThat(districtEs).isEqualToComparingFieldByField(testDistrict);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = districtRepository.findAll().size();
        // set the field null
        district.setName(null);

        // Create the District, which fails.
        DistrictDTO districtDTO = districtMapper.districtToDistrictDTO(district);

        restDistrictMockMvc.perform(post("/api/districts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(districtDTO)))
                .andExpect(status().isBadRequest());

        List<District> districts = districtRepository.findAll();
        assertThat(districts).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllDistricts() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        // Get all the districts
        restDistrictMockMvc.perform(get("/api/districts?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(district.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getDistrict() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        // Get the district
        restDistrictMockMvc.perform(get("/api/districts/{id}", district.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(district.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingDistrict() throws Exception {
        // Get the district
        restDistrictMockMvc.perform(get("/api/districts/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDistrict() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);
        districtSearchRepository.save(district);
        int databaseSizeBeforeUpdate = districtRepository.findAll().size();

        // Update the district
        District updatedDistrict = districtRepository.findOne(district.getId());
        updatedDistrict
                .name(UPDATED_NAME);
        DistrictDTO districtDTO = districtMapper.districtToDistrictDTO(updatedDistrict);

        restDistrictMockMvc.perform(put("/api/districts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(districtDTO)))
                .andExpect(status().isOk());

        // Validate the District in the database
        List<District> districts = districtRepository.findAll();
        assertThat(districts).hasSize(databaseSizeBeforeUpdate);
        District testDistrict = districts.get(districts.size() - 1);
        assertThat(testDistrict.getName()).isEqualTo(UPDATED_NAME);

        // Validate the District in ElasticSearch
        District districtEs = districtSearchRepository.findOne(testDistrict.getId());
        assertThat(districtEs).isEqualToComparingFieldByField(testDistrict);
    }

    @Test
    @Transactional
    public void deleteDistrict() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);
        districtSearchRepository.save(district);
        int databaseSizeBeforeDelete = districtRepository.findAll().size();

        // Get the district
        restDistrictMockMvc.perform(delete("/api/districts/{id}", district.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean districtExistsInEs = districtSearchRepository.exists(district.getId());
        assertThat(districtExistsInEs).isFalse();

        // Validate the database is empty
        List<District> districts = districtRepository.findAll();
        assertThat(districts).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchDistrict() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);
        districtSearchRepository.save(district);

        // Search the district
        restDistrictMockMvc.perform(get("/api/_search/districts?query=" + district.getName()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(district.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
}
