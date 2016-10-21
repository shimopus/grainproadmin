package pro.grain.admin.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;


/**
 * A DTO for the OrganisationType entity.
 */
public class OrganisationTypeDTO implements Serializable {

    private Long id;

    @NotNull
    private String type;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OrganisationTypeDTO organisationTypeDTO = (OrganisationTypeDTO) o;

        if ( ! Objects.equals(id, organisationTypeDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "OrganisationTypeDTO{" +
            "id=" + id +
            ", type='" + type + "'" +
            '}';
    }
}
