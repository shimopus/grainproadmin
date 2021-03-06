package pro.grain.admin.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

import pro.grain.admin.domain.enumeration.BidType;
import pro.grain.admin.domain.enumeration.QualityClass;
import pro.grain.admin.domain.enumeration.NDS;

/**
 * A DTO for the Bid entity.
 */
public class BidDTO implements Serializable {

    private Long id;

    @NotNull
    private LocalDateTime creationDate;

    @NotNull
    private QualityClass qualityClass;

    @NotNull
    private Integer volume;

    @NotNull
    private Long price;

    @NotNull
    private NDS nds;

    @NotNull
    private BidType bidType;

    private Boolean isActive;

    private LocalDateTime archiveDate;


    private Long agentContactId;

    private Set<QualityValueDTO> qualityParameters = new HashSet<>();

    private Long agentId;


    private String agentName;

    private Long elevatorId;


    private String elevatorName;

    private Set<PassportDTO> qualityPassports = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
    public QualityClass getQualityClass() {
        return qualityClass;
    }

    public void setQualityClass(QualityClass qualityClass) {
        this.qualityClass = qualityClass;
    }
    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }
    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }
    public NDS getNds() {
        return nds;
    }

    public void setNds(NDS nds) {
        this.nds = nds;
    }

    public BidType getBidType() {
        return bidType;
    }

    public void setBidType(BidType bidType) {
        this.bidType = bidType;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    public LocalDateTime getArchiveDate() {
        return archiveDate;
    }

    public void setArchiveDate(LocalDateTime archiveDate) {
        this.archiveDate = archiveDate;
    }

    public Long getAgentContactId() {
        return agentContactId;
    }

    public void setAgentContactId(Long contactId) {
        this.agentContactId = contactId;
    }

    public Set<QualityValueDTO> getQualityParameters() {
        return qualityParameters;
    }

    public void setQualityParameters(Set<QualityValueDTO> qualityValues) {
        this.qualityParameters = qualityValues;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long partnerId) {
        this.agentId = partnerId;
    }


    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String partnerName) {
        this.agentName = partnerName;
    }

    public Long getElevatorId() {
        return elevatorId;
    }

    public void setElevatorId(Long partnerId) {
        this.elevatorId = partnerId;
    }


    public String getElevatorName() {
        return elevatorName;
    }

    public void setElevatorName(String partnerName) {
        this.elevatorName = partnerName;
    }

    public Set<PassportDTO> getQualityPassports() {
        return qualityPassports;
    }

    public void setQualityPassports(Set<PassportDTO> passports) {
        this.qualityPassports = passports;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BidDTO bidDTO = (BidDTO) o;

        if ( ! Objects.equals(id, bidDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "BidDTO{" +
            "id=" + id +
            ", creationDate='" + creationDate + "'" +
            ", qualityClass='" + qualityClass + "'" +
            ", volume='" + volume + "'" +
            ", price='" + price + "'" +
            ", nds='" + nds + "'" +
            ", isActive='" + isActive + "'" +
            ", archiveDate='" + archiveDate + "'" +
            '}';
    }
}
