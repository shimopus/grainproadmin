package pro.grain.admin.repository;

import pro.grain.admin.domain.Bid;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Bid entity.
 */
@SuppressWarnings("unused")
public interface BidRepository extends JpaRepository<Bid,Long> {

    @Query("select distinct bid from Bid bid left join fetch bid.qualityParameters where bid.agent_id =:id")
    List<Bid> findAllWithEagerRelationships();

    List<Bid> findAllWithEagerRelationshipsByPartner(@Param("id") Long id);

    @Query("select bid from Bid bid left join fetch bid.qualityParameters where bid.id =:id")
    Bid findOneWithEagerRelationships(@Param("id") Long id);

}
