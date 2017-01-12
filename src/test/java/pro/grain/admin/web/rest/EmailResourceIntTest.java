package pro.grain.admin.web.rest;

import pro.grain.admin.GrainAdminApp;

import pro.grain.admin.domain.Email;
import pro.grain.admin.repository.EmailRepository;
import pro.grain.admin.service.EmailService;
import pro.grain.admin.repository.search.EmailSearchRepository;
import pro.grain.admin.service.dto.EmailDTO;
import pro.grain.admin.service.mapper.EmailMapper;

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
 * Test class for the EmailResource REST controller.
 *
 * @see EmailResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GrainAdminApp.class)
public class EmailResourceIntTest {

    private static final String DEFAULT_EMAIL = "AAAAA@bbb.com";
    private static final String UPDATED_EMAIL = "bbbbb@aaa.com";

    @Inject
    private EmailRepository emailRepository;

    @Inject
    private EmailMapper emailMapper;

    @Inject
    private EmailService emailService;

    @Inject
    private EmailSearchRepository emailSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restEmailMockMvc;

    private Email email;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        EmailResource emailResource = new EmailResource();
        ReflectionTestUtils.setField(emailResource, "emailService", emailService);
        this.restEmailMockMvc = MockMvcBuilders.standaloneSetup(emailResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Email createEntity(EntityManager em) {
        Email email = new Email()
                .email(DEFAULT_EMAIL);
        return email;
    }

    @Before
    public void initTest() {
        emailSearchRepository.deleteAll();
        email = createEntity(em);
    }

    @Test
    @Transactional
    public void createEmail() throws Exception {
        int databaseSizeBeforeCreate = emailRepository.findAll().size();

        // Create the Email
        EmailDTO emailDTO = emailMapper.emailToEmailDTO(email);

        restEmailMockMvc.perform(post("/api/emails")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(emailDTO)))
                .andExpect(status().isCreated());

        // Validate the Email in the database
        List<Email> emails = emailRepository.findAll();
        assertThat(emails).hasSize(databaseSizeBeforeCreate + 1);
        Email testEmail = emails.get(emails.size() - 1);
        assertThat(testEmail.getEmail()).isEqualTo(DEFAULT_EMAIL);

        // Validate the Email in ElasticSearch
        Email emailEs = emailSearchRepository.findOne(testEmail.getId());
        assertThat(emailEs).isEqualToComparingFieldByField(testEmail);
    }

    @Test
    @Transactional
    public void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = emailRepository.findAll().size();
        // set the field null
        email.setEmail(null);

        // Create the Email, which fails.
        EmailDTO emailDTO = emailMapper.emailToEmailDTO(email);

        restEmailMockMvc.perform(post("/api/emails")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(emailDTO)))
                .andExpect(status().isBadRequest());

        List<Email> emails = emailRepository.findAll();
        assertThat(emails).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllEmails() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emails
        restEmailMockMvc.perform(get("/api/emails?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(email.getId().intValue())))
                .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())));
    }

    @Test
    @Transactional
    public void getEmail() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get the email
        restEmailMockMvc.perform(get("/api/emails/{id}", email.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(email.getId().intValue()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingEmail() throws Exception {
        // Get the email
        restEmailMockMvc.perform(get("/api/emails/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEmail() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);
        emailSearchRepository.save(email);
        int databaseSizeBeforeUpdate = emailRepository.findAll().size();

        // Update the email
        Email updatedEmail = emailRepository.findOne(email.getId());
        updatedEmail
                .email(UPDATED_EMAIL);
        EmailDTO emailDTO = emailMapper.emailToEmailDTO(updatedEmail);

        restEmailMockMvc.perform(put("/api/emails")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(emailDTO)))
                .andExpect(status().isOk());

        // Validate the Email in the database
        List<Email> emails = emailRepository.findAll();
        assertThat(emails).hasSize(databaseSizeBeforeUpdate);
        Email testEmail = emails.get(emails.size() - 1);
        assertThat(testEmail.getEmail()).isEqualTo(UPDATED_EMAIL);

        // Validate the Email in ElasticSearch
        Email emailEs = emailSearchRepository.findOne(testEmail.getId());
        assertThat(emailEs).isEqualToComparingFieldByField(testEmail);
    }

    @Test
    @Transactional
    public void deleteEmail() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);
        emailSearchRepository.save(email);
        int databaseSizeBeforeDelete = emailRepository.findAll().size();

        // Get the email
        restEmailMockMvc.perform(delete("/api/emails/{id}", email.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean emailExistsInEs = emailSearchRepository.exists(email.getId());
        assertThat(emailExistsInEs).isFalse();

        // Validate the database is empty
        List<Email> emails = emailRepository.findAll();
        assertThat(emails).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchEmail() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);
        emailSearchRepository.save(email);

        // Search the email
        restEmailMockMvc.perform(get("/api/_search/emails?query=" + email.getEmail()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(email.getId().intValue())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())));
    }
}
