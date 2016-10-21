package pro.grain.admin.repository;

import pro.grain.admin.domain.OrganisationType;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the OrganisationType entity.
 */
@SuppressWarnings("unused")
public interface OrganisationTypeRepository extends JpaRepository<OrganisationType,Long> {

}
