package pro.grain.admin.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A QualityValue.
 */
@Entity
@Table(name = "quality_value")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "qualityvalue")
public class QualityValue implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "value", nullable = false)
    private String value;

    @ManyToOne
    private QualityParameter qualityParameter;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public QualityValue value(String value) {
        this.value = value;
        return this;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public QualityParameter getQualityParameter() {
        return qualityParameter;
    }

    public QualityValue qualityParameter(QualityParameter qualityParameter) {
        this.qualityParameter = qualityParameter;
        return this;
    }

    public void setQualityParameter(QualityParameter qualityParameter) {
        this.qualityParameter = qualityParameter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QualityValue qualityValue = (QualityValue) o;
        if(qualityValue.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, qualityValue.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "QualityValue{" +
            "id=" + id +
            ", value='" + value + "'" +
            '}';
    }
}
