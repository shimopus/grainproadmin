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

    @Query("select distinct bid from Bid bid left join fetch bid.qualityParameters left join fetch bid.qualityPassports")
    List<Bid> findAllWithEagerRelationships();

    @Query("select distinct bid from Bid bid left join fetch bid.qualityParameters left join fetch bid.qualityPassports " +
        "where bid.agent.id =:id and bid.archiveDate = null " +
        "order by bid.creationDate desc")
    List<Bid> findAllNotArchivedWithEagerRelationshipsByPartner(@Param("id") Long id);

    @Query("select distinct bid from Bid bid left join fetch bid.qualityParameters left join fetch bid.qualityPassports " +
        "where bid.agent.id =:id and bid.archiveDate is not null " +
        "order by bid.archiveDate desc")
    List<Bid> findAllArchivedWithEagerRelationshipsByPartner(@Param("id") Long id);


    @Query("select bid from Bid bid left join fetch bid.qualityParameters left join fetch bid.qualityPassports where bid.id =:id")
    Bid findOneWithEagerRelationships(@Param("id") Long id);

}
