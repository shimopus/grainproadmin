package pro.grain.admin.web.rest;

import pro.grain.admin.GrainAdminApp;

import pro.grain.admin.domain.ServicePrice;
import pro.grain.admin.repository.ServicePriceRepository;
import pro.grain.admin.service.ServicePriceService;
import pro.grain.admin.repository.search.ServicePriceSearchRepository;
import pro.grain.admin.service.dto.ServicePriceDTO;
import pro.grain.admin.service.mapper.ServicePriceMapper;

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
 * Test class for the ServicePriceResource REST controller.
 *
 * @see ServicePriceResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GrainAdminApp.class)
public class ServicePriceResourceIntTest {

    private static final Long DEFAULT_PRICE = 1L;
    private static final Long UPDATED_PRICE = 2L;

    @Inject
    private ServicePriceRepository servicePriceRepository;

    @Inject
    private ServicePriceMapper servicePriceMapper;

    @Inject
    private ServicePriceService servicePriceService;

    @Inject
    private ServicePriceSearchRepository servicePriceSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restServicePriceMockMvc;

    private ServicePrice servicePrice;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ServicePriceResource servicePriceResource = new ServicePriceResource();
        ReflectionTestUtils.setField(servicePriceResource, "servicePriceService", servicePriceService);
        this.restServicePriceMockMvc = MockMvcBuilders.standaloneSetup(servicePriceResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ServicePrice createEntity(EntityManager em) {
        ServicePrice servicePrice = new ServicePrice()
                .price(DEFAULT_PRICE);
        return servicePrice;
    }

    @Before
    public void initTest() {
        servicePriceSearchRepository.deleteAll();
        servicePrice = createEntity(em);
    }

    @Test
    @Transactional
    public void createServicePrice() throws Exception {
        int databaseSizeBeforeCreate = servicePriceRepository.findAll().size();

        // Create the ServicePrice
        ServicePriceDTO servicePriceDTO = servicePriceMapper.servicePriceToServicePriceDTO(servicePrice);

        restServicePriceMockMvc.perform(post("/api/service-prices")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(servicePriceDTO)))
                .andExpect(status().isCreated());

        // Validate the ServicePrice in the database
        List<ServicePrice> servicePrices = servicePriceRepository.findAll();
        assertThat(servicePrices).hasSize(databaseSizeBeforeCreate + 1);
        ServicePrice testServicePrice = servicePrices.get(servicePrices.size() - 1);
        assertThat(testServicePrice.getPrice()).isEqualTo(DEFAULT_PRICE);

        // Validate the ServicePrice in ElasticSearch
        ServicePrice servicePriceEs = servicePriceSearchRepository.findOne(testServicePrice.getId());
        assertThat(servicePriceEs).isEqualToComparingFieldByField(testServicePrice);
    }

    @Test
    @Transactional
    public void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = servicePriceRepository.findAll().size();
        // set the field null
        servicePrice.setPrice(null);

        // Create the ServicePrice, which fails.
        ServicePriceDTO servicePriceDTO = servicePriceMapper.servicePriceToServicePriceDTO(servicePrice);

        restServicePriceMockMvc.perform(post("/api/service-prices")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(servicePriceDTO)))
                .andExpect(status().isBadRequest());

        List<ServicePrice> servicePrices = servicePriceRepository.findAll();
        assertThat(servicePrices).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllServicePrices() throws Exception {
        // Initialize the database
        servicePriceRepository.saveAndFlush(servicePrice);

        // Get all the servicePrices
        restServicePriceMockMvc.perform(get("/api/service-prices?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(servicePrice.getId().intValue())))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.intValue())));
    }

    @Test
    @Transactional
    public void getServicePrice() throws Exception {
        // Initialize the database
        servicePriceRepository.saveAndFlush(servicePrice);

        // Get the servicePrice
        restServicePriceMockMvc.perform(get("/api/service-prices/{id}", servicePrice.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(servicePrice.getId().intValue()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingServicePrice() throws Exception {
        // Get the servicePrice
        restServicePriceMockMvc.perform(get("/api/service-prices/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateServicePrice() throws Exception {
        // Initialize the database
        servicePriceRepository.saveAndFlush(servicePrice);
        servicePriceSearchRepository.save(servicePrice);
        int databaseSizeBeforeUpdate = servicePriceRepository.findAll().size();

        // Update the servicePrice
        ServicePrice updatedServicePrice = servicePriceRepository.findOne(servicePrice.getId());
        updatedServicePrice
                .price(UPDATED_PRICE);
        ServicePriceDTO servicePriceDTO = servicePriceMapper.servicePriceToServicePriceDTO(updatedServicePrice);

        restServicePriceMockMvc.perform(put("/api/service-prices")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(servicePriceDTO)))
                .andExpect(status().isOk());

        // Validate the ServicePrice in the database
        List<ServicePrice> servicePrices = servicePriceRepository.findAll();
        assertThat(servicePrices).hasSize(databaseSizeBeforeUpdate);
        ServicePrice testServicePrice = servicePrices.get(servicePrices.size() - 1);
        assertThat(testServicePrice.getPrice()).isEqualTo(UPDATED_PRICE);

        // Validate the ServicePrice in ElasticSearch
        ServicePrice servicePriceEs = servicePriceSearchRepository.findOne(testServicePrice.getId());
        assertThat(servicePriceEs).isEqualToComparingFieldByField(testServicePrice);
    }

    @Test
    @Transactional
    public void deleteServicePrice() throws Exception {
        // Initialize the database
        servicePriceRepository.saveAndFlush(servicePrice);
        servicePriceSearchRepository.save(servicePrice);
        int databaseSizeBeforeDelete = servicePriceRepository.findAll().size();

        // Get the servicePrice
        restServicePriceMockMvc.perform(delete("/api/service-prices/{id}", servicePrice.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean servicePriceExistsInEs = servicePriceSearchRepository.exists(servicePrice.getId());
        assertThat(servicePriceExistsInEs).isFalse();

        // Validate the database is empty
        List<ServicePrice> servicePrices = servicePriceRepository.findAll();
        assertThat(servicePrices).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchServicePrice() throws Exception {
        // Initialize the database
        servicePriceRepository.saveAndFlush(servicePrice);
        servicePriceSearchRepository.save(servicePrice);

        // Search the servicePrice
        restServicePriceMockMvc.perform(get("/api/_search/service-prices?query=id:" + servicePrice.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(servicePrice.getId().intValue())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.intValue())));
    }
}
