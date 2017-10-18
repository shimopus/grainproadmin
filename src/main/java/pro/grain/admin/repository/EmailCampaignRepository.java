package pro.grain.admin.repository;

import pro.grain.admin.domain.EmailCampaign;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the EmailCampaign entity.
 */
@SuppressWarnings("unused")
public interface EmailCampaignRepository extends JpaRepository<EmailCampaign,Long> {

}
