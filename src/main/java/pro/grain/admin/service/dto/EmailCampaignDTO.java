package pro.grain.admin.service.dto;

import java.time.ZonedDateTime;
import java.io.Serializable;
import java.util.Objects;


/**
 * A DTO for the EmailCampaign entity.
 */
public class EmailCampaignDTO implements Serializable {

    private Long id;

    private String name;

    private ZonedDateTime date;


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
    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EmailCampaignDTO emailCampaignDTO = (EmailCampaignDTO) o;

        if ( ! Objects.equals(id, emailCampaignDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "EmailCampaignDTO{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", date='" + date + "'" +
            '}';
    }
}
