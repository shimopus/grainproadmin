package pro.grain.admin.service.dto;

import pro.grain.admin.domain.Partner;

public class BidFullDTO extends BidDTO {
    private Partner elevator;

    public Partner getElevator() {
        return elevator;
    }

    public void setElevator(Partner elevator) {
        this.elevator = elevator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BidFullDTO)) return false;
        if (!super.equals(o)) return false;

        BidFullDTO that = (BidFullDTO) o;

        return getElevator() != null ? getElevator().equals(that.getElevator()) : that.getElevator() == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getElevator() != null ? getElevator().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BidFullDTO{" +
            "id=" + getId() +
            ", creationDate='" + getCreationDate() + "'" +
            ", qualityClass='" + getQualityClass() + "'" +
            ", qualityPassport='" + getQualityPassport() + "'" +
            ", volume='" + getVolume() + "'" +
            ", price='" + getPrice() + "'" +
            ", nds='" + getNds() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", archiveDate='" + getArchiveDate() + "'" +
            "  elevator=" + elevator +
            '}';
    }
}
