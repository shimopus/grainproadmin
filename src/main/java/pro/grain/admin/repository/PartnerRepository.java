package pro.grain.admin.repository;

import pro.grain.admin.domain.Partner;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Partner entity.
 */
@SuppressWarnings("unused")
public interface PartnerRepository extends JpaRepository<Partner,Long> {

    @Query("select distinct partner from Partner partner left join fetch partner.contacts left join fetch partner.servicePrices")
    List<Partner> findAllWithEagerRelationships();

    @Query("select partner from Partner partner left join fetch partner.contacts left join fetch partner.servicePrices where partner.id =:id")
    Partner findOneWithEagerRelationships(@Param("id") Long id);

}
