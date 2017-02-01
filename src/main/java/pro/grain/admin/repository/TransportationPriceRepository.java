package pro.grain.admin.repository;

import org.springframework.data.repository.query.Param;
import pro.grain.admin.domain.TransportationPrice;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the TransportationPrice entity.
 */
@SuppressWarnings("unused")
public interface TransportationPriceRepository extends JpaRepository<TransportationPrice,Long> {
    @Query("select distinct tp from TransportationPrice tp " +
        "where " +
            "(tp.stationFrom.name like :stationFrom and " +
            "tp.stationTo.name like :stationTo) or " +
            "(tp.stationFrom.name like :stationTo and " +
            "tp.stationTo.name like :stationFrom)")
    TransportationPrice findByStationNames(@Param("stationFrom") String stationFromName, @Param("stationTo") String stationToName);
}
