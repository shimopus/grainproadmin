package pro.grain.admin.service.dto;

import java.time.LocalDate;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

import pro.grain.admin.domain.enumeration.MailOpenType;

/**
 * A DTO for the Tracking entity.
 */
public class TrackingDTO implements Serializable {

    private Long id;

    private LocalDate mailDate;

    private MailOpenType openType;

    private Integer openCount;


    private Long partnerId;
    

    private String partnerName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public LocalDate getMailDate() {
        return mailDate;
    }

    public void setMailDate(LocalDate mailDate) {
        this.mailDate = mailDate;
    }
    public MailOpenType getOpenType() {
        return openType;
    }

    public void setOpenType(MailOpenType openType) {
        this.openType = openType;
    }
    public Integer getOpenCount() {
        return openCount;
    }

    public void setOpenCount(Integer openCount) {
        this.openCount = openCount;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }


    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TrackingDTO trackingDTO = (TrackingDTO) o;

        if ( ! Objects.equals(id, trackingDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TrackingDTO{" +
            "id=" + id +
            ", mailDate='" + mailDate + "'" +
            ", openType='" + openType + "'" +
            ", openCount='" + openCount + "'" +
            '}';
    }
}
