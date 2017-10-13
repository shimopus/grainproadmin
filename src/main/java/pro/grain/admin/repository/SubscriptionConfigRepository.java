package pro.grain.admin.repository;

import pro.grain.admin.domain.SubscriptionConfig;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the SubscriptionConfig entity.
 */
@SuppressWarnings("unused")
public interface SubscriptionConfigRepository extends JpaRepository<SubscriptionConfig,Long> {

}
