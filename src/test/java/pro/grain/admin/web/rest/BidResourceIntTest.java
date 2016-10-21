package pro.grain.admin.web.rest;

import pro.grain.admin.GrainAdminApp;

import pro.grain.admin.domain.Bid;
import pro.grain.admin.repository.BidRepository;
import pro.grain.admin.service.BidService;
import pro.grain.admin.repository.search.BidSearchRepository;
import pro.grain.admin.service.dto.BidDTO;
import pro.grain.admin.service.mapper.BidMapper;

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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import pro.grain.admin.domain.enumeration.QualityClass;
import pro.grain.admin.domain.enumeration.NDS;
/**
 * Test class for the BidResource REST controller.
 *
 * @see BidResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GrainAdminApp.class)
public class BidResourceIntTest {

    private static final LocalDate DEFAULT_CREATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATION_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final QualityClass DEFAULT_QUALITY_CLASS = QualityClass.BKL;
    private static final QualityClass UPDATED_QUALITY_CLASS = QualityClass.ONE;

    private static final String DEFAULT_QUALITY_PASSPORT = "AAAAA";
    private static final String UPDATED_QUALITY_PASSPORT = "BBBBB";

    private static final Integer DEFAULT_VOLUME = 1;
    private static final Integer UPDATED_VOLUME = 2;

    private static final Long DEFAULT_PRICE = 1L;
    private static final Long UPDATED_PRICE = 2L;

    private static final NDS DEFAULT_NDS = NDS.INCLUDED;
    private static final NDS UPDATED_NDS = NDS.EXCLUDED;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final LocalDate DEFAULT_ARCHIVE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ARCHIVE_DATE = LocalDate.now(ZoneId.systemDefault());

    @Inject
    private BidRepository bidRepository;

    @Inject
    private BidMapper bidMapper;

    @Inject
    private BidService bidService;

    @Inject
    private BidSearchRepository bidSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restBidMockMvc;

    private Bid bid;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BidResource bidResource = new BidResource();
        ReflectionTestUtils.setField(bidResource, "bidService", bidService);
        this.restBidMockMvc = MockMvcBuilders.standaloneSetup(bidResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bid createEntity(EntityManager em) {
        Bid bid = new Bid()
                .creationDate(DEFAULT_CREATION_DATE)
                .qualityClass(DEFAULT_QUALITY_CLASS)
                .qualityPassport(DEFAULT_QUALITY_PASSPORT)
                .volume(DEFAULT_VOLUME)
                .price(DEFAULT_PRICE)
                .nds(DEFAULT_NDS)
                .isActive(DEFAULT_IS_ACTIVE)
                .archiveDate(DEFAULT_ARCHIVE_DATE);
        return bid;
    }

    @Before
    public void initTest() {
        bidSearchRepository.deleteAll();
        bid = createEntity(em);
    }

    @Test
    @Transactional
    public void createBid() throws Exception {
        int databaseSizeBeforeCreate = bidRepository.findAll().size();

        // Create the Bid
        BidDTO bidDTO = bidMapper.bidToBidDTO(bid);

        restBidMockMvc.perform(post("/api/bids")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bidDTO)))
                .andExpect(status().isCreated());

        // Validate the Bid in the database
        List<Bid> bids = bidRepository.findAll();
        assertThat(bids).hasSize(databaseSizeBeforeCreate + 1);
        Bid testBid = bids.get(bids.size() - 1);
        assertThat(testBid.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testBid.getQualityClass()).isEqualTo(DEFAULT_QUALITY_CLASS);
        assertThat(testBid.getQualityPassport()).isEqualTo(DEFAULT_QUALITY_PASSPORT);
        assertThat(testBid.getVolume()).isEqualTo(DEFAULT_VOLUME);
        assertThat(testBid.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testBid.getNds()).isEqualTo(DEFAULT_NDS);
        assertThat(testBid.isIsActive()).isEqualTo(DEFAULT_IS_ACTIVE);
        assertThat(testBid.getArchiveDate()).isEqualTo(DEFAULT_ARCHIVE_DATE);

        // Validate the Bid in ElasticSearch
        Bid bidEs = bidSearchRepository.findOne(testBid.getId());
        assertThat(bidEs).isEqualToComparingFieldByField(testBid);
    }

    @Test
    @Transactional
    public void checkCreationDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = bidRepository.findAll().size();
        // set the field null
        bid.setCreationDate(null);

        // Create the Bid, which fails.
        BidDTO bidDTO = bidMapper.bidToBidDTO(bid);

        restBidMockMvc.perform(post("/api/bids")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bidDTO)))
                .andExpect(status().isBadRequest());

        List<Bid> bids = bidRepository.findAll();
        assertThat(bids).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkQualityClassIsRequired() throws Exception {
        int databaseSizeBeforeTest = bidRepository.findAll().size();
        // set the field null
        bid.setQualityClass(null);

        // Create the Bid, which fails.
        BidDTO bidDTO = bidMapper.bidToBidDTO(bid);

        restBidMockMvc.perform(post("/api/bids")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bidDTO)))
                .andExpect(status().isBadRequest());

        List<Bid> bids = bidRepository.findAll();
        assertThat(bids).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkQualityPassportIsRequired() throws Exception {
        int databaseSizeBeforeTest = bidRepository.findAll().size();
        // set the field null
        bid.setQualityPassport(null);

        // Create the Bid, which fails.
        BidDTO bidDTO = bidMapper.bidToBidDTO(bid);

        restBidMockMvc.perform(post("/api/bids")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bidDTO)))
                .andExpect(status().isBadRequest());

        List<Bid> bids = bidRepository.findAll();
        assertThat(bids).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkVolumeIsRequired() throws Exception {
        int databaseSizeBeforeTest = bidRepository.findAll().size();
        // set the field null
        bid.setVolume(null);

        // Create the Bid, which fails.
        BidDTO bidDTO = bidMapper.bidToBidDTO(bid);

        restBidMockMvc.perform(post("/api/bids")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bidDTO)))
                .andExpect(status().isBadRequest());

        List<Bid> bids = bidRepository.findAll();
        assertThat(bids).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = bidRepository.findAll().size();
        // set the field null
        bid.setPrice(null);

        // Create the Bid, which fails.
        BidDTO bidDTO = bidMapper.bidToBidDTO(bid);

        restBidMockMvc.perform(post("/api/bids")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bidDTO)))
                .andExpect(status().isBadRequest());

        List<Bid> bids = bidRepository.findAll();
        assertThat(bids).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNdsIsRequired() throws Exception {
        int databaseSizeBeforeTest = bidRepository.findAll().size();
        // set the field null
        bid.setNds(null);

        // Create the Bid, which fails.
        BidDTO bidDTO = bidMapper.bidToBidDTO(bid);

        restBidMockMvc.perform(post("/api/bids")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bidDTO)))
                .andExpect(status().isBadRequest());

        List<Bid> bids = bidRepository.findAll();
        assertThat(bids).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBids() throws Exception {
        // Initialize the database
        bidRepository.saveAndFlush(bid);

        // Get all the bids
        restBidMockMvc.perform(get("/api/bids?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(bid.getId().intValue())))
                .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
                .andExpect(jsonPath("$.[*].qualityClass").value(hasItem(DEFAULT_QUALITY_CLASS.toString())))
                .andExpect(jsonPath("$.[*].qualityPassport").value(hasItem(DEFAULT_QUALITY_PASSPORT.toString())))
                .andExpect(jsonPath("$.[*].volume").value(hasItem(DEFAULT_VOLUME)))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.intValue())))
                .andExpect(jsonPath("$.[*].nds").value(hasItem(DEFAULT_NDS.toString())))
                .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())))
                .andExpect(jsonPath("$.[*].archiveDate").value(hasItem(DEFAULT_ARCHIVE_DATE.toString())));
    }

    @Test
    @Transactional
    public void getBid() throws Exception {
        // Initialize the database
        bidRepository.saveAndFlush(bid);

        // Get the bid
        restBidMockMvc.perform(get("/api/bids/{id}", bid.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(bid.getId().intValue()))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()))
            .andExpect(jsonPath("$.qualityClass").value(DEFAULT_QUALITY_CLASS.toString()))
            .andExpect(jsonPath("$.qualityPassport").value(DEFAULT_QUALITY_PASSPORT.toString()))
            .andExpect(jsonPath("$.volume").value(DEFAULT_VOLUME))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.intValue()))
            .andExpect(jsonPath("$.nds").value(DEFAULT_NDS.toString()))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.archiveDate").value(DEFAULT_ARCHIVE_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBid() throws Exception {
        // Get the bid
        restBidMockMvc.perform(get("/api/bids/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBid() throws Exception {
        // Initialize the database
        bidRepository.saveAndFlush(bid);
        bidSearchRepository.save(bid);
        int databaseSizeBeforeUpdate = bidRepository.findAll().size();

        // Update the bid
        Bid updatedBid = bidRepository.findOne(bid.getId());
        updatedBid
                .creationDate(UPDATED_CREATION_DATE)
                .qualityClass(UPDATED_QUALITY_CLASS)
                .qualityPassport(UPDATED_QUALITY_PASSPORT)
                .volume(UPDATED_VOLUME)
                .price(UPDATED_PRICE)
                .nds(UPDATED_NDS)
                .isActive(UPDATED_IS_ACTIVE)
                .archiveDate(UPDATED_ARCHIVE_DATE);
        BidDTO bidDTO = bidMapper.bidToBidDTO(updatedBid);

        restBidMockMvc.perform(put("/api/bids")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bidDTO)))
                .andExpect(status().isOk());

        // Validate the Bid in the database
        List<Bid> bids = bidRepository.findAll();
        assertThat(bids).hasSize(databaseSizeBeforeUpdate);
        Bid testBid = bids.get(bids.size() - 1);
        assertThat(testBid.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testBid.getQualityClass()).isEqualTo(UPDATED_QUALITY_CLASS);
        assertThat(testBid.getQualityPassport()).isEqualTo(UPDATED_QUALITY_PASSPORT);
        assertThat(testBid.getVolume()).isEqualTo(UPDATED_VOLUME);
        assertThat(testBid.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testBid.getNds()).isEqualTo(UPDATED_NDS);
        assertThat(testBid.isIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
        assertThat(testBid.getArchiveDate()).isEqualTo(UPDATED_ARCHIVE_DATE);

        // Validate the Bid in ElasticSearch
        Bid bidEs = bidSearchRepository.findOne(testBid.getId());
        assertThat(bidEs).isEqualToComparingFieldByField(testBid);
    }

    @Test
    @Transactional
    public void deleteBid() throws Exception {
        // Initialize the database
        bidRepository.saveAndFlush(bid);
        bidSearchRepository.save(bid);
        int databaseSizeBeforeDelete = bidRepository.findAll().size();

        // Get the bid
        restBidMockMvc.perform(delete("/api/bids/{id}", bid.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean bidExistsInEs = bidSearchRepository.exists(bid.getId());
        assertThat(bidExistsInEs).isFalse();

        // Validate the database is empty
        List<Bid> bids = bidRepository.findAll();
        assertThat(bids).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchBid() throws Exception {
        // Initialize the database
        bidRepository.saveAndFlush(bid);
        bidSearchRepository.save(bid);

        // Search the bid
        restBidMockMvc.perform(get("/api/_search/bids?query=id:" + bid.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bid.getId().intValue())))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].qualityClass").value(hasItem(DEFAULT_QUALITY_CLASS.toString())))
            .andExpect(jsonPath("$.[*].qualityPassport").value(hasItem(DEFAULT_QUALITY_PASSPORT.toString())))
            .andExpect(jsonPath("$.[*].volume").value(hasItem(DEFAULT_VOLUME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.intValue())))
            .andExpect(jsonPath("$.[*].nds").value(hasItem(DEFAULT_NDS.toString())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].archiveDate").value(hasItem(DEFAULT_ARCHIVE_DATE.toString())));
    }
}
