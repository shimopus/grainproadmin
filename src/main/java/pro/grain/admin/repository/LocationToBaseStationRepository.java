package pro.grain.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.grain.admin.domain.LocationToBaseStation;
import pro.grain.admin.domain.Station;

import java.util.List;

@SuppressWarnings("unused")
public interface LocationToBaseStationRepository extends JpaRepository<LocationToBaseStation,Long> {

    @Query("select distinct ltbs.baseStation from LocationToBaseStation ltbs")
    List<Station> getAllBaseStatons();

    @Query("select distinct lts.baseStation from Partner part, Station part_st, LocationToBaseStation lts " +
        "where " +
        "part.station.id = part_st.id and " +
        "lts.pk.region.id = part_st.region.id and " +
        "lts.pk.district.id = part_st.district.id and " +
        "(part_st.locality is null or " +
        "lts.pk.locality.id = part_st.locality.id)")
    List<Station> getBaseStationsForCurrentPartners();
}
