package pro.grain.admin.repository;

import pro.grain.admin.domain.Bid;
import pro.grain.admin.domain.BidPrice;

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

    @Query("select distinct bid from Bid bid " +
        "left join fetch bid.qualityParameters " +
        "left join fetch bid.qualityPassports " +
        "where bid.agent.id =:id and bid.archiveDate is null " +
        "order by bid.creationDate desc")
    List<Bid> findAllNotArchivedWithEagerRelationshipsByPartner(@Param("id") Long partnerId);

    @Query("select distinct bid from Bid bid left join fetch bid.qualityParameters left join fetch bid.qualityPassports " +
        "where bid.agent.id =:id and bid.archiveDate is not null " +
        "order by bid.archiveDate desc")
    List<Bid> findAllArchivedWithEagerRelationshipsByPartner(@Param("id") Long partnerId);

    @Query("select distinct new pro.grain.admin.domain.BidPrice(bid, tp) " +
        "from Bid bid, TransportationPrice tp, LocationToBaseStation lts " +
        //"left join bid.qualityParameters " +
        //"left join bid.qualityPassports " +
        "where " +
        //Только активные заявки
        "   bid.isActive = true and" +
        "   bid.archiveDate is null and" +
        //Высчитываем базовую станцию
        "   lts.pk.region = bid.elevator.station.region and" +
        "   lts.pk.district = bid.elevator.station.district and" +
        "   (bid.elevator.station.locality is null or" +
        "   lts.pk.locality = bid.elevator.station.locality) and" +

        //Проверяем в одном направлении
        "   ((tp.stationFrom.code = lts.baseStation.code and " +
        "   tp.stationTo.code = :code)" +

        " or " +

        //Проверяем в другом
        "   (tp.stationTo.code = lts.baseStation.code and " +
        "   tp.stationFrom.code = :code))")
    List<BidPrice> findAllCurrentBidsWithTransportationPrice(@Param("code") String code);

    @Query("select distinct new pro.grain.admin.domain.BidPrice(bid) from Bid bid " +
        "left join bid.qualityParameters " +
        "left join bid.qualityPassports " +
        "where " +
        "   bid.isActive = true and" +
        "   bid.archiveDate is null")
    List<BidPrice> findAllCurrentBids();

    @Query("select distinct new pro.grain.admin.domain.BidPrice(bid) from Bid bid left join bid.qualityParameters left join bid.qualityPassports " +
        "where bid.isActive = true and bid.archiveDate is null")
    List<BidPrice> findAllCurrentWithEagerRelationships();

    @Query("select bid from Bid bid left join fetch bid.qualityParameters left join fetch bid.qualityPassports where bid.id =:id")
    Bid findOneWithEagerRelationships(@Param("id") Long id);

}
