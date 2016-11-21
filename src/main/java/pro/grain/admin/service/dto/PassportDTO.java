package pro.grain.admin.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Lob;


/**
 * A DTO for the Passport entity.
 */
public class PassportDTO implements Serializable {

    private Long id;

    @NotNull
    @Lob
    private byte[] image;

    private String imageContentType;
    private String title;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PassportDTO passportDTO = (PassportDTO) o;

        if ( ! Objects.equals(id, passportDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "PassportDTO{" +
            "id=" + id +
            ", image='" + image + "'" +
            ", title='" + title + "'" +
            '}';
    }
}
