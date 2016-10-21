package pro.grain.admin.web.rest;

import pro.grain.admin.GrainAdminApp;

import pro.grain.admin.domain.TransportationPrice;
import pro.grain.admin.repository.TransportationPriceRepository;
import pro.grain.admin.service.TransportationPriceService;
import pro.grain.admin.repository.search.TransportationPriceSearchRepository;
import pro.grain.admin.service.dto.TransportationPriceDTO;
import pro.grain.admin.service.mapper.TransportationPriceMapper;

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
 * Test class for the TransportationPriceResource REST controller.
 *
 * @see TransportationPriceResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GrainAdminApp.class)
public class TransportationPriceResourceIntTest {

    private static final Long DEFAULT_PRICE = 1L;
    private static final Long UPDATED_PRICE = 2L;

    @Inject
    private TransportationPriceRepository transportationPriceRepository;

    @Inject
    private TransportationPriceMapper transportationPriceMapper;

    @Inject
    private TransportationPriceService transportationPriceService;

    @Inject
    private TransportationPriceSearchRepository transportationPriceSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restTransportationPriceMockMvc;

    private TransportationPrice transportationPrice;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TransportationPriceResource transportationPriceResource = new TransportationPriceResource();
        ReflectionTestUtils.setField(transportationPriceResource, "transportationPriceService", transportationPriceService);
        this.restTransportationPriceMockMvc = MockMvcBuilders.standaloneSetup(transportationPriceResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransportationPrice createEntity(EntityManager em) {
        TransportationPrice transportationPrice = new TransportationPrice()
                .price(DEFAULT_PRICE);
        return transportationPrice;
    }

    @Before
    public void initTest() {
        transportationPriceSearchRepository.deleteAll();
        transportationPrice = createEntity(em);
    }

    @Test
    @Transactional
    public void createTransportationPrice() throws Exception {
        int databaseSizeBeforeCreate = transportationPriceRepository.findAll().size();

        // Create the TransportationPrice
        TransportationPriceDTO transportationPriceDTO = transportationPriceMapper.transportationPriceToTransportationPriceDTO(transportationPrice);

        restTransportationPriceMockMvc.perform(post("/api/transportation-prices")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(transportationPriceDTO)))
                .andExpect(status().isCreated());

        // Validate the TransportationPrice in the database
        List<TransportationPrice> transportationPrices = transportationPriceRepository.findAll();
        assertThat(transportationPrices).hasSize(databaseSizeBeforeCreate + 1);
        TransportationPrice testTransportationPrice = transportationPrices.get(transportationPrices.size() - 1);
        assertThat(testTransportationPrice.getPrice()).isEqualTo(DEFAULT_PRICE);

        // Validate the TransportationPrice in ElasticSearch
        TransportationPrice transportationPriceEs = transportationPriceSearchRepository.findOne(testTransportationPrice.getId());
        assertThat(transportationPriceEs).isEqualToComparingFieldByField(testTransportationPrice);
    }

    @Test
    @Transactional
    public void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = transportationPriceRepository.findAll().size();
        // set the field null
        transportationPrice.setPrice(null);

        // Create the TransportationPrice, which fails.
        TransportationPriceDTO transportationPriceDTO = transportationPriceMapper.transportationPriceToTransportationPriceDTO(transportationPrice);

        restTransportationPriceMockMvc.perform(post("/api/transportation-prices")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(transportationPriceDTO)))
                .andExpect(status().isBadRequest());

        List<TransportationPrice> transportationPrices = transportationPriceRepository.findAll();
        assertThat(transportationPrices).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTransportationPrices() throws Exception {
        // Initialize the database
        transportationPriceRepository.saveAndFlush(transportationPrice);

        // Get all the transportationPrices
        restTransportationPriceMockMvc.perform(get("/api/transportation-prices?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(transportationPrice.getId().intValue())))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.intValue())));
    }

    @Test
    @Transactional
    public void getTransportationPrice() throws Exception {
        // Initialize the database
        transportationPriceRepository.saveAndFlush(transportationPrice);

        // Get the transportationPrice
        restTransportationPriceMockMvc.perform(get("/api/transportation-prices/{id}", transportationPrice.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(transportationPrice.getId().intValue()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingTransportationPrice() throws Exception {
        // Get the transportationPrice
        restTransportationPriceMockMvc.perform(get("/api/transportation-prices/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTransportationPrice() throws Exception {
        // Initialize the database
        transportationPriceRepository.saveAndFlush(transportationPrice);
        transportationPriceSearchRepository.save(transportationPrice);
        int databaseSizeBeforeUpdate = transportationPriceRepository.findAll().size();

        // Update the transportationPrice
        TransportationPrice updatedTransportationPrice = transportationPriceRepository.findOne(transportationPrice.getId());
        updatedTransportationPrice
                .price(UPDATED_PRICE);
        TransportationPriceDTO transportationPriceDTO = transportationPriceMapper.transportationPriceToTransportationPriceDTO(updatedTransportationPrice);

        restTransportationPriceMockMvc.perform(put("/api/transportation-prices")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(transportationPriceDTO)))
                .andExpect(status().isOk());

        // Validate the TransportationPrice in the database
        List<TransportationPrice> transportationPrices = transportationPriceRepository.findAll();
        assertThat(transportationPrices).hasSize(databaseSizeBeforeUpdate);
        TransportationPrice testTransportationPrice = transportationPrices.get(transportationPrices.size() - 1);
        assertThat(testTransportationPrice.getPrice()).isEqualTo(UPDATED_PRICE);

        // Validate the TransportationPrice in ElasticSearch
        TransportationPrice transportationPriceEs = transportationPriceSearchRepository.findOne(testTransportationPrice.getId());
        assertThat(transportationPriceEs).isEqualToComparingFieldByField(testTransportationPrice);
    }

    @Test
    @Transactional
    public void deleteTransportationPrice() throws Exception {
        // Initialize the database
        transportationPriceRepository.saveAndFlush(transportationPrice);
        transportationPriceSearchRepository.save(transportationPrice);
        int databaseSizeBeforeDelete = transportationPriceRepository.findAll().size();

        // Get the transportationPrice
        restTransportationPriceMockMvc.perform(delete("/api/transportation-prices/{id}", transportationPrice.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean transportationPriceExistsInEs = transportationPriceSearchRepository.exists(transportationPrice.getId());
        assertThat(transportationPriceExistsInEs).isFalse();

        // Validate the database is empty
        List<TransportationPrice> transportationPrices = transportationPriceRepository.findAll();
        assertThat(transportationPrices).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTransportationPrice() throws Exception {
        // Initialize the database
        transportationPriceRepository.saveAndFlush(transportationPrice);
        transportationPriceSearchRepository.save(transportationPrice);

        // Search the transportationPrice
        restTransportationPriceMockMvc.perform(get("/api/_search/transportation-prices?query=id:" + transportationPrice.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transportationPrice.getId().intValue())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.intValue())));
    }
}
