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
}
