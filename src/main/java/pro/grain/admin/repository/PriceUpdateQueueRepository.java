package pro.grain.admin.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import pro.grain.admin.domain.PriceUpdateQueue;

import org.springframework.data.jpa.repository.*;

import javax.persistence.LockModeType;
import java.util.List;

/**
 * Spring Data JPA repository for the PriceUpdateQueue entity.
 */
@SuppressWarnings("unused")
public interface PriceUpdateQueueRepository extends JpaRepository<PriceUpdateQueue,Long> {

    @Query("select puq from PriceUpdateQueue puq " +
        "where " +
        "puq.loaded = false " +
        "order by puq.loadingOrder asc")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<PriceUpdateQueue> findNextAvailable(Pageable pageable);

    default PriceUpdateQueue findNextAvailable() {
        List<PriceUpdateQueue> priceUpdateQueueList = findNextAvailable(new PageRequest(0, 1));

        if (priceUpdateQueueList != null && priceUpdateQueueList.size() > 0) {
            return priceUpdateQueueList.iterator().next();
        } else {
            return null;
        }
    }

    @Modifying
    @Query("update PriceUpdateQueue puq " +
        "set " +
        "puq.loaded = true " +
        "where puq.id = :id")
    void markAsUnavailable(@Param("id") Long id);
}
