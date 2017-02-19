package pro.grain.admin.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A TransportationPrice.
 */
@Entity
@Table(name = "transportation_price")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "transportationprice")
public class TransportationPrice implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "transportation_price_gen")
    @SequenceGenerator(name = "transportation_price_gen", sequenceName = "transportation_price_id_seq")
    private Long id;

    @NotNull
    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "price_nds")
    private Long priceNds;

    @Column(name = "distance")
    private Integer distance;

    @Column(name = "version_number")
    private Integer versionNumber;

    @Column(name = "loading_date")
    private LocalDate loadingDate;

    @NotNull
    @OneToOne
    @JoinColumn(name="station_from_code", referencedColumnName = "code", nullable = false)
    private Station stationFrom;

    @NotNull
    @OneToOne
    @JoinColumn(name="station_to_code", referencedColumnName = "code", nullable = false)
    private Station stationTo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrice() {
        return price;
    }

    public TransportationPrice price(Long price) {
        this.price = price;
        return this;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getPriceNds() {
        return priceNds;
    }

    public TransportationPrice priceNds(Long priceNds) {
        this.priceNds = priceNds;
        return this;
    }

    public void setPriceNds(Long priceNds) {
        this.priceNds = priceNds;
    }

    public Integer getDistance() {
        return distance;
    }

    public TransportationPrice distance(Integer distance) {
        this.distance = distance;
        return this;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public LocalDate getLoadingDate() {
        return loadingDate;
    }

    public void setLoadingDate(LocalDate loadingDate) {
        this.loadingDate = loadingDate;
    }

    public Station getStationFrom() {
        return stationFrom;
    }

    public TransportationPrice stationFrom(Station station) {
        this.stationFrom = station;
        return this;
    }

    public void setStationFrom(Station station) {
        this.stationFrom = station;
    }

    public Station getStationTo() {
        return stationTo;
    }

    public TransportationPrice stationTo(Station station) {
        this.stationTo = station;
        return this;
    }

    public void setStationTo(Station station) {
        this.stationTo = station;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TransportationPrice transportationPrice = (TransportationPrice) o;
        if(transportationPrice.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, transportationPrice.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TransportationPrice{" +
            "id=" + id +
            ", price='" + price + "'" +
            ", priceNds='" + priceNds + "'" +
            ", distance='" + distance + "'" +
            '}';
    }
}
