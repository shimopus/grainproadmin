package pro.grain.admin.web.rest;

import pro.grain.admin.GrainAdminApp;

import pro.grain.admin.domain.Passport;
import pro.grain.admin.repository.PassportRepository;
import pro.grain.admin.service.PassportService;
import pro.grain.admin.repository.search.PassportSearchRepository;
import pro.grain.admin.service.dto.PassportDTO;
import pro.grain.admin.service.mapper.PassportMapper;

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
import org.springframework.util.Base64Utils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the PassportResource REST controller.
 *
 * @see PassportResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GrainAdminApp.class)
public class PassportResourceIntTest {

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_TITLE = "AAAAA";
    private static final String UPDATED_TITLE = "BBBBB";

    @Inject
    private PassportRepository passportRepository;

    @Inject
    private PassportMapper passportMapper;

    @Inject
    private PassportService passportService;

    @Inject
    private PassportSearchRepository passportSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restPassportMockMvc;

    private Passport passport;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PassportResource passportResource = new PassportResource();
        ReflectionTestUtils.setField(passportResource, "passportService", passportService);
        this.restPassportMockMvc = MockMvcBuilders.standaloneSetup(passportResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Passport createEntity(EntityManager em) {
        Passport passport = new Passport()
                .image(DEFAULT_IMAGE)
                .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE)
                .title(DEFAULT_TITLE);
        return passport;
    }

    @Before
    public void initTest() {
        passportSearchRepository.deleteAll();
        passport = createEntity(em);
    }

    @Test
    @Transactional
    public void createPassport() throws Exception {
        int databaseSizeBeforeCreate = passportRepository.findAll().size();

        // Create the Passport
        PassportDTO passportDTO = passportMapper.passportToPassportDTO(passport);

        restPassportMockMvc.perform(post("/api/passports")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(passportDTO)))
                .andExpect(status().isCreated());

        // Validate the Passport in the database
        List<Passport> passports = passportRepository.findAll();
        assertThat(passports).hasSize(databaseSizeBeforeCreate + 1);
        Passport testPassport = passports.get(passports.size() - 1);
        assertThat(testPassport.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testPassport.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testPassport.getTitle()).isEqualTo(DEFAULT_TITLE);

        // Validate the Passport in ElasticSearch
        Passport passportEs = passportSearchRepository.findOne(testPassport.getId());
        assertThat(passportEs).isEqualToComparingFieldByField(testPassport);
    }

    @Test
    @Transactional
    public void checkImageIsRequired() throws Exception {
        int databaseSizeBeforeTest = passportRepository.findAll().size();
        // set the field null
        passport.setImage(null);

        // Create the Passport, which fails.
        PassportDTO passportDTO = passportMapper.passportToPassportDTO(passport);

        restPassportMockMvc.perform(post("/api/passports")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(passportDTO)))
                .andExpect(status().isBadRequest());

        List<Passport> passports = passportRepository.findAll();
        assertThat(passports).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPassports() throws Exception {
        // Initialize the database
        passportRepository.saveAndFlush(passport);

        // Get all the passports
        restPassportMockMvc.perform(get("/api/passports?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(passport.getId().intValue())))
                .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
                .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
                .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())));
    }

    @Test
    @Transactional
    public void getPassport() throws Exception {
        // Initialize the database
        passportRepository.saveAndFlush(passport);

        // Get the passport
        restPassportMockMvc.perform(get("/api/passports/{id}", passport.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(passport.getId().intValue()))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPassport() throws Exception {
        // Get the passport
        restPassportMockMvc.perform(get("/api/passports/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePassport() throws Exception {
        // Initialize the database
        passportRepository.saveAndFlush(passport);
        passportSearchRepository.save(passport);
        int databaseSizeBeforeUpdate = passportRepository.findAll().size();

        // Update the passport
        Passport updatedPassport = passportRepository.findOne(passport.getId());
        updatedPassport
                .image(UPDATED_IMAGE)
                .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
                .title(UPDATED_TITLE);
        PassportDTO passportDTO = passportMapper.passportToPassportDTO(updatedPassport);

        restPassportMockMvc.perform(put("/api/passports")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(passportDTO)))
                .andExpect(status().isOk());

        // Validate the Passport in the database
        List<Passport> passports = passportRepository.findAll();
        assertThat(passports).hasSize(databaseSizeBeforeUpdate);
        Passport testPassport = passports.get(passports.size() - 1);
        assertThat(testPassport.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testPassport.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testPassport.getTitle()).isEqualTo(UPDATED_TITLE);

        // Validate the Passport in ElasticSearch
        Passport passportEs = passportSearchRepository.findOne(testPassport.getId());
        assertThat(passportEs).isEqualToComparingFieldByField(testPassport);
    }

    @Test
    @Transactional
    public void deletePassport() throws Exception {
        // Initialize the database
        passportRepository.saveAndFlush(passport);
        passportSearchRepository.save(passport);
        int databaseSizeBeforeDelete = passportRepository.findAll().size();

        // Get the passport
        restPassportMockMvc.perform(delete("/api/passports/{id}", passport.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean passportExistsInEs = passportSearchRepository.exists(passport.getId());
        assertThat(passportExistsInEs).isFalse();

        // Validate the database is empty
        List<Passport> passports = passportRepository.findAll();
        assertThat(passports).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPassport() throws Exception {
        // Initialize the database
        passportRepository.saveAndFlush(passport);
        passportSearchRepository.save(passport);

        // Search the passport
        restPassportMockMvc.perform(get("/api/_search/passports?query=id:" + passport.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(passport.getId().intValue())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())));
    }
}
