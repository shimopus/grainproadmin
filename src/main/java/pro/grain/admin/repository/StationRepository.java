package pro.grain.admin.repository;

import pro.grain.admin.domain.Station;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Station entity.
 */
@SuppressWarnings("unused")
public interface StationRepository extends JpaRepository<Station,Long> {

}
