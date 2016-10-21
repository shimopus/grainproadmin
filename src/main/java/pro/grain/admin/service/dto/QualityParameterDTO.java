package pro.grain.admin.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;


/**
 * A DTO for the QualityParameter entity.
 */
public class QualityParameterDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private String availableValues;

    private String unit;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getAvailableValues() {
        return availableValues;
    }

    public void setAvailableValues(String availableValues) {
        this.availableValues = availableValues;
    }
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        QualityParameterDTO qualityParameterDTO = (QualityParameterDTO) o;

        if ( ! Objects.equals(id, qualityParameterDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "QualityParameterDTO{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", availableValues='" + availableValues + "'" +
            ", unit='" + unit + "'" +
            '}';
    }
}
