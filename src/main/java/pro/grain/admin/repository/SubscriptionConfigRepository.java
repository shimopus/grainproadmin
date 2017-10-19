package pro.grain.admin.repository;

import org.springframework.data.repository.query.Param;
import pro.grain.admin.domain.SubscriptionConfig;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the SubscriptionConfig entity.
 */
@SuppressWarnings("unused")
public interface SubscriptionConfigRepository extends JpaRepository<SubscriptionConfig,Long> {

    @Query("select distinct subscriptionConfig from SubscriptionConfig subscriptionConfig " +
        "where " +
        "   subscriptionConfig.partner.id = :partnerId")
    SubscriptionConfig findByPartner(@Param("partnerId") Long partnerId);

    @Query("select distinct subscriptionConfig from SubscriptionConfig subscriptionConfig " +
        "where " +
        "   subscriptionConfig.isActive = true")
    List<SubscriptionConfig> getAllActive();
}
