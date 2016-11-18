package pro.grain.admin.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A DTO for the QualityValue entity.
 */
public class QualityValueDTO implements Serializable {

    private Long id;

    @NotNull
    private Long value;


    private Long qualityParameterId;


    private String qualityParameterName;

    private String qualityParameterUnit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public Long getQualityParameterId() {
        return qualityParameterId;
    }

    public void setQualityParameterId(Long qualityParameterId) {
        this.qualityParameterId = qualityParameterId;
    }


    public String getQualityParameterName() {
        return qualityParameterName;
    }

    public void setQualityParameterName(String qualityParameterName) {
        this.qualityParameterName = qualityParameterName;
    }

    public String getQualityParameterUnit() {
        return qualityParameterUnit;
    }

    public void setQualityParameterUnit(String qualityParameterUnit) {
        this.qualityParameterUnit = qualityParameterUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        QualityValueDTO qualityValueDTO = (QualityValueDTO) o;

        if ( ! Objects.equals(id, qualityValueDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "QualityValueDTO{" +
            "id=" + id +
            ", value='" + value + "'" +
            '}';
    }
}
