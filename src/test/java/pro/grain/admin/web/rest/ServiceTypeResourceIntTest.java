package pro.grain.admin.web.rest;

import pro.grain.admin.GrainAdminApp;

import pro.grain.admin.domain.ServiceType;
import pro.grain.admin.repository.ServiceTypeRepository;
import pro.grain.admin.service.ServiceTypeService;
import pro.grain.admin.repository.search.ServiceTypeSearchRepository;
import pro.grain.admin.service.dto.ServiceTypeDTO;
import pro.grain.admin.service.mapper.ServiceTypeMapper;

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
 * Test class for the ServiceTypeResource REST controller.
 *
 * @see ServiceTypeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GrainAdminApp.class)
public class ServiceTypeResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    @Inject
    private ServiceTypeRepository serviceTypeRepository;

    @Inject
    private ServiceTypeMapper serviceTypeMapper;

    @Inject
    private ServiceTypeService serviceTypeService;

    @Inject
    private ServiceTypeSearchRepository serviceTypeSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restServiceTypeMockMvc;

    private ServiceType serviceType;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ServiceTypeResource serviceTypeResource = new ServiceTypeResource();
        ReflectionTestUtils.setField(serviceTypeResource, "serviceTypeService", serviceTypeService);
        this.restServiceTypeMockMvc = MockMvcBuilders.standaloneSetup(serviceTypeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ServiceType createEntity(EntityManager em) {
        ServiceType serviceType = new ServiceType()
                .name(DEFAULT_NAME);
        return serviceType;
    }

    @Before
    public void initTest() {
        serviceTypeSearchRepository.deleteAll();
        serviceType = createEntity(em);
    }

    @Test
    @Transactional
    public void createServiceType() throws Exception {
        int databaseSizeBeforeCreate = serviceTypeRepository.findAll().size();

        // Create the ServiceType
        ServiceTypeDTO serviceTypeDTO = serviceTypeMapper.serviceTypeToServiceTypeDTO(serviceType);

        restServiceTypeMockMvc.perform(post("/api/service-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(serviceTypeDTO)))
                .andExpect(status().isCreated());

        // Validate the ServiceType in the database
        List<ServiceType> serviceTypes = serviceTypeRepository.findAll();
        assertThat(serviceTypes).hasSize(databaseSizeBeforeCreate + 1);
        ServiceType testServiceType = serviceTypes.get(serviceTypes.size() - 1);
        assertThat(testServiceType.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the ServiceType in ElasticSearch
        ServiceType serviceTypeEs = serviceTypeSearchRepository.findOne(testServiceType.getId());
        assertThat(serviceTypeEs).isEqualToComparingFieldByField(testServiceType);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = serviceTypeRepository.findAll().size();
        // set the field null
        serviceType.setName(null);

        // Create the ServiceType, which fails.
        ServiceTypeDTO serviceTypeDTO = serviceTypeMapper.serviceTypeToServiceTypeDTO(serviceType);

        restServiceTypeMockMvc.perform(post("/api/service-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(serviceTypeDTO)))
                .andExpect(status().isBadRequest());

        List<ServiceType> serviceTypes = serviceTypeRepository.findAll();
        assertThat(serviceTypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllServiceTypes() throws Exception {
        // Initialize the database
        serviceTypeRepository.saveAndFlush(serviceType);

        // Get all the serviceTypes
        restServiceTypeMockMvc.perform(get("/api/service-types?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(serviceType.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getServiceType() throws Exception {
        // Initialize the database
        serviceTypeRepository.saveAndFlush(serviceType);

        // Get the serviceType
        restServiceTypeMockMvc.perform(get("/api/service-types/{id}", serviceType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(serviceType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingServiceType() throws Exception {
        // Get the serviceType
        restServiceTypeMockMvc.perform(get("/api/service-types/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateServiceType() throws Exception {
        // Initialize the database
        serviceTypeRepository.saveAndFlush(serviceType);
        serviceTypeSearchRepository.save(serviceType);
        int databaseSizeBeforeUpdate = serviceTypeRepository.findAll().size();

        // Update the serviceType
        ServiceType updatedServiceType = serviceTypeRepository.findOne(serviceType.getId());
        updatedServiceType
                .name(UPDATED_NAME);
        ServiceTypeDTO serviceTypeDTO = serviceTypeMapper.serviceTypeToServiceTypeDTO(updatedServiceType);

        restServiceTypeMockMvc.perform(put("/api/service-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(serviceTypeDTO)))
                .andExpect(status().isOk());

        // Validate the ServiceType in the database
        List<ServiceType> serviceTypes = serviceTypeRepository.findAll();
        assertThat(serviceTypes).hasSize(databaseSizeBeforeUpdate);
        ServiceType testServiceType = serviceTypes.get(serviceTypes.size() - 1);
        assertThat(testServiceType.getName()).isEqualTo(UPDATED_NAME);

        // Validate the ServiceType in ElasticSearch
        ServiceType serviceTypeEs = serviceTypeSearchRepository.findOne(testServiceType.getId());
        assertThat(serviceTypeEs).isEqualToComparingFieldByField(testServiceType);
    }

    @Test
    @Transactional
    public void deleteServiceType() throws Exception {
        // Initialize the database
        serviceTypeRepository.saveAndFlush(serviceType);
        serviceTypeSearchRepository.save(serviceType);
        int databaseSizeBeforeDelete = serviceTypeRepository.findAll().size();

        // Get the serviceType
        restServiceTypeMockMvc.perform(delete("/api/service-types/{id}", serviceType.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean serviceTypeExistsInEs = serviceTypeSearchRepository.exists(serviceType.getId());
        assertThat(serviceTypeExistsInEs).isFalse();

        // Validate the database is empty
        List<ServiceType> serviceTypes = serviceTypeRepository.findAll();
        assertThat(serviceTypes).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchServiceType() throws Exception {
        // Initialize the database
        serviceTypeRepository.saveAndFlush(serviceType);
        serviceTypeSearchRepository.save(serviceType);

        // Search the serviceType
        restServiceTypeMockMvc.perform(get("/api/_search/service-types?query=id:" + serviceType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(serviceType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
}
