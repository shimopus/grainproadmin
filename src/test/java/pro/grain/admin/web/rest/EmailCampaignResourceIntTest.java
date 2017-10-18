package pro.grain.admin.web.rest;

import pro.grain.admin.GrainAdminApp;

import pro.grain.admin.domain.EmailCampaign;
import pro.grain.admin.repository.EmailCampaignRepository;
import pro.grain.admin.service.EmailCampaignService;
import pro.grain.admin.repository.search.EmailCampaignSearchRepository;
import pro.grain.admin.service.dto.EmailCampaignDTO;
import pro.grain.admin.service.mapper.EmailCampaignMapper;

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
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the EmailCampaignResource REST controller.
 *
 * @see EmailCampaignResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GrainAdminApp.class)
public class EmailCampaignResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_DATE_STR = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(DEFAULT_DATE);

    @Inject
    private EmailCampaignRepository emailCampaignRepository;

    @Inject
    private EmailCampaignMapper emailCampaignMapper;

    @Inject
    private EmailCampaignService emailCampaignService;

    @Inject
    private EmailCampaignSearchRepository emailCampaignSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restEmailCampaignMockMvc;

    private EmailCampaign emailCampaign;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        EmailCampaignResource emailCampaignResource = new EmailCampaignResource();
        ReflectionTestUtils.setField(emailCampaignResource, "emailCampaignService", emailCampaignService);
        this.restEmailCampaignMockMvc = MockMvcBuilders.standaloneSetup(emailCampaignResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EmailCampaign createEntity(EntityManager em) {
        EmailCampaign emailCampaign = new EmailCampaign()
                .name(DEFAULT_NAME)
                .date(DEFAULT_DATE);
        return emailCampaign;
    }

    @Before
    public void initTest() {
        emailCampaignSearchRepository.deleteAll();
        emailCampaign = createEntity(em);
    }

    @Test
    @Transactional
    public void createEmailCampaign() throws Exception {
        int databaseSizeBeforeCreate = emailCampaignRepository.findAll().size();

        // Create the EmailCampaign
        EmailCampaignDTO emailCampaignDTO = emailCampaignMapper.emailCampaignToEmailCampaignDTO(emailCampaign);

        restEmailCampaignMockMvc.perform(post("/api/email-campaigns")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(emailCampaignDTO)))
                .andExpect(status().isCreated());

        // Validate the EmailCampaign in the database
        List<EmailCampaign> emailCampaigns = emailCampaignRepository.findAll();
        assertThat(emailCampaigns).hasSize(databaseSizeBeforeCreate + 1);
        EmailCampaign testEmailCampaign = emailCampaigns.get(emailCampaigns.size() - 1);
        assertThat(testEmailCampaign.getName()).isEqualTo(DEFAULT_NAME);
//        assertThat(testEmailCampaign.getDate()).isEqualTo(DEFAULT_DATE);

        // Validate the EmailCampaign in ElasticSearch
//        EmailCampaign emailCampaignEs = emailCampaignSearchRepository.findOne(testEmailCampaign.getId());
//        assertThat(emailCampaignEs).isEqualToComparingFieldByField(testEmailCampaign);
    }

    @Test
    @Transactional
    public void getAllEmailCampaigns() throws Exception {
        // Initialize the database
        emailCampaignRepository.saveAndFlush(emailCampaign);

        // Get all the emailCampaigns
        restEmailCampaignMockMvc.perform(get("/api/email-campaigns?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(emailCampaign.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
//                .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE_STR)));
    }

    @Test
    @Transactional
    public void getEmailCampaign() throws Exception {
        // Initialize the database
        emailCampaignRepository.saveAndFlush(emailCampaign);

        // Get the emailCampaign
        restEmailCampaignMockMvc.perform(get("/api/email-campaigns/{id}", emailCampaign.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(emailCampaign.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
//            .andExpect(jsonPath("$.date").value(DEFAULT_DATE_STR));
    }

    @Test
    @Transactional
    public void getNonExistingEmailCampaign() throws Exception {
        // Get the emailCampaign
        restEmailCampaignMockMvc.perform(get("/api/email-campaigns/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEmailCampaign() throws Exception {
        // Initialize the database
        emailCampaignRepository.saveAndFlush(emailCampaign);
//        emailCampaignSearchRepository.save(emailCampaign);
        int databaseSizeBeforeUpdate = emailCampaignRepository.findAll().size();

        // Update the emailCampaign
        EmailCampaign updatedEmailCampaign = emailCampaignRepository.findOne(emailCampaign.getId());
        updatedEmailCampaign
                .name(UPDATED_NAME)
                .date(UPDATED_DATE);
        EmailCampaignDTO emailCampaignDTO = emailCampaignMapper.emailCampaignToEmailCampaignDTO(updatedEmailCampaign);

        restEmailCampaignMockMvc.perform(put("/api/email-campaigns")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(emailCampaignDTO)))
                .andExpect(status().isOk());

        // Validate the EmailCampaign in the database
        List<EmailCampaign> emailCampaigns = emailCampaignRepository.findAll();
        assertThat(emailCampaigns).hasSize(databaseSizeBeforeUpdate);
        EmailCampaign testEmailCampaign = emailCampaigns.get(emailCampaigns.size() - 1);
        assertThat(testEmailCampaign.getName()).isEqualTo(UPDATED_NAME);
//        assertThat(testEmailCampaign.getDate()).isEqualTo(UPDATED_DATE);

        // Validate the EmailCampaign in ElasticSearch
//        EmailCampaign emailCampaignEs = emailCampaignSearchRepository.findOne(testEmailCampaign.getId());
//        assertThat(emailCampaignEs).isEqualToComparingFieldByField(testEmailCampaign);
    }

    @Test
    @Transactional
    public void deleteEmailCampaign() throws Exception {
        // Initialize the database
        emailCampaignRepository.saveAndFlush(emailCampaign);
//        emailCampaignSearchRepository.save(emailCampaign);
        int databaseSizeBeforeDelete = emailCampaignRepository.findAll().size();

        // Get the emailCampaign
        restEmailCampaignMockMvc.perform(delete("/api/email-campaigns/{id}", emailCampaign.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
//        boolean emailCampaignExistsInEs = emailCampaignSearchRepository.exists(emailCampaign.getId());
//        assertThat(emailCampaignExistsInEs).isFalse();

        // Validate the database is empty
        List<EmailCampaign> emailCampaigns = emailCampaignRepository.findAll();
        assertThat(emailCampaigns).hasSize(databaseSizeBeforeDelete - 1);
    }

//    @Test
    @Transactional
    public void searchEmailCampaign() throws Exception {
        // Initialize the database
        emailCampaignRepository.saveAndFlush(emailCampaign);
        emailCampaignSearchRepository.save(emailCampaign);

        // Search the emailCampaign
        restEmailCampaignMockMvc.perform(get("/api/_search/email-campaigns?query=id:" + emailCampaign.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(emailCampaign.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
//            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE_STR)));
    }
}
