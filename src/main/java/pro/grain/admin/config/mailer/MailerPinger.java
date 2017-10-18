package pro.grain.admin.config.mailer;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pro.grain.admin.config.GrainProAdminProperties;

import javax.inject.Inject;

@Service
public class MailerPinger {
    @Inject
    private RestTemplate restTemplate;

    @Inject
    private GrainProAdminProperties grainProAdminProperties;

    @Scheduled(fixedDelay = 15 /*min*/ * 60 /*sec*/ * 1000 /*msec*/)
    public void pingServer() {
        restTemplate.getForEntity(grainProAdminProperties.getMailer().getUrl() + "/", String.class);
    }
}
