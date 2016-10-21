package pro.grain.admin.repository;

import pro.grain.admin.domain.QualityValue;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the QualityValue entity.
 */
@SuppressWarnings("unused")
public interface QualityValueRepository extends JpaRepository<QualityValue,Long> {

}
