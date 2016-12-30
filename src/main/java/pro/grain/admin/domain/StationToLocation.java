package pro.grain.admin.domain;

import com.google.common.base.Objects;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "station_location")
@IdClass(StationToLocation.LocationPK.class)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class StationToLocation {
    private static final long serialVersionUID = 1L;

    @Id
    @NotNull
    @Column(name = "region_id", nullable = false)
    private Long regionId;

    @Id
    @NotNull
    @Column(name = "district_id", nullable = false)
    private Long districtId;

    @Id
    @NotNull
    @Column(name = "locality_id", nullable = false)
    private Long localityId;

    @NotNull
    @Column(name = "code", nullable = false)
    private String code;

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

    public Long getLocalityId() {
        return localityId;
    }

    public void setLocalityId(Long localityId) {
        this.localityId = localityId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationToLocation that = (StationToLocation) o;
        return Objects.equal(regionId, that.regionId) &&
            Objects.equal(districtId, that.districtId) &&
            Objects.equal(localityId, that.localityId) &&
            Objects.equal(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(regionId, districtId, localityId, code);
    }

    static class LocationPK implements Serializable {
        private Long regionId;
        private Long districtId;
        private Long localityId;

        public Long getRegionId() {
            return regionId;
        }

        public void setRegionId(Long regionId) {
            this.regionId = regionId;
        }

        public Long getDistrictId() {
            return districtId;
        }

        public void setDistrictId(Long districtId) {
            this.districtId = districtId;
        }

        public Long getLocalityId() {
            return localityId;
        }

        public void setLocalityId(Long localityId) {
            this.localityId = localityId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LocationPK that = (LocationPK) o;
            return Objects.equal(regionId, that.regionId) &&
                Objects.equal(districtId, that.districtId) &&
                Objects.equal(localityId, that.localityId);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(regionId, districtId, localityId);
        }
    }
}
