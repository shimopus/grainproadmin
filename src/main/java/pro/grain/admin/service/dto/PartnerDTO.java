package pro.grain.admin.service.dto;

import java.time.LocalDate;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

import pro.grain.admin.domain.enumeration.NDS;

/**
 * A DTO for the Partner entity.
 */
public class PartnerDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private String shortName;

    private String inn;

    private NDS nds;

    private String card;

    private String site;

    private LocalDate lastUpdate;


    private Long organisationTypeId;


    private String organisationTypeType;

    private Long districtId;


    private String districtName;

    private Long regionId;


    private String regionName;

    private Long localityId;


    private String localityName;

    private Long stationId;


    private String stationCode;

    private String baseStationCode;

    private String stationName;

    private Set<ContactDTO> contacts = new HashSet<>();

    private Set<ServicePriceDTO> servicePrices = new HashSet<>();

    private Set<PartnerDTO> ownedBies = new HashSet<>();

    private Long ownerForId;


    private String ownerForName;

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
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }
    public NDS getNds() {
        return nds;
    }

    public void setNds(NDS nds) {
        this.nds = nds;
    }
    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public LocalDate getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDate lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getOrganisationTypeId() {
        return organisationTypeId;
    }

    public void setOrganisationTypeId(Long organisationTypeId) {
        this.organisationTypeId = organisationTypeId;
    }


    public String getOrganisationTypeType() {
        return organisationTypeType;
    }

    public void setOrganisationTypeType(String organisationTypeType) {
        this.organisationTypeType = organisationTypeType;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }


    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }


    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public Long getLocalityId() {
        return localityId;
    }

    public void setLocalityId(Long localityId) {
        this.localityId = localityId;
    }


    public String getLocalityName() {
        return localityName;
    }

    public void setLocalityName(String localityName) {
        this.localityName = localityName;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public String getBaseStationCode() {
        return baseStationCode;
    }

    public void setBaseStationCode(String baseStationCode) {
        this.baseStationCode = baseStationCode;
    }

    public Set<ContactDTO> getContacts() {
        return contacts;
    }

    public void setContacts(Set<ContactDTO> contacts) {
        this.contacts = contacts;
    }

    public Set<ServicePriceDTO> getServicePrices() {
        return servicePrices;
    }

    public void setServicePrices(Set<ServicePriceDTO> servicePrices) {
        this.servicePrices = servicePrices;
    }

    public Set<PartnerDTO> getOwnedBies() {
        return ownedBies;
    }

    public void setOwnedBies (Set<PartnerDTO> ownedBy) {
        this.ownedBies = ownedBy;
    }

    public Long getOwnerForId() {
        return ownerForId;
    }

    public void setOwnerForId(Long partnerId) {
        this.ownerForId = partnerId;
    }


    public String getOwnerForName() {
        return ownerForName;
    }

    public void setOwnerForName(String partnerName) {
        this.ownerForName = partnerName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PartnerDTO partnerDTO = (PartnerDTO) o;

        if ( ! Objects.equals(id, partnerDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "PartnerDTO{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", shortName='" + shortName + "'" +
            ", inn='" + inn + "'" +
            ", nds='" + nds + "'" +
            ", card='" + card + "'" +
            ", lastUpdate='" + lastUpdate + "'" +
            '}';
    }
}
