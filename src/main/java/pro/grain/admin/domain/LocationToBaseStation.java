package pro.grain.admin.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A LocationToBaseStation.
 */
@Entity
@Table(name = "station_location")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class LocationToBaseStation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private LocationToBaseStationPK pk;

    @NotNull
    @OneToOne
    @JoinColumn(name="code", referencedColumnName = "code", nullable = false)
    private Station baseStation;

    public Station getBaseStation() {
        return baseStation;
    }

    public void setBaseStation(Station baseStation) {
        this.baseStation = baseStation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LocationToBaseStation locationToBaseStation = (LocationToBaseStation) o;
        return com.google.common.base.Objects.equal(pk, locationToBaseStation.pk);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(baseStation.getId());
    }

    @Override
    public String toString() {
        return "LocationToStation{" +
            "region='" + pk.region + "'" +
            ", district='" + pk.district + "'" +
            ", locality='" + pk.locality + "'" +
            ", baseStation='" + baseStation + "'" +
            '}';
    }

    @Embeddable
    class LocationToBaseStationPK implements Serializable {
        @NotNull
        @ManyToOne
        private Region region;

        @NotNull
        @ManyToOne
        private District district;

        @NotNull
        @ManyToOne
        private Locality locality;

        public Region getRegion() {
            return region;
        }

        public void setRegion(Region region) {
            this.region = region;
        }

        public District getDistrict() {
            return district;
        }

        public void setDistrict(District district) {
            this.district = district;
        }

        public Locality getLocality() {
            return locality;
        }

        public void setLocality(Locality locality) {
            this.locality = locality;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LocationToBaseStationPK that = (LocationToBaseStationPK) o;
            return com.google.common.base.Objects.equal(region, that.region) &&
                com.google.common.base.Objects.equal(district, that.district) &&
                com.google.common.base.Objects.equal(locality, that.locality);
        }

        @Override
        public int hashCode() {
            return com.google.common.base.Objects.hashCode(region, district, locality);
        }
    }
}

