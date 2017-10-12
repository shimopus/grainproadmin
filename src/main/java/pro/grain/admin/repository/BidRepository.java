package pro.grain.admin.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import pro.grain.admin.domain.Bid;
import pro.grain.admin.domain.BidPrice;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import pro.grain.admin.domain.TransportationPrice;
import pro.grain.admin.domain.enumeration.BidType;

import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.SqlResultSetMapping;
import java.util.List;

/**
 * Spring Data JPA repository for the Bid entity.
 */
@SuppressWarnings("unused")
public interface BidRepository extends JpaRepository<Bid,Long> {

    @Query("select distinct bid from Bid bid left join fetch bid.qualityParameters left join fetch bid.qualityPassports")
    List<Bid> findAllWithEagerRelationships();

    @Query("select distinct bid from Bid bid " +
        "where " +
        "   bid.agent.id =:id and " +
        "   bid.archiveDate is null and " +
        "   bid.bidType = :bidType ")
    List<Bid> findAllNotArchivedWithEagerRelationshipsByPartner(@Param("id") Long partnerId,
                                                                @Param("bidType") BidType bidType,
                                                                Sort sort);

    @Query("select distinct bid from Bid bid " +
        "where bid.agent.id =:id and bid.archiveDate is not null " +
        "order by bid.archiveDate desc")
    List<Bid> findAllArchivedWithEagerRelationshipsByPartner(@Param("id") Long partnerId, Pageable pageable);

    @Query("select distinct new pro.grain.admin.domain.BidPrice(bid) from Bid bid " +
//        "left join bid.qualityParameters " +
//        "left join bid.qualityPassports " +
        "where " +
        "   bid.isActive = true and" +
        "   bid.archiveDate is null and" +
        "   bid.bidType = :bidType")
    List<BidPrice> findAllCurrentBids(@Param("bidType") BidType bidType);


    //The query is defined as a named query on a Bid class.
    List<Object[]> findAllCurrentBidsWithTransportationPrice(@Param("code") String code,
                                 @Param("bidType") String bidType,
                                 @Param("versionNumber") Integer versionNumber);

    @Query("select distinct new pro.grain.admin.domain.BidPrice(bid) from Bid bid left join bid.qualityParameters left join bid.qualityPassports " +
        "where bid.isActive = true and bid.archiveDate is null")
    List<BidPrice> findAllCurrentWithEagerRelationships();

    @Query("select bid from Bid bid left join fetch bid.qualityParameters left join fetch bid.qualityPassports where bid.id =:id")
    Bid findOneWithEagerRelationships(@Param("id") Long id);

}
