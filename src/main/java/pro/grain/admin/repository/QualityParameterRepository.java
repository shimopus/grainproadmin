package pro.grain.admin.repository;

import pro.grain.admin.domain.QualityParameter;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the QualityParameter entity.
 */
@SuppressWarnings("unused")
public interface QualityParameterRepository extends JpaRepository<QualityParameter,Long> {

}
