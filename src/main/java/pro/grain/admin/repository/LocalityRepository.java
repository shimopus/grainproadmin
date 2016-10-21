package pro.grain.admin.repository;

import pro.grain.admin.domain.Locality;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Locality entity.
 */
@SuppressWarnings("unused")
public interface LocalityRepository extends JpaRepository<Locality,Long> {

}
