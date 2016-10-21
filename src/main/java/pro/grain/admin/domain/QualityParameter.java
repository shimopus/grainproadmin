package pro.grain.admin.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A QualityParameter.
 */
@Entity
@Table(name = "quality_parameter")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "qualityparameter")
public class QualityParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "available_values")
    private String availableValues;

    @Column(name = "unit")
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

    public QualityParameter name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvailableValues() {
        return availableValues;
    }

    public QualityParameter availableValues(String availableValues) {
        this.availableValues = availableValues;
        return this;
    }

    public void setAvailableValues(String availableValues) {
        this.availableValues = availableValues;
    }

    public String getUnit() {
        return unit;
    }

    public QualityParameter unit(String unit) {
        this.unit = unit;
        return this;
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
        QualityParameter qualityParameter = (QualityParameter) o;
        if(qualityParameter.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, qualityParameter.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "QualityParameter{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", availableValues='" + availableValues + "'" +
            ", unit='" + unit + "'" +
            '}';
    }
}
