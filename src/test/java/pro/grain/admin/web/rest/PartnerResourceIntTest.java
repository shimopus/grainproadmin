package pro.grain.admin.web.rest;

import pro.grain.admin.GrainAdminApp;

import pro.grain.admin.domain.Partner;
import pro.grain.admin.repository.PartnerRepository;
import pro.grain.admin.service.PartnerService;
import pro.grain.admin.repository.search.PartnerSearchRepository;
import pro.grain.admin.service.dto.PartnerDTO;
import pro.grain.admin.service.mapper.PartnerMapper;

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

import pro.grain.admin.domain.enumeration.NDS;
/**
 * Test class for the PartnerResource REST controller.
 *
 * @see PartnerResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GrainAdminApp.class)
public class PartnerResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    private static final String DEFAULT_SHORT_NAME = "AAAAA";
    private static final String UPDATED_SHORT_NAME = "BBBBB";

    private static final String DEFAULT_INN = "AAAAA";
    private static final String UPDATED_INN = "BBBBB";

    private static final NDS DEFAULT_NDS = NDS.INCLUDED;
    private static final NDS UPDATED_NDS = NDS.EXCLUDED;

    private static final String DEFAULT_CARD = "AAAAA";
    private static final String UPDATED_CARD = "BBBBB";

    @Inject
    private PartnerRepository partnerRepository;

    @Inject
    private PartnerMapper partnerMapper;

    @Inject
    private PartnerService partnerService;

    @Inject
    private PartnerSearchRepository partnerSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restPartnerMockMvc;

    private Partner partner;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PartnerResource partnerResource = new PartnerResource();
        ReflectionTestUtils.setField(partnerResource, "partnerService", partnerService);
        this.restPartnerMockMvc = MockMvcBuilders.standaloneSetup(partnerResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Partner createEntity(EntityManager em) {
        Partner partner = new Partner()
                .name(DEFAULT_NAME)
                .shortName(DEFAULT_SHORT_NAME)
                .inn(DEFAULT_INN)
                .nds(DEFAULT_NDS)
                .card(DEFAULT_CARD);
        return partner;
    }

    @Before
    public void initTest() {
        partnerSearchRepository.deleteAll();
        partner = createEntity(em);
    }

    @Test
    @Transactional
    public void createPartner() throws Exception {
        int databaseSizeBeforeCreate = partnerRepository.findAll().size();

        // Create the Partner
        PartnerDTO partnerDTO = partnerMapper.partnerToPartnerDTO(partner);

        restPartnerMockMvc.perform(post("/api/partners")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(partnerDTO)))
                .andExpect(status().isCreated());

        // Validate the Partner in the database
        List<Partner> partners = partnerRepository.findAll();
        assertThat(partners).hasSize(databaseSizeBeforeCreate + 1);
        Partner testPartner = partners.get(partners.size() - 1);
        assertThat(testPartner.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPartner.getShortName()).isEqualTo(DEFAULT_SHORT_NAME);
        assertThat(testPartner.getInn()).isEqualTo(DEFAULT_INN);
        assertThat(testPartner.getNds()).isEqualTo(DEFAULT_NDS);
        assertThat(testPartner.getCard()).isEqualTo(DEFAULT_CARD);

        // Validate the Partner in ElasticSearch
        Partner partnerEs = partnerSearchRepository.findOne(testPartner.getId());
        assertThat(partnerEs).isEqualToComparingFieldByField(testPartner);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = partnerRepository.findAll().size();
        // set the field null
        partner.setName(null);

        // Create the Partner, which fails.
        PartnerDTO partnerDTO = partnerMapper.partnerToPartnerDTO(partner);

        restPartnerMockMvc.perform(post("/api/partners")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(partnerDTO)))
                .andExpect(status().isBadRequest());

        List<Partner> partners = partnerRepository.findAll();
        assertThat(partners).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPartners() throws Exception {
        // Initialize the database
        partnerRepository.saveAndFlush(partner);

        // Get all the partners
        restPartnerMockMvc.perform(get("/api/partners?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(partner.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].shortName").value(hasItem(DEFAULT_SHORT_NAME.toString())))
                .andExpect(jsonPath("$.[*].inn").value(hasItem(DEFAULT_INN.toString())))
                .andExpect(jsonPath("$.[*].nds").value(hasItem(DEFAULT_NDS.toString())))
                .andExpect(jsonPath("$.[*].card").value(hasItem(DEFAULT_CARD.toString())));
    }

    @Test
    @Transactional
    public void getPartner() throws Exception {
        // Initialize the database
        partnerRepository.saveAndFlush(partner);

        // Get the partner
        restPartnerMockMvc.perform(get("/api/partners/{id}", partner.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(partner.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.shortName").value(DEFAULT_SHORT_NAME.toString()))
            .andExpect(jsonPath("$.inn").value(DEFAULT_INN.toString()))
            .andExpect(jsonPath("$.nds").value(DEFAULT_NDS.toString()))
            .andExpect(jsonPath("$.card").value(DEFAULT_CARD.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPartner() throws Exception {
        // Get the partner
        restPartnerMockMvc.perform(get("/api/partners/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePartner() throws Exception {
        // Initialize the database
        partnerRepository.saveAndFlush(partner);
        partnerSearchRepository.save(partner);
        int databaseSizeBeforeUpdate = partnerRepository.findAll().size();

        // Update the partner
        Partner updatedPartner = partnerRepository.findOne(partner.getId());
        updatedPartner
                .name(UPDATED_NAME)
                .shortName(UPDATED_SHORT_NAME)
                .inn(UPDATED_INN)
                .nds(UPDATED_NDS)
                .card(UPDATED_CARD);
        PartnerDTO partnerDTO = partnerMapper.partnerToPartnerDTO(updatedPartner);

        restPartnerMockMvc.perform(put("/api/partners")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(partnerDTO)))
                .andExpect(status().isOk());

        // Validate the Partner in the database
        List<Partner> partners = partnerRepository.findAll();
        assertThat(partners).hasSize(databaseSizeBeforeUpdate);
        Partner testPartner = partners.get(partners.size() - 1);
        assertThat(testPartner.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPartner.getShortName()).isEqualTo(UPDATED_SHORT_NAME);
        assertThat(testPartner.getInn()).isEqualTo(UPDATED_INN);
        assertThat(testPartner.getNds()).isEqualTo(UPDATED_NDS);
        assertThat(testPartner.getCard()).isEqualTo(UPDATED_CARD);

        // Validate the Partner in ElasticSearch
        Partner partnerEs = partnerSearchRepository.findOne(testPartner.getId());
        assertThat(partnerEs).isEqualToComparingFieldByField(testPartner);
    }

    @Test
    @Transactional
    public void deletePartner() throws Exception {
        // Initialize the database
        partnerRepository.saveAndFlush(partner);
        partnerSearchRepository.save(partner);
        int databaseSizeBeforeDelete = partnerRepository.findAll().size();

        // Get the partner
        restPartnerMockMvc.perform(delete("/api/partners/{id}", partner.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean partnerExistsInEs = partnerSearchRepository.exists(partner.getId());
        assertThat(partnerExistsInEs).isFalse();

        // Validate the database is empty
        List<Partner> partners = partnerRepository.findAll();
        assertThat(partners).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPartner() throws Exception {
        // Initialize the database
        partnerRepository.saveAndFlush(partner);
        partnerSearchRepository.save(partner);

        // Search the partner
        restPartnerMockMvc.perform(get("/api/_search/partners?query=id:" + partner.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(partner.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].shortName").value(hasItem(DEFAULT_SHORT_NAME.toString())))
            .andExpect(jsonPath("$.[*].inn").value(hasItem(DEFAULT_INN.toString())))
            .andExpect(jsonPath("$.[*].nds").value(hasItem(DEFAULT_NDS.toString())))
            .andExpect(jsonPath("$.[*].card").value(hasItem(DEFAULT_CARD.toString())));
    }
}
