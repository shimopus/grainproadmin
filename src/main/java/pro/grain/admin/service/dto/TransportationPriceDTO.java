package pro.grain.admin.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A DTO for the TransportationPrice entity.
 */
public class TransportationPriceDTO implements Serializable {

    private Long id;

    @NotNull
    private Long price;


    private Long stationFromId;
    

    private String stationFromCode;

    private Long stationToId;
    

    private String stationToCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getStationFromId() {
        return stationFromId;
    }

    public void setStationFromId(Long stationId) {
        this.stationFromId = stationId;
    }


    public String getStationFromCode() {
        return stationFromCode;
    }

    public void setStationFromCode(String stationCode) {
        this.stationFromCode = stationCode;
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

    public void setStationToCode(String stationCode) {
        this.stationToCode = stationCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TransportationPriceDTO transportationPriceDTO = (TransportationPriceDTO) o;

        if ( ! Objects.equals(id, transportationPriceDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TransportationPriceDTO{" +
            "id=" + id +
            ", price='" + price + "'" +
            '}';
    }
}
