package pro.grain.admin.repository;

import pro.grain.admin.domain.ServicePrice;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ServicePrice entity.
 */
@SuppressWarnings("unused")
public interface ServicePriceRepository extends JpaRepository<ServicePrice,Long> {

}
