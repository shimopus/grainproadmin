package pro.grain.admin.repository;

import org.springframework.data.repository.query.Param;
import pro.grain.admin.domain.Station;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Station entity.
 */
@SuppressWarnings("unused")
public interface StationRepository extends JpaRepository<Station,Long> {
    @Query("select distinct station from Station station where code = :code")
    Station findByCode (@Param("code") String code);

    @Query("select distinct station from Station station where code in " +
        "(select code from StationToLocation station_location where " +
        "region_id = :region_id and " +
        "district_id = :district_id and " +
        "locality_id = :locality_id" +
        ")")
    Station findByLocation(@Param("region_id") Long region_id, @Param("district_id") Long district_id, @Param("locality_id") Long locality_id);

    @Query("select distinct station from Station station where code in " +
        "(select code from StationToLocation station_location where " +
        "region_id = :region_id and " +
        "district_id = :district_id" +
        ")")
    Station findByLocation(@Param("region_id") Long region_id, @Param("district_id") Long district_id);
}
