package pro.grain.admin.repository;

import pro.grain.admin.domain.District;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the District entity.
 */
@SuppressWarnings("unused")
public interface DistrictRepository extends JpaRepository<District,Long> {

}
