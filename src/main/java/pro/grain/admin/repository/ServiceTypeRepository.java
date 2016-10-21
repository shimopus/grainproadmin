package pro.grain.admin.repository;

import pro.grain.admin.domain.ServiceType;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ServiceType entity.
 */
@SuppressWarnings("unused")
public interface ServiceTypeRepository extends JpaRepository<ServiceType,Long> {

}
