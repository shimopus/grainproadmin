package pro.grain.admin.repository;

import pro.grain.admin.domain.PriceUpdateQueue;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the PriceUpdateQueue entity.
 */
@SuppressWarnings("unused")
public interface PriceUpdateQueueRepository extends JpaRepository<PriceUpdateQueue,Long> {

}
