package pro.grain.admin.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A DTO for the PriceUpdateQueue entity.
 */
public class PriceUpdateQueueDTO implements Serializable {

    private Long id;

    @NotNull
    private Boolean loaded;

    private Long loadingOrder;

    private Long stationFromId;

    private String stationFromName;

    private String stationFromCode;

    private Long stationToId;

    private String stationToName;

    private String stationToCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Boolean getLoaded() {
        return loaded;
    }

    public void setLoaded(Boolean loaded) {
        this.loaded = loaded;
    }
    public Long getLoadingOrder() {
        return loadingOrder;
    }

    public void setLoadingOrder(Long loadingOrder) {
        this.loadingOrder = loadingOrder;
    }

    public Long getStationFromId() {
        return stationFromId;
    }

    public void setStationFromId(Long stationId) {
        this.stationFromId = stationId;
    }

    public String getStationFromName() {
        return stationFromName;
    }

    public void setStationFromName(String stationName) {
        this.stationFromName = stationName;
    }

    public String getStationFromCode() {
        return stationFromCode;
    }

    public void setStationFromCode(String stationFromCode) {
        this.stationFromCode = stationFromCode;
    }

    public Long getStationToId() {
        return stationToId;
    }

    public void setStationToId(Long stationId) {
        this.stationToId = stationId;
    }

    public String getStationToCode() {
        return stationToCode;
    }

    public void setStationToCode(String stationToCode) {
        this.stationToCode = stationToCode;
    }

    public String getStationToName() {
        return stationToName;
    }

    public void setStationToName(String stationName) {
        this.stationToName = stationName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PriceUpdateQueueDTO priceUpdateQueueDTO = (PriceUpdateQueueDTO) o;

        if ( ! Objects.equals(id, priceUpdateQueueDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "PriceUpdateQueueDTO{" +
            "id=" + id +
            ", loaded='" + loaded + "'" +
            ", loadingOrder='" + loadingOrder + "'" +
            '}';
    }
}
