package pro.grain.admin.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A PriceUpdateQueue.
 */
@Entity
@Table(name = "price_update_queue")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "priceupdatequeue")
public class PriceUpdateQueue implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "loaded", nullable = false)
    private Boolean loaded;

    @Column(name = "loading_order")
    private Long loadingOrder;

    @ManyToOne
    @NotNull
    private Station stationFrom;

    @ManyToOne
    @NotNull
    private Station stationTo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean isLoaded() {
        return loaded;
    }

    public PriceUpdateQueue loaded(Boolean loaded) {
        this.loaded = loaded;
        return this;
    }

    public void setLoaded(Boolean loaded) {
        this.loaded = loaded;
    }

    public Long getLoadingOrder() {
        return loadingOrder;
    }

    public PriceUpdateQueue loadingOrder(Long loadingOrder) {
        this.loadingOrder = loadingOrder;
        return this;
    }

    public void setLoadingOrder(Long loadingOrder) {
        this.loadingOrder = loadingOrder;
    }

    public Station getStationFrom() {
        return stationFrom;
    }

    public PriceUpdateQueue stationFrom(Station station) {
        this.stationFrom = station;
        return this;
    }

    public void setStationFrom(Station station) {
        this.stationFrom = station;
    }

    public Station getStationTo() {
        return stationTo;
    }

    public PriceUpdateQueue stationTo(Station station) {
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
        PriceUpdateQueue priceUpdateQueue = (PriceUpdateQueue) o;
        if(priceUpdateQueue.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, priceUpdateQueue.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "PriceUpdateQueue{" +
            "id=" + id +
            ", loaded='" + loaded + "'" +
            ", loadingOrder='" + loadingOrder + "'" +
            '}';
    }
}
