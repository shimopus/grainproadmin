package pro.grain.admin.repository;

import pro.grain.admin.domain.TransportationPrice;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the TransportationPrice entity.
 */
@SuppressWarnings("unused")
public interface TransportationPriceRepository extends JpaRepository<TransportationPrice,Long> {

}
