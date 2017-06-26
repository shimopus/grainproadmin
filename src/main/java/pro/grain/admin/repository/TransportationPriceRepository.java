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
            "((tp.stationFrom.name like :stationFrom and " +
            "tp.stationTo.name like :stationTo) or " +
            "(tp.stationFrom.name like :stationTo and " +
            "tp.stationTo.name like :stationFrom)) and " +
            "tp.versionNumber = :versionNumber")
    TransportationPrice findByStationNames(
        @Param("stationFrom") String stationFromName,
        @Param("stationTo") String stationToName,
        @Param("versionNumber") Integer versionNumber);

    @Query("select distinct tp from TransportationPrice tp " +
        "where " +
        "((tp.stationFrom.code like :stationFromCode and " +
        "tp.stationTo.code like :stationToCode) or " +
        "(tp.stationFrom.code like :stationToCode and " +
        "tp.stationTo.code like :stationFromCode)) and " +
        "tp.versionNumber = :versionNumber")
    TransportationPrice findByStationCodes(
        @Param("stationFromCode") String stationFromCode,
        @Param("stationToCode") String stationToCode,
        @Param("versionNumber") Integer versionNumber);
}
